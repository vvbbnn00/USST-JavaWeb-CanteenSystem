package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.TopicLike;

/**
 * 主题喜欢数据访问对象接口
 * 这个接口定义了和主题相关的喜欢操作
 */
public interface TopicLikeDao {

    /**
     * 插入主题喜欢
     * 这个函数用于插入主题喜欢
     *
     * @param topicLike 主题喜欢对象
     * @return 插入成功返回true，否则返回false
     */
    boolean insert(TopicLike topicLike);

    /**
     * 删除主题喜欢
     * 这个函数用于删除主题喜欢
     *
     * @param topicLike 主题喜欢对象
     * @return 删除成功返回true，否则返回false
     */
    boolean delete(TopicLike topicLike);

    /**
     * 通过用户ID和主题ID查询主题喜欢
     *
     * @param userId  用户ID
     * @param topicId 主题ID
     * @return 查询到的主题喜欢，如果没有找到则返回null
     */
    Boolean queryTopicLikeById(Integer userId, Integer topicId);

    /**
     * 通过主题ID计算主题喜欢的数量
     *
     * @param topicId 主题ID
     * @return 主题喜欢的数量
     */
    Integer countTopicLikeById(Integer topicId);
}