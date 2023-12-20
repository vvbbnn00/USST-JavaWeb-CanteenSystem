package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.TopicDao;
import cn.vvbbnn00.canteen.model.Topic;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TopicDaoImpl implements TopicDao {

    @Override
    public Topic insert(Topic topic, Integer userId) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, topic, new String[]{
                    "createdBy", "title", "content"
            });
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return queryTopicById(rs.getInt(1), userId);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void delete(Integer topicId) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM topic WHERE topic_id = ?");
            ps.setInt(1, topicId);
            ps.executeUpdate();
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }

    @Override
    public Topic queryTopicById(Integer topicId, Integer queryUserId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(Topic.class, new String[]{
                    "topicId", "title", "content", "createdBy", "createdAt", "updatedAt", "flagged",
                    "(SELECT COUNT(*) FROM topic_like WHERE topic_id = ?) AS like_count",
                    "(SELECT COUNT(*) FROM comment WHERE reference_id = ? AND type = 'topic') AS comment_count",
                    "(SELECT COUNT(*) FROM topic_like WHERE topic_id = ? AND user_id = ?) AS is_liked",
                    "(SELECT GROUP_CONCAT(file_id) FROM image WHERE reference_id = topic.topic_id AND type = 'topic') AS file_keys",
                    "((SELECT COUNT(*) FROM topic_like WHERE topic_id = topic.topic_id AND user_id = ?) + (SELECT COUNT(*) FROM comment WHERE reference_id = topic.topic_id AND type = 'topic') * 1.5) AS comp_value",
            }) + " WHERE topic_id = ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, topicId);
            ps.setInt(2, topicId);
            ps.setInt(3, topicId);
            ps.setInt(4, queryUserId);
            ps.setInt(5, queryUserId);
            ps.setInt(6, topicId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Topic topic = (Topic) SqlStatementUtils.makeEntityFromResult(rs, Topic.class);
                String fileKeys = rs.getString("file_keys");
                if (fileKeys != null) {
                    String[] fileKeyArr = fileKeys.split(",");
                    topic.setImages(List.of(fileKeyArr));
                }
                return topic;
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    public static class ConditionAndParam {
        public List<String> conditions = new ArrayList<>();
        public List<Object> params = new ArrayList<>();

        public ConditionAndParam(Integer userId) {
            if (userId != null) {
                conditions.add("created_by = ?");
                params.add(userId);
            }
        }
    }

    @Override
    public List<Topic> queryTopics(Integer userId, Integer page, Integer pageSize, String orderBy, Boolean asc, Integer queryUserId) {
        try (Connection connection = Hikari.getConnection()) {
            ConditionAndParam conditionAndParam = new ConditionAndParam(userId);
            String sql = SqlStatementUtils.generateBasicSelectSql(Topic.class, new String[]{
                    "topicId", "title", "content", "createdBy", "(topic.created_at) as created_at", "(topic.updated_at) as updated_at", "flagged",
                    "(SELECT COUNT(*) FROM topic_like WHERE topic_id = topic.topic_id) AS like_count",
                    "(SELECT COUNT(*) FROM comment WHERE reference_id = topic.topic_id AND type = 'topic') AS comment_count",
                    "(SELECT COUNT(*) FROM topic_like WHERE topic_id = topic.topic_id AND user_id = ?) AS is_liked",
                    "(user.username) AS username",
                    "(created_by) AS user_id",
                    "(user.email) AS email",
                    "(SELECT GROUP_CONCAT(file_id) FROM image WHERE reference_id = topic.topic_id AND type = 'topic') AS file_keys",
                    "((SELECT COUNT(*) FROM topic_like WHERE topic_id = topic.topic_id AND user_id = ?) + (SELECT COUNT(*) FROM comment WHERE reference_id = topic.topic_id AND type = 'topic') * 1.5) AS comp_value",
            });
            sql += " LEFT JOIN user ON topic.created_by = user.user_id ";
            sql += SqlStatementUtils.generateWhereSql(conditionAndParam.conditions);
            if (orderBy != null) {
                sql += " ORDER BY ?";
                if (asc != null && !asc) {
                    sql += " DESC";
                }
                conditionAndParam.params.add(SqlStatementUtils.camelToSnakeQuote(orderBy));
            }
            sql += " LIMIT ?, ?";

            PreparedStatement ps = connection.prepareStatement(sql);
            int i = 1;
            ps.setInt(i++, queryUserId);
            ps.setInt(i++, queryUserId);
            for (Object param : conditionAndParam.params) {
                ps.setObject(i++, param);
            }
            ps.setInt(i++, (page - 1) * pageSize);
            ps.setInt(i, pageSize);

            // LogUtils.info(ps.toString());

            ResultSet rs = ps.executeQuery();
            List<Topic> topics = new ArrayList<>();
            while (rs.next()) {
                topics.add((Topic) SqlStatementUtils.makeEntityFromResult(rs, Topic.class));
                User user = (User) SqlStatementUtils.makeEntityFromResult(rs, User.class);
                Topic topic = topics.get(topics.size() - 1);
                topic.setUser(user);
                String fileKeys = rs.getString("file_keys");
                if (fileKeys != null) {
                    String[] fileKeyArr = fileKeys.split(",");
                    topic.setImages(List.of(fileKeyArr));
                }
            }
            return topics;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Integer count(Integer userId) {
        try (Connection connection = Hikari.getConnection()) {
            ConditionAndParam conditionAndParam = new ConditionAndParam(userId);
            String sql = "SELECT COUNT(*) FROM topic";
            sql += SqlStatementUtils.generateWhereSql(conditionAndParam.conditions);

            PreparedStatement ps = connection.prepareStatement(sql);
            for (int i = 0; i < conditionAndParam.params.size(); i++) {
                ps.setObject(i + 1, conditionAndParam.params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }
}
