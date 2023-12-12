package cn.vvbbnn00.canteen.service;
import cn.vvbbnn00.canteen.dao.CommentDao;
import cn.vvbbnn00.canteen.dao.impl.CommentDaoImpl;
import cn.vvbbnn00.canteen.model.Comment;

import java.util.List;

public class CommentService {
    private final CommentDao commentDao = new CommentDaoImpl();

    /**
     * 插入一个评论
     *
     * @param comment 评论，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     * @return 是否成功
     */
    public boolean createComment(Comment comment) {
        if (comment.getScore() == null) {
            throw new RuntimeException("评分不能为空");
        }
        if (comment.getContent() == null || comment.getContent().isEmpty()) {
            throw new RuntimeException("评论内容不能为空");
        }
        if (comment.getContent().length() > 300) {
            throw new RuntimeException("评论内容长度不能超过300个字符");
        }
        return commentDao.insert(comment);
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
     * @return 是否成功
     */
    public boolean deleteComment(Integer id) {
        return commentDao.delete(id);
    }

    /**
     * 查询评论列表
     *
     * @param kw       关键词
     * @param type     评论类型
     * @param orderBy  排序字段
     * @param asc      是否升序
     * @return 评论列表
     */
    public List<Comment> getCommentList(Integer page, Integer pageSize, String kw, String type, String orderBy, Boolean asc) {
        return commentDao.queryComments(
                page,
                pageSize,
                kw,
                type,
                orderBy,
                asc
        );
    }

    /**
     * 获取评论列表数量
     *
     * @param kw 关键词
     * @param type 评论类型
     * @return 评论列表数量
     */
    public int getCommentListCount(String kw,String type) {
        return commentDao.queryCommentsCount(
                kw,
                type
        );
    }
}
