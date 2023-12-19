import pymysql
from config import DB_HOST, DB_PORT, DB_USERNAME, DB_PASSWORD, DB_NAME

connection = pymysql.connect(host=DB_HOST,
                             port=int(DB_PORT),
                             user=DB_USERNAME,
                             password=DB_PASSWORD,
                             db=DB_NAME,
                             charset='utf8mb4',
                             cursorclass=pymysql.cursors.DictCursor)


def getCommentsToAudit():
    with connection.cursor() as cursor:
        sql = "SELECT * FROM comment WHERE weight is NULL"
        cursor.execute(sql)
        result = cursor.fetchall()
        return result


def changeUserPoint(userId, delta, detail):
    with connection.cursor() as cursor:
        sql = "UPDATE user SET point = point + %s WHERE user_id = %s"
        cursor.execute(sql, (delta, userId))
        sql = "INSERT INTO user_point_log (user_id, point, detail) VALUES (%s, %s, %s)"
        cursor.execute(sql, (userId, delta, detail))
        connection.commit()


def sendUserNotification(userId, content):
    with connection.cursor() as cursor:
        sql = "INSERT INTO user_notification (user_id, content) VALUES (%s, %s)"
        cursor.execute(sql, (userId, content))
        connection.commit()


def updateCommentWeight(commentId, weight, flagged=False):
    with connection.cursor() as cursor:
        sql = "UPDATE comment SET weight = %s"
        if flagged:
            sql += ", flagged = 1"
        sql += " WHERE comment_id = %s"
        cursor.execute(sql, (weight, commentId))
        connection.commit()


def getUser(userId):
    with connection.cursor() as cursor:
        sql = "SELECT level, is_verified FROM canteen_community.user WHERE user_id = %s"
        cursor.execute(sql, (userId,))
        result = cursor.fetchone()
        return result


def getCanteenByItem(item):
    with connection.cursor() as cursor:
        sql = ("SELECT canteen_id FROM canteen_community.cuisine WHERE cuisine_id in "
               "(SELECT cuisine_id FROM canteen_community.item WHERE item_id = %s)")
        cursor.execute(sql, (item,))
        result = cursor.fetchone()
        return result


if __name__ == '__main__':
    print(getUser(1))
