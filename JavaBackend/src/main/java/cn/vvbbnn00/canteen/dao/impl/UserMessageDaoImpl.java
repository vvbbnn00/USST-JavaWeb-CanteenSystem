package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.UserMessageDao;
import cn.vvbbnn00.canteen.dto.response.UserMessageCount;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserMessage;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;
import cn.vvbbnn00.canteen.util.StringUtils;

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

    public List<UserMessageCount> queryMessagedUsers(Integer userId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "SELECT user.user_id as user_id, SUM(message_count) AS total_messages, username, email, level, is_verified " +
                    "FROM ( " +
                    "    SELECT from_user_id AS user_id, count(message_id) as message_count, MAX(created_at) AS last_date " +
                    "    FROM user_message " +
                    "    WHERE from_user_id <> ? AND to_user_id = ? " +
                    "    GROUP BY from_user_id " +
                    "    UNION ALL " +
                    "    SELECT to_user_id AS user_id, count(message_id), MAX(created_at) AS last_date " +
                    "    FROM user_message " +
                    "    WHERE to_user_id <> ? AND from_user_id = ? " +
                    "    GROUP BY to_user_id " +
                    "    UNION ALL " +
                    "    SELECT to_user_id AS user_id, count(message_id), MAX(created_at) AS last_date " +
                    "    FROM user_message " +
                    "    WHERE to_user_id = ? " +
                    "    GROUP BY to_user_id " +
                    "    ORDER BY last_date DESC" +
                    "    LIMIT 200 " +
                    ") AS combined " +
                    "JOIN user ON user.user_id = combined.user_id " +
                    "GROUP BY user.user_id;";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, userId);

            ResultSet rs = ps.executeQuery();

            List<UserMessageCount> users = new ArrayList<>();
            while (rs.next()) {
                UserMessageCount user = (UserMessageCount) SqlStatementUtils.makeEntityFromResult(rs, UserMessageCount.class);
                user.setAvatar(StringUtils.getAvatarUrl(rs.getString("email")));
                users.add(user);
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
