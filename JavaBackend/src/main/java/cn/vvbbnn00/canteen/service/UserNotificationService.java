package cn.vvbbnn00.canteen.service;


import cn.vvbbnn00.canteen.dao.UserNotificationDao;
import cn.vvbbnn00.canteen.dao.impl.UserNotificationDaoImpl;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserNotification;

import java.util.List;

public class UserNotificationService {
    private final UserNotificationDao notificationDao = new UserNotificationDaoImpl();

    /**
     * 获取通知列表
     *
     * @param unread   是否未读
     * @param UserId   接收者id
     * @return 通知列表
     */
    public List<UserNotification> getUserNotificationList(Boolean unread,Integer UserId) {
        return notificationDao.queryByUserId(unread,UserId);
    }

    /**
     * 增加通知
     * @param UserId   接收者id
     * @param content 通知内容
     * @return 通知列表
     */

    public int addUserNotification(Integer UserId, String content) {
        UserNotification notification = new UserNotification();
        notification.setUserId(UserId);
        notification.setContent(content);
        notification.setIsRead(false);
        return notificationDao.insert(notification);
    }
    /**
     * 更新通知的isRead状态
     *
     * @param notificationId 通知ID
     * @param isRead 新的isRead状态
     */
    public void readUserNotification(Integer notificationId, Boolean isRead) {
        notificationDao.batchUpdate(notificationId, isRead);
    }
    public readAllUserNotification(Integer UserId) {
        notificationDao.batchUpdate(UserId);
    }