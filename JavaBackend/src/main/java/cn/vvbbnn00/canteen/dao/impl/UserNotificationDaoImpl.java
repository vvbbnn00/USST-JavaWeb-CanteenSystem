package cn.vvbbnn00.canteen.dao.impl;


import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.UserNotificationDao;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserNotification;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserNotificationDaoImpl implements UserNotificationDao {

    @Override
    public boolean insert(UserNotification notification) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, notification, new String[]{
                    "userId", "content"
            });

            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                return true;
            } else {
                // Handle the case where no key was generated
                throw new SQLException("Insertion failed, no ID obtained.");
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public List<UserNotification> queryByUserId(Integer userId, Boolean isRead) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(UserNotification.class, new String[]{
                    "notificationId", "userId", "content", "isRead", "createdAt", "updatedAt"
            }) + " WHERE `user_id` = ? ";
            if (isRead != null) {
                sql += "AND `is_read` = ? ";
            }
            sql += " ORDER BY `created_at` DESC";
            sql += " LIMIT 200";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            if (isRead != null) {
                ps.setBoolean(2, isRead);
            }
            ResultSet rs = ps.executeQuery();
            List<UserNotification> notifications = new ArrayList<>();
            while (rs.next()) {
                notifications.add((UserNotification) SqlStatementUtils.makeEntityFromResult(rs, UserNotification.class));
            }
            return notifications;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }

    private static class ConditionAndParam {
        List<String> conditions;
        List<Object> params;

        ConditionAndParam(Integer notificationId, Boolean isRead) {
            conditions = new ArrayList<>();
            params = new ArrayList<>();
            if (notificationId != null) {
                conditions.add("`notification_id` = ?");
                params.add(notificationId);
            }
            if (isRead != null) {
                conditions.add("`is_read` = ?");
                params.add(isRead);
            }
        }
    }


    @Override
    public boolean update(Integer notificationId, Boolean isRead, Integer userId) {
        if (userId == null) {
            return false;
        }
        String sql = "UPDATE " + Hikari.getDbName() + ".`user_notification` SET `is_read` = ? WHERE `user_id` = ? ";
        if (notificationId != null) {
            sql += "AND `notification_id` = ? ";
        }
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, isRead);
            ps.setInt(2, userId);
            if (notificationId != null) {
                ps.setInt(3, notificationId);
            }
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return false;
    }


    @Override
    public int count(Boolean isRead, Integer userId) {
        String sql = "SELECT COUNT(*) FROM " + Hikari.getDbName() + ".`user_notification` WHERE `user_id` = ? ";
        if (isRead != null) {
            sql += "AND `is_read` = ? ";
        }
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            if (isRead != null) {
                ps.setBoolean(2, isRead);
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return 0;
    }


    @Override
    public void delete(Integer notificationId, Integer userId) {
        String sql = "DELETE FROM " + Hikari.getDbName() + ".`user_notification` WHERE `user_id` = ? AND `notification_id` = ? ";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, notificationId);
            ps.executeUpdate();
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
    }
}