package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Topic;

import java.util.List;

/**
 * 实现一个接口用于话题数据访问的定义
 */
public interface TopicDao {

    /**
     * 插入一个新的话题
     *
     * @param topic  新的话题
     * @param userId 发起插入的用户ID
     * @return 插入的话题
     */
    Topic insert(Topic topic, Integer userId);

    /**
     * 删除一个已存在的话题
     *
     * @param topicId 需要删除的话题ID
     */
    void delete(Integer topicId);

    /**
     * 根据话题ID查询话题
     *
     * @param topicId     话题的ID
     * @param queryUserId 发起查询的用户ID
     * @return 查询到的话题
     */
    Topic queryTopicById(Integer topicId, Integer queryUserId);

    /**
     * 根据用户ID，分页信息，排序字段和排序方式查询话题列表
     *
     * @param userId      用户ID
     * @param page        页数
     * @param pageSize    每页数量
     * @param orderBy     排序字段
     * @param asc         是否升序
     * @param queryUserId 发起查询的用户ID
     * @return 查询到的话题列表
     */
    List<Topic> queryTopics(Integer userId, Integer page, Integer pageSize, String orderBy, Boolean asc, Integer queryUserId);

    /**
     * 根据用户ID计数
     *
     * @param userId 用户ID
     * @return 用户的话题数
     */
    Integer count(Integer userId);
}