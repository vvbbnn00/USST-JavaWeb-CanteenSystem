package cn.vvbbnn00.canteen.dao;
import cn.vvbbnn00.canteen.model.Comment;

import java.util.List;

public interface CommentDao {
    /**
     * 插入一个评论
     *
     * @param comment 评论，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     * @return 是否成功
     */
    boolean insert(Comment comment);

    /**
     * 根据id查询评论
     *
     * @param id 评论id
     * @return 评论
     */
    Comment queryCommentById(Integer id);

    /**
     * 根据id删除评论
     *
     * @param id 评论id
     * @return 是否成功
     */
    boolean delete(Integer id);

    /**
     * 查询评论列表，参数为空可忽略这个条件
     *
     * @param kw       关键词，模糊匹配评论内容 支持:content,reference_id,parent_id
     * @param type     评论类型，支持：canteen、cuisine、order
     * @param orderBy  排序字段，支持：id、name、createdAt、updatedAt、Score
     * @param asc      是否升序
     * @return 评论列表
     */
    List<Comment> queryComments(Integer page, Integer pageSize, String kw, String type, String orderBy, Boolean asc);

    /**
     * 查询评论数量，参数为空可忽略这个条件
     *
     * @param kw 关键词，模糊匹配评论内容
     * @return 评论数量
     */
    Integer queryCommentsCount(String kw,String type);
}
