package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.CuisineDao;
import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.model.Cuisine;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CuisineDaoImpl implements CuisineDao {
    @Override
    public boolean insert(Cuisine cuisine) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, cuisine, new String[]{
                    "name", "canteenId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Cuisine queryCuisineById(Integer id) {
        try {
            Connection connection = Hikari.getConnection();
            String sql = SqlStatementUtils.generateBasicSelectSql(Cuisine.class, new String[]{
                    "cuisineId", "name", "canteenId", "createdAt", "updatedAt"
            }) + " WHERE `cuisine_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Cuisine) SqlStatementUtils.makeEntityFromResult(rs, Cuisine.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean update(Cuisine cuisine) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateUpdateStatement(connection, cuisine, new String[]{
                    "name"
            }, new String[]{
                    "cuisineId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`cuisine`" + " WHERE cuisine_id=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    public static class ConditionAndParams {
        List<String> conditions;
        List<Object> params;

        ConditionAndParams(String kw, Integer canteenId) {
            this.conditions = new ArrayList<>();
            this.params = new ArrayList<>();
            if (kw != null) {
                this.conditions.add("(`name` LIKE ?)");
                this.params.add("%" + kw + "%");
            }
            if (canteenId != null) {
                this.conditions.add("`canteen_id` = ?");
                this.params.add(canteenId);
            }
        }
    }

    @Override
    public List<Cuisine> queryCuisines(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc, Integer canteenId) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Cuisine.class, new String[]{
                "cuisine_id", "name", "canteen_id", "created_at", "updated_at"
        });

        ConditionAndParams conditionAndParams = new ConditionAndParams(kw, canteenId);

        List<String> conditions = conditionAndParams.conditions;
        List<Object> params = conditionAndParams.params;

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
            List<Cuisine> cuisines = new ArrayList<>();
            while (rs.next()) {
                cuisines.add((Cuisine) SqlStatementUtils.makeEntityFromResult(rs, Cuisine.class));
            }
            return cuisines;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public Integer queryCuisinesCount(String kw, Integer canteenId) {
        String sql = "SELECT COUNT(*) FROM " + Hikari.getDbName() + ".`cuisine`";
        ConditionAndParams conditionAndParams = new ConditionAndParams(kw, canteenId);

        List<String> conditions = conditionAndParams.conditions;
        List<Object> params = conditionAndParams.params;

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
            return null;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return null;
        }
    }
}
