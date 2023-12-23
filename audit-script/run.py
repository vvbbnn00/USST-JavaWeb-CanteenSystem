import json
import os
import time

from database import *
from openai import *

logging.basicConfig(level=logging.INFO)

# 一个映射级别和权重的字典
LEVEL_MAP = {
    1: 0.5,
    2: 0.75,
    3: 1,
    4: 1.26,
    5: 1.53,
    6: 1.81,
    7: 2.10,
    8: 2.39,
    9: 2.69,
    10: 3.00,
    11: 3.53,
    12: 3.96,
    13: 4.35,
    14: 4.57,
    15: 4.75,
    16: 4.89,
    17: 4.95,
    18: 5,
}


# 确保所有评论被审查
def auditComments():
    commentList = getCommentsToAudit()
    global itemIdToUpdate, canteenIdToUpdate

    # 对于列表中的每一条评论
    for comment in commentList:
        logging.info(comment)
        comment = dict(comment)
        commentId = comment.get('comment_id')
        referenceId = comment.get('reference_id')
        userId = comment.get('created_by')
        content = comment.get('content')
        contentType = comment.get('type')
        # 如果评论类型是投诉
        if contentType == 'complaint':
            logging.info(f"Complaint {commentId} ignored, for it is not a comment for an item or a canteen")
            updateCommentWeight(commentId, -1, False)
            continue

        # 获取用户数据
        user = getUser(userId)
        if user is None:
            logging.warning(f"User {userId} not found, comment {commentId} ignored")
            # updateCommentWeight(commentId, 0, False)
            continue

        # 获取审核结果
        auditResult = get_text_moderation(content)
        if not auditResult.get('success'):
            logging.warning(f"OpenAI error: {json.dumps(auditResult)}")
            continue
        flagged = auditResult.get('flagged')
        message = f"您的评论“{content[0]}******{content[-1]}”(Id: {commentId})由于存在不当的发言已被标记，您的账号被扣除5点积分，如有疑问请联系管理员。"

        level = user.get('level')
        isVerified = user.get('is_verified')

        weight = LEVEL_MAP.get(level, 0.5)
        if isVerified:
            weight *= 1.25
        # 计算权重，并保证权重不超过5
        if weight > 5:
            weight = 5
        # 如果评论被标记，发送消息和修改积分
        if flagged:
            weight = weight * 0.1

        logging.info("Content weight:" + str(weight))

        if contentType == 'item':
            itemIdToUpdate.add(referenceId)
            canteen = getCanteenByItem(referenceId)
            if canteen:
                canteenIdToUpdate.add(canteen.get('canteen_id'))
        elif contentType == 'canteen':
            canteenIdToUpdate.add(referenceId)

        updateCommentWeight(commentId, weight, flagged)
        if flagged:
            changeUserPoint(userId, -5, message)
            sendUserNotification(userId, message)

        time.sleep(1)


# 计算项目得分
def calcItemScore(itemId):
    with connection.cursor() as cursor:
        sql = """
SELECT 
    created_by,
    AVG(score) as avg_score,
    MAX(weight) as max_weight
FROM 
    (SELECT 
        created_by, 
        score,
        weight,
        RANK() OVER (PARTITION BY created_by ORDER BY weight DESC) as weight_rank
    FROM 
        comment
    WHERE 
        type = 'item' AND 
        reference_id = %s AND 
        weight IS NOT NULL
    ) as ranked_comments
WHERE 
    weight_rank = 1
GROUP BY 
    created_by;
        """
        cursor.execute(sql, (itemId,))
        result = cursor.fetchall()
        total = 0
        count = 0
        for row in result:
            avgScore = row.get('avg_score')
            maxWeight = row.get('max_weight')
            if avgScore is None:
                avgScore = 0
            if maxWeight is None:
                maxWeight = 0
            count += float(avgScore) * float(maxWeight)
            total += float(maxWeight) * 5
        score = count / total * 5
        return score


# 计算食堂得分的第一部分
def calcCanteenScorePart1(canteenId):
    with connection.cursor() as cursor:
        sql = """
SELECT 
    created_by,
    AVG(score) as avg_score,
    MAX(weight) as max_weight
FROM 
    (SELECT 
        created_by, 
        score,
        weight,
        RANK() OVER (PARTITION BY created_by ORDER BY weight DESC) as weight_rank
    FROM 
        comment
    WHERE 
        type = 'canteen' AND 
        reference_id = %s AND 
        weight IS NOT NULL
    ) as ranked_comments
WHERE 
    weight_rank = 1
GROUP BY 
    created_by;
            """
        cursor.execute(sql, (canteenId,))
        result = cursor.fetchall()
        total = 0
        count = 0
        for row in result:
            avgScore = row.get('avg_score')
            maxWeight = row.get('max_weight')
            if avgScore is None:
                avgScore = 0
            if maxWeight is None:
                maxWeight = 0
            count += float(avgScore) * float(maxWeight)
            total += float(maxWeight) * 5
        if total == 0:
            return 0
        score = count / total * 5
        return score


# 计算食堂得分的第二部分
def calcCanteenScorePart2(canteenId):
    with connection.cursor() as cursor:
        sql = """
SELECT AVG(comp_score) FROM 
(SELECT comp_score
FROM item
LEFT JOIN cuisine ON cuisine.cuisine_id = item.cuisine_id
WHERE cuisine.canteen_id = %s AND item.comp_score > 0) AS sel;
"""
        cursor.execute(sql, (canteenId,))
        result = cursor.fetchone()
        return result.get('AVG(comp_score)')


# 计算食堂得分
def calcCanteenScore(canteenId):
    part1 = calcCanteenScorePart1(canteenId) or 0
    part2 = calcCanteenScorePart2(canteenId) or 0
    part1 = float(part1)
    part2 = float(part2)
    if part1 == 0:
        return part2
    if part2 == 0:
        return part1
    return part1 * 0.6 + part2 * 0.4


# 主函数
def main():
    auditComments()

    # 更新每个项目的分数
    for itemId in itemIdToUpdate:
        score = calcItemScore(itemId)
        with connection.cursor() as cursor:
            sql = "UPDATE item SET comp_score = %s WHERE item_id = %s"
            cursor.execute(sql, (score, itemId))
            connection.commit()
        logging.info(f"Item {itemId} updated, score: {score}")

    # 更新每个食堂的分数
    for canteenId in canteenIdToUpdate:
        score = calcCanteenScore(canteenId)
        with connection.cursor() as cursor:
            sql = "UPDATE canteen SET comp_score = %s WHERE canteen_id = %s"
            cursor.execute(sql, (score, canteenId))
            connection.commit()
        logging.info(f"Canteen {canteenId} updated, score: {score}")


if __name__ == '__main__':
    if os.name != 'nt':
        import fcntl
        import sys

        lockfile = open("lockfile.lock", 'w')

        try:
            # 尝试获取文件锁
            fcntl.flock(lockfile, fcntl.LOCK_EX | fcntl.LOCK_NB)
        except IOError:
            # 如果锁已经被其他进程持有，则退出
            logging.warning("Another instance is running. Exiting.")
            sys.exit(0)
    else:
        logging.warning("Windows system detected. Lock not acquired.")

    logging.info("Audit script started.")

    itemIdToUpdate = set()
    canteenIdToUpdate = set()

    main()

    logging.info("Audit script finished.")
