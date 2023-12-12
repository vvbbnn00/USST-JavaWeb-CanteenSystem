package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.MessageDao;
import cn.vvbbnn00.canteen.model.UserMessage;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MessageDaoImpl implements MessageDao {

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

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`user_message` WHERE `message_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return false;
    }


    @Override
    public List<UserMessage> queryMessages(Integer toUserId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "SELECT messageId, fromUserId, toUserId, content, createdAt, updatedAt " +
                    "FROM " + Hikari.getDbName() + ".`user_message` " +
                    "WHERE to_user_id = ? " +
                    "ORDER BY createdAt DESC LIMIT 200;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, toUserId);
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


    @Override
    public int queryMessagesCount(Integer toUserId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(UserMessage.class, new String[]{
                    "messageId", "fromUserId", "toUserId", "content", "createdAt", "updatedAt"
            }) + " WHERE `to_user_id` = ? AND `content` LIKE ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, toUserId);
            ResultSet rs = ps.executeQuery();
            int count = 0;
            while (rs.next()) {
                count++;
            }
            return count;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return 0;
    }

}
