package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.UserDao;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SafetyUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public boolean insert(User user) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, user, new String[]{
                    "username", "password", "name", "employeeId", "level", "point", "available", "role", "isVerified"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public User queryUserById(Integer id) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(User.class, new String[]{
                    "userId", "username", "password", "name", "employeeId", "level", "point", "available", "role", "isVerified", "createdAt", "updatedAt", "lastLoginAt"
            }) + " WHERE `user_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (User) SqlStatementUtils.makeEntityFromResult(rs, User.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public User queryUserByUsername(String username) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(User.class, new String[]{
                    "userId", "username", "password", "name", "employeeId", "level", "point", "available", "role", "isVerified", "createdAt", "updatedAt", "lastLoginAt"
            }) + " WHERE `username` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (User) SqlStatementUtils.makeEntityFromResult(rs, User.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean update(User user) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateUpdateStatement(connection, user, new String[]{
                    "username", "password", "name", "employeeId", "level", "point", "available", "role", "isVerified"
            }, new String[]{
                    "userId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = Hikari.getConnection()) {
            // 此处直接拼接字符串即可，无需调用复杂的方法
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`user` WHERE `user_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return false;
    }

    private static class ConditionAndParam {
        List<String> conditions;
        List<Object> params;

        ConditionAndParam(String kw, Boolean available, User.Role role, Boolean isVerified) {
            conditions = new ArrayList<>();
            params = new ArrayList<>();
            if (kw != null) {
                conditions.add("(`username` LIKE ? OR `name` LIKE ? OR `employee_id` LIKE ?)");
                params.add("%" + kw + "%");
                params.add("%" + kw + "%");
                params.add("%" + kw + "%");
            }

            if (available != null) {
                conditions.add("`available` = ?");
                params.add(available ? 1 : 0);
            }

            if (role != null) {
                conditions.add("`role` = ?");
                params.add(role.toString());
            }

            if (isVerified != null) {
                conditions.add("`is_verified` = ?");
                params.add(isVerified ? 1 : 0);
            }
        }
    }

    @Override
    public List<User> queryUsers(Integer page, Integer pageSize, String kw, Boolean available, User.Role role, Boolean isVerified, String orderBy, Boolean asc) {
        // 密码不应该被查询出来
        String sql = SqlStatementUtils.generateBasicSelectSql(User.class, new String[]{
                "userId", "username", "name", "employeeId", "level", "point", "available", "role", "isVerified", "createdAt", "updatedAt", "lastLoginAt"
        });

        ConditionAndParam condAndParam = new ConditionAndParam(kw, available, role, isVerified);

        List<String> conditions = condAndParam.conditions;
        List<Object> params = condAndParam.params;

        sql += SqlStatementUtils.generateWhereSql(conditions);

        if (orderBy != null) {
            sql += " ORDER BY " + SqlStatementUtils.camelToSnakeQuote(orderBy);
            if (asc != null && !asc) {
                sql += " DESC";
            }
        }

        if (page != null && pageSize != null) {
            sql += " LIMIT ?, ?";
            params.add((page - 1) * pageSize);
            params.add(pageSize);
        }

        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add((User) SqlStatementUtils.makeEntityFromResult(rs, User.class));
            }
            return users;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public Integer queryUsersCount(String kw, Boolean available, User.Role role, Boolean isVerified) {
        String sql = "SELECT COUNT(*) FROM " + Hikari.getDbName() + ".`user`";
        ConditionAndParam condAndParam = new ConditionAndParam(kw, available, role, isVerified);

        List<String> conditions = condAndParam.conditions;
        List<Object> params = condAndParam.params;

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

    @Override
    public List<User> batchQueryUsers(List<Integer> userIds) {
        if (userIds.isEmpty()) {
            return new ArrayList<>();
        }
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(User.class, new String[]{
                    "userId", "username", "name", "employeeId", "level", "point", "available", "role", "isVerified", "createdAt", "updatedAt", "lastLoginAt"
            }) + " WHERE `user_id` IN (" + SqlStatementUtils.generateQuestionMarks(userIds.size()) + ");";
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int i = 0; i < userIds.size(); i++) {
                ps.setInt(i + 1, userIds.get(i));
            }
            LogUtils.info(ps.toString());
            ResultSet rs = ps.executeQuery();
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add((User) SqlStatementUtils.makeEntityFromResult(rs, User.class));
            }
            return users;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }
}
