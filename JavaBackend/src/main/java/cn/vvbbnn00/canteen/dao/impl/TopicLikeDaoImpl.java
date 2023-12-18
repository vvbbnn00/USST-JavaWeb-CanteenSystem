package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.TopicLikeDao;
import cn.vvbbnn00.canteen.model.TopicLike;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TopicLikeDaoImpl implements TopicLikeDao {

    @Override
    public boolean insert(TopicLike topicLike) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, topicLike, new String[]{
                    "userId", "topicId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean delete(TopicLike topicLike) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM topic_like WHERE user_id = ? AND topic_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, topicLike.getUserId());
            ps.setInt(2, topicLike.getTopicId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Boolean queryTopicLikeById(Integer userId, Integer topicId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "SELECT * FROM topic_like WHERE user_id = ? AND topic_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, topicId);
            return ps.executeQuery().next();
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Integer countTopicLikeById(Integer topicId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "SELECT COUNT(*) FROM topic_like WHERE topic_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, topicId);
            return ps.executeQuery().getInt(1);
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }
}
