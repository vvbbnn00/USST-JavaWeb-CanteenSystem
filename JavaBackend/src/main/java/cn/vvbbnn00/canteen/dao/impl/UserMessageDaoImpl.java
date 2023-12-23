package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.UserMessageDao;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserMessage;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserMessageDaoImpl implements UserMessageDao {

    @Override
    public boolean insert(UserMessage message) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, message, new String[]{
                    "fromUserId", "toUserId", "content"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    public List<User> queryMessagedUsers(Integer userId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "SELECT DISTINCT user.user_id as user_id, user.username as username, " +
                    "user.email as email, message.created_at as msg_created_at, level, is_verified " +
                    "FROM " + Hikari.getDbName() + ".`user_message` AS message " +
                    "JOIN " + Hikari.getDbName() + ".`user` AS user " +
                    "ON message.from_user_id = user.user_id OR message.to_user_id = user.user_id " +
                    "WHERE (message.from_user_id = ? OR message.to_user_id = ?) " +
                    "AND message.created_at = (SELECT MAX(created_at) FROM " + Hikari.getDbName() + ".`user_message` " +
                    "                           WHERE (from_user_id = ? OR to_user_id = ?) " +
                    "                             AND (from_user_id = user.user_id OR to_user_id = user.user_id)) " +
                    "ORDER BY message.created_at DESC LIMIT 200;";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ResultSet rs = ps.executeQuery();

            List<User> users = new ArrayList<>();
            while (rs.next()) {
                User user = (User) SqlStatementUtils.makeEntityFromResult(rs, User.class);
                if (!user.getUserId().equals(userId)) {
                    users.add(user);
                }
            }
            return users;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return new ArrayList<>();
        }
    }


    @Override
    public List<UserMessage> queryMessagesList(Integer toUserId, Integer fromUserId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "SELECT * FROM " + Hikari.getDbName() + ".`user_message` " +
                    "WHERE (`to_user_id` = ? AND `from_user_id` = ?) OR (`to_user_id` = ? AND `from_user_id` = ?) " +
                    "ORDER BY `created_at` DESC LIMIT 200;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, toUserId);
            ps.setInt(2, fromUserId);
            ps.setInt(3, fromUserId);
            ps.setInt(4, toUserId);
            ResultSet rs = ps.executeQuery();

            List<UserMessage> messages = new ArrayList<>();
            while (rs.next()) {
                messages.add((UserMessage) SqlStatementUtils.makeEntityFromResult(rs, UserMessage.class));
            }
            return messages;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return new ArrayList<>();
        }
    }

}
