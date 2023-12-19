package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.UserPointLogDao;
import cn.vvbbnn00.canteen.model.UserPointLog;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserPointLogDaoImpl implements UserPointLogDao {
    @Override
    public void insert(UserPointLog userPointLog) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, userPointLog, new String[]{
                    "userId", "point", "detail"
            });
            ps.executeUpdate();
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }

    @Override
    public Integer countTodayPointByUserId(Integer userId) {
        String sql = "SELECT SUM(`point`) FROM " + Hikari.getDbName() + ".`user_point_log` " +
                "WHERE `user_id` = ? AND `created_at` > CURDATE();";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
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
