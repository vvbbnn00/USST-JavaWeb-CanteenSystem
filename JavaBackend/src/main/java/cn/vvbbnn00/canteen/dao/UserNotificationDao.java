package cn.vvbbnn00.canteen.dao;
import cn.vvbbnn00.canteen.model.UserNotification;

import java.util.List;

public interface UserNotificationDao {
    /**
     * 插入一条通知
     *
     * @param notification 通知，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    boolean insert(UserNotification notification);

        /**
         * 更新通知的isRead状态
         *
         * @param notificationId 通知ID
         * @param isRead 新的isRead状态
         */
        void batchUpdate(Integer notificationId, Boolean isRead, Integer userId);

    /**
     * 查询最近200条用户的通知列表
     *
     * @param userId 用户ID
     * @return 通知列表
     */
    List<UserNotification> queryByUserId(Integer userId);

    boolean update(Integer notificationId, Boolean isRead, Integer userId);

    /**
     * 查询用户的未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    int count(Boolean isRead, Integer userId);
}
