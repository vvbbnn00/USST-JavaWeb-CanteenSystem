package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.UserNotification;

import java.util.List;

public interface UserNotificationDao {
    /**
     * 插入一条通知
     * 该函数用于在数据库中插入一条新的通知记录
     *
     * @param notification 通知，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     * @return 是否成功插入新的记录
     */
    boolean insert(UserNotification notification);

    /**
     * 获取指定用户的通知列表
     * 该函数用于获取指定用户的最新200条通知
     *
     * @param userId 用户ID
     * @param isRead 是否已读
     * @return 通知列表
     */
    List<UserNotification> queryByUserId(Integer userId, Boolean isRead);

    /**
     * 更新通知已读状态
     * 该函数用于更新指定的通知的已读状态
     *
     * @param notificationId 通知ID
     * @param isRead         新的isRead状态
     * @param userId         用户ID
     * @return 是否成功更新已读状态
     */
    boolean update(Integer notificationId, Boolean isRead, Integer userId);

    /**
     * 获取用户未读通知数量
     * 该函数用于获取指定用户的未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int count(Boolean isRead, Integer userId);

    /**
     * 删除通知
     * 该函数用于删除指定的通知
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     */
    void delete(Integer notificationId, Integer userId);
}