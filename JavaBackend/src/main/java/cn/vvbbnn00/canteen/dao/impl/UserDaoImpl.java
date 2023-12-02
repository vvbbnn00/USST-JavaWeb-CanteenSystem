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
import java.util.List;

public class UserDaoImpl implements UserDao {

    @Override
    public boolean insert(User user) {
        user.setPassword(SafetyUtils.passwordDoBCrypt(user.getPassword()));

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
            String sql = SqlStatementUtils.generateBasicSelectSql(new User(), new String[]{
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
            String sql = SqlStatementUtils.generateBasicSelectSql(new User(), new String[]{
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
        // TODO: 实现更新用户信息
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        // TODO: 实现删除用户
        return false;
    }

    @Override
    public List<User> queryUsers(Integer page, Integer pageSize, String kw, Boolean available, User.Role role, Boolean isVerified, String orderBy, Boolean asc) {
        // TODO: 实现查询用户列表
        return null;
    }

    @Override
    public Integer queryUsersCount(String kw, Boolean available, User.Role role, Boolean isVerified) {
        // TODO: 实现查询用户数量
        return null;
    }
}
