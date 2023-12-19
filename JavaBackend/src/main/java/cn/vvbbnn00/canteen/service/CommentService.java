package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.CommentDao;
import cn.vvbbnn00.canteen.dao.impl.CommentDaoImpl;
import cn.vvbbnn00.canteen.model.Comment;
import cn.vvbbnn00.canteen.model.Complaint;
import cn.vvbbnn00.canteen.model.Topic;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.TagUtils;

import java.util.List;

public class CommentService {
    private static final CommentDao commentDao = new CommentDaoImpl();
    private static final CanteenService canteenService = new CanteenService();
    private static final ItemService itemService = new ItemService();
    private static final TopicService topicService = new TopicService();
    private static final ComplaintService complaintService = new ComplaintService();
    private static final UserService userService = new UserService();
    private static final UserPointLogService userPointLogService = new UserPointLogService();
    private static final UserNotificationService userNotificationService = new UserNotificationService();

    /**
     * 检查评论是否合法
     *
     * @param comment 评论
     */
    private void checkComment(Comment comment, Integer userId) {
        if (comment.getContent() == null || comment.getContent().isEmpty()) {
            throw new RuntimeException("评论内容不能为空");
        }
        Integer referenceId = comment.getReferenceId();
        if (referenceId == null) {
            throw new RuntimeException("评论引用id不能为空");
        }
        Comment.CommentType type = comment.getType();
        switch (type) {
            case canteen:
                if (canteenService.getCanteenById(referenceId) == null) {
                    throw new RuntimeException("食堂不存在");
                }
                if (comment.getScore() == null && comment.getParentId() == null) {
                    throw new RuntimeException("评分不能为空");
                }
                userPointLogService.changeUserPoint(userId, 3, "评论食堂" + referenceId);
                break;
            case topic:
                Topic topic = topicService.getTopicById(referenceId, userId);
                if (topic == null) {
                    throw new RuntimeException("话题不存在");
                }
                userPointLogService.changeUserPoint(userId, 3, "评论话题" + referenceId);
                userNotificationService.addUserNotification(
                        topic.getCreatedBy(),
                        "用户" +
                                TagUtils.generateTag(userService.getUserById(userId)) +
                                "评论了你的话题" +
                                TagUtils.generateTag(topic) +
                                "。");
                comment.setScore(null);
                break;
            case complaint:
                Complaint complaint = complaintService.getComplaintById(referenceId);
                if (complaint == null) {
                    throw new RuntimeException("投诉不存在");
                }
                if (complaint.getStatus() == Complaint.Status.finished) {
                    throw new RuntimeException("投诉已结束");
                }
                comment.setScore(null);
                comment.setParentId(null);
                break;
            case item:
                if (itemService.getItemById(referenceId) == null) {
                    throw new RuntimeException("菜品不存在");
                }
                if (comment.getScore() == null) {
                    throw new RuntimeException("评分不能为空");
                }
                userPointLogService.changeUserPoint(userId, 3, "评论菜品" + referenceId);
                break;
            default:
                throw new RuntimeException("评论类型错误");
        }

        Integer parentId = comment.getParentId();
        if (parentId != null) {
            Comment parentComment = commentDao.queryCommentById(parentId);
            if (parentComment == null) {
                throw new RuntimeException("父评论不存在");
            }
            if (!parentComment.getType().equals(type)) {
                throw new RuntimeException("父评论类型错误");
            }
            if (!parentComment.getReferenceId().equals(referenceId)) {
                throw new RuntimeException("父评论引用id错误");
            }
        }
    }

    /**
     * 插入一个评论
     *
     * @param comment 评论，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    public void createComment(Comment comment, Integer userId) {
        checkComment(comment, userId);
        try {
            boolean success = commentDao.insert(comment);
            if (!success) {
                throw new RuntimeException("创建评论失败");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 根据id获取评论
     *
     * @param id 评论id
     * @return 评论
     */
    public Comment getCommentById(Integer id) {
        return commentDao.queryCommentById(id);
    }

    /**
     * 根据id删除评论
     *
     * @param id 评论id
     */
    public void deleteComment(Integer id, Integer userId) {
        Comment comment = commentDao.queryCommentById(id);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!comment.getCreatedBy().equals(userId) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("您没有权限删除该评论");
        }
        if (comment.getType().equals(Comment.CommentType.complaint) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("投诉评论不能删除");
        }
        try {
            boolean success = commentDao.delete(id);
            if (!success) {
                throw new RuntimeException("删除评论失败");
            }
        } catch (Exception e) {
            throw new RuntimeException("删除评论失败");
        }
        userPointLogService.changeUserPoint(userId, -3, "删除评论" + id);
    }

    /**
     * 查询评论列表
     *
     * @param kw          关键词，模糊匹配评论内容 支持:content,reference_id,parent_id
     * @param type        评论类型，支持：canteen、cuisine、order
     * @param referenceId 评论的引用id，支持：食堂id、菜品id、订单id
     * @param parentId    父评论id
     * @param page        分页查询的页码
     * @param pageSize    分页查询的每页数量
     * @param orderBy     排序字段，支持：id、name、createdAt、updatedAt、Score
     * @param asc         是否升序
     * @return 评论列表
     */
    public List<Comment> getCommentList(String kw, Comment.CommentType type, Integer referenceId, Integer parentId,
                                        Integer page, Integer pageSize, String orderBy, Boolean asc) {
        return commentDao.queryComments(
                kw,
                type.toString(),
                referenceId,
                parentId,
                page,
                pageSize,
                orderBy,
                asc
        );
    }

    /**
     * 获取评论列表数量
     *
     * @param kw          关键词
     * @param type        评论类型,支持：canteen、cuisine、order
     * @param referenceId 评论的引用id，支持：食堂id、菜品id、订单id
     * @param parentId    父评论id
     * @return 评论列表数量
     */
    public int getCommentListCount(String kw, Comment.CommentType type, Integer referenceId, Integer parentId) {
        return commentDao.queryCommentsCount(
                kw,
                type.toString(),
                referenceId,
                parentId
        );
    }
}
