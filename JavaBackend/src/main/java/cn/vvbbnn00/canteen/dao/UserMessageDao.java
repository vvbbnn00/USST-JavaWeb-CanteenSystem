package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.dto.response.UserMessageCount;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserMessage;

import java.util.List;

public interface UserMessageDao {
    /**
     * 插入一条消息
     *
     * @param message 消息，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    boolean insert(UserMessage message);
    /**
     * 查询最近200条消息的用户列表
     * @param UserId 相关用户id
     * @return 消息列表
     */
    List<UserMessageCount> queryMessagedUsers(Integer UserId);
    /**
     * 查询最近200条消息
     * @param fromUserId 互相发送消息的用户id
     * @param toUserId 互相发送消息的用户id
     * @return 消息列表数量
     */
     List<UserMessage> queryMessagesList(Integer toUserId, Integer fromUserId);
}
