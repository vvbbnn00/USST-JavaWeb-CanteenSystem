package cn.vvbbnn00.canteen.dao.impl;


import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.UserNotificationDao;
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
                    "userId", "content", "isRead"
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
    public List<UserNotification> queryByUserId(Integer userId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(UserNotification.class, new String[]{
                    "notificationId", "userId", "content", "isRead", "createdAt", "updatedAt"
            }) + " WHERE `user_id` = ? ORDER BY `created_at` DESC LIMIT 200;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
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


    @Override
    public boolean update(Integer notificationId, Boolean isRead, Integer userId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "UPDATE " + Hikari.getDbName() + ".`user_notification` SET `is_read` = ? WHERE `notification_id` = ? AND `user_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, isRead);
            ps.setInt(2, notificationId);
            ps.setInt(3, userId);
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
    public void batchUpdate(Integer notificationId, Boolean isRead, Integer userId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "UPDATE " + Hikari.getDbName() + ".`user_notification` SET `is_read` = ? WHERE `notification_id` = ? AND `user_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, isRead);
            ps.setInt(2, notificationId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
    }
}