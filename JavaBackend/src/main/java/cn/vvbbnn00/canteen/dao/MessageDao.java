package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.UserMessage;

import java.util.List;

public interface MessageDao {
    /**
     * 插入一条消息
     *
     * @param message 消息，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    boolean insert(UserMessage message);
    /**
     * 删除一条消息
     * @param id 消息id
     * @return 是否成功
     */
    boolean delete(Integer id);
    /**
     * 查询消息列表，参数为空可忽略这个条件
     * 最多返回200条
     * @param toUserId 接收者id
     *
     * @return 消息列表
     */
    List<UserMessage> queryMessages(Integer toUserId);
    /**
     * 查询消息列表数量，参数为空可忽略这个条件
     * @param toUserId 接收者id
     *
     * @return 消息列表数量
     */
    int queryMessagesCount(Integer toUserId);

    UserMessage queryMessageById(Integer messageId);
}
