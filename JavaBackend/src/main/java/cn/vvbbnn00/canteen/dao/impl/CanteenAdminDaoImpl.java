package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.CanteenAdminDao;
import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.model.CanteenAdmin;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CanteenAdminDaoImpl implements CanteenAdminDao {
    @Override
    public boolean insert(CanteenAdmin canteenAdmin) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, canteenAdmin, new String[]{
                    "canteenId", "userId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean remove(CanteenAdmin canteenAdmin) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`canteen_admin`" + " WHERE canteen_id=? AND user_id=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, canteenAdmin.getCanteenId());
            ps.setInt(2, canteenAdmin.getUserId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public List<CanteenAdmin> query(Integer canteenId, Integer userId) {
        String sql = SqlStatementUtils.generateBasicSelectSql(CanteenAdmin.class, new String[]{
                "canteenId", "userId"
        });
        ConditionAndParam conditionAndParam = new ConditionAndParam(canteenId, userId);
        List<String> conditions = conditionAndParam.conditions;
        List<Object> params = conditionAndParam.params;

        sql += SqlStatementUtils.generateWhereSql(conditions);

        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            List<CanteenAdmin> canteenAdmins = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                canteenAdmins.add((CanteenAdmin) SqlStatementUtils.makeEntityFromResult(rs, CanteenAdmin.class));
            }
            return canteenAdmins;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    public static class ConditionAndParam {
        public List<String> conditions = new ArrayList<>();
        public List<Object> params = new ArrayList<>();

        public ConditionAndParam(Integer canteenId, Integer userId) {
            if (canteenId != null) {
                conditions.add("canteen_id = ?");
                params.add(canteenId);
            }
            if (userId != null) {
                conditions.add("user_id = ?");
                params.add(userId);
            }
        }
    }
}
