package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.CommentDao;
import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.model.Comment;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CommentDaoImpl implements CommentDao{

    @Override
    public boolean insert(Comment comment) {
        try (Connection connection = Hikari.getConnection()){
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, comment, new String[]{
                    "type","reference_id","created_by","content","score","parent_id","created_at","updated_at"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public Comment queryCommentById(Integer id) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(Comment.class, new String[]{
                    "comment_id", "type", "reference_id", "created_by", "content", "score", "parent_id", "created_at", "updated_at"
            }) + " WHERE `comment_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Comment) SqlStatementUtils.makeEntityFromResult(rs, Comment.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM" + Hikari.getDbName() + ".`comment` WHERE `comment_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    public static class ConditionsAndParams {
        List<String> conditions;
        List<Object> params;

        ConditionsAndParams(String kw,String type) {
            conditions = new ArrayList<>();
            params = new ArrayList<>();
            if (kw != null) {
                conditions.add("(`content` LIKE ? OR `reference_id` LIKE ? OR `parent_id` LIKE ?)");
                params.add("%" + kw + "%");
                params.add("%" + kw + "%");
                params.add("%" + kw + "%");
            }
            if (type != null) {
                conditions.add("`type` = ?");
                params.add(type);
            }
        }
    }

    @Override
    public List<Comment> queryComments(Integer page, Integer pageSize, String kw, String type, String orderBy, Boolean asc) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Comment.class, new String[]{
                "comment_id", "type", "reference_id", "created_by", "content", "score", "parent_id", "created_at", "updated_at"
        });

        ConditionsAndParams conditionsAndParams = new ConditionsAndParams(kw,type);
        List<String> conditions = conditionsAndParams.conditions;
        List<Object> params = conditionsAndParams.params;

        sql += SqlStatementUtils.generateWhereSql(conditions);

        if (orderBy != null) {
            sql += " ORDER BY " + SqlStatementUtils.camelToSnakeQuote(orderBy);
            if (asc != null && !asc) {
                sql += " DESC";
            }
        }

        if (page != null && pageSize != null) {
            sql += " LIMIT ?, ?;";
            params.add((page - 1) * pageSize);
            params.add(pageSize);
        }

        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<Comment> comments = new ArrayList<>();
            while (rs.next()) {
                comments.add((Comment) SqlStatementUtils.makeEntityFromResult(rs, Comment.class));
            }
            return comments;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public Integer queryCommentsCount(String kw,String type) {
        String sql = "SELECT COUNT(*) FROM " + Hikari.getDbName() + ".`comment`";
        ConditionsAndParams conditionsAndParams = new ConditionsAndParams(kw,type);

        List<String> conditions = conditionsAndParams.conditions;
        List<Object> params = conditionsAndParams.params;

        sql += SqlStatementUtils.generateWhereSql(conditions);

        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }
}
