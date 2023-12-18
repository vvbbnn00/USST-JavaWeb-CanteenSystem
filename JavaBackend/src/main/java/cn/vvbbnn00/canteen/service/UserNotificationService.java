package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.UserNotificationDao;
import cn.vvbbnn00.canteen.dao.impl.UserNotificationDaoImpl;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserNotification;

import java.util.List;

public class UserNotificationService {
    private final UserNotificationDao userNotificationDao = new UserNotificationDaoImpl();

    /**
     * 根据用户ID和阅读状态获取用户通知列表
     *
     * @param isRead 阅读状态
     * @param userId 用户ID
     * @return 用户通知列表
     */
    public List<UserNotification> getUserNotificationList(Boolean isRead, Integer userId) {
        return userNotificationDao.queryByUserId(userId, isRead);
    }

    /**
     * 添加用户通知
     *
     * @param UserId  用户ID
     * @param content 通知内容
     */
    public void addUserNotification(Integer UserId, String content) {
        UserNotification notification = new UserNotification();
        notification.setUserId(UserId);
        notification.setContent(content);
        notification.setIsRead(false);
        try {
            boolean result = userNotificationDao.insert(notification);
            if (!result) {
                throw new RuntimeException("添加通知失败");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 读取用户通知
     *
     * @param userId         用户ID
     * @param notificationId 通知ID
     */
    public void readUserNotification(Integer userId, Integer notificationId) {
        userNotificationDao.update(notificationId, true, userId);
    }

    /**
     * 读取所有用户通知
     *
     * @param UserId 用户ID
     */
    public void readAllUserNotification(Integer UserId) {
        userNotificationDao.update(null, true, UserId);
    }


    /**
     * 获取用户未读通知数量
     *
     * @param userId 用户ID
     * @return 未读通知数量
     */
    public int count(Integer userId) {
        return userNotificationDao.count(false, userId);
    }

    /**
     * 删除通知
     *
     * @param notificationId 通知ID
     * @param userId         用户ID
     */
    public void deleteUserNotification(Integer notificationId, Integer userId) {
        userNotificationDao.delete(notificationId, userId);
    }
}