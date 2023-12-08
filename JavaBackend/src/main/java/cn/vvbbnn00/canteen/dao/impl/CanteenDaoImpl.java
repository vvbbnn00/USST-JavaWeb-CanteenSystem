package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.CanteenDao;
import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SafetyUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CanteenDaoImpl implements CanteenDao {

    @Override
    public int insert(Canteen canteen) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, canteen, new String[]{
                    "name", "location", "introduction", "compScore",
            });

            ps.executeUpdate();

            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                return generatedKeys.getInt(1);
            } else {
                // Handle the case where no key was generated
                throw new SQLException("Insertion failed, no ID obtained.");
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return -1;
        }
    }

    @Override
    public Canteen queryCanteenById(Integer id) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(Canteen.class, new String[]{
                    "canteen_id", "name", "location", "introduction", "compScore", "createdAt", "updatedAt"
            }) + " WHERE `canteen_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Canteen) SqlStatementUtils.makeEntityFromResult(rs, Canteen.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }


    @Override
    public boolean update(Canteen canteen) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateUpdateStatement(connection, canteen, new String[]{
                    "name", "location", "introduction", "compScore"
            }, new String[]{
                    "canteenId"
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
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`canteen` WHERE `canteen_id` = ?;";
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
    public List<Canteen> queryCanteens(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Canteen.class, new String[]{
                "canteen_id", "name", "location", "introduction", "compScore", "createdAt", "updatedAt"
        });

        ConditionAndParam condAndParam = new ConditionAndParam(kw);

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
            List<Canteen> canteens = new ArrayList<>();
            while (rs.next()) {
                canteens.add((Canteen) SqlStatementUtils.makeEntityFromResult(rs, Canteen.class));
            }
            return canteens;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
        }
        return null;
    }

    public static class ConditionAndParam {
        public List<String> conditions = new ArrayList<>();
        public List<Object> params = new ArrayList<>();

        public ConditionAndParam(String kw) {
            if (kw != null) {
                conditions.add("(`name` LIKE ? OR `location` LIKE ? OR `introduction` LIKE ?)");
                params.add("%" + kw + "%");
                params.add("%" + kw + "%");
                params.add("%" + kw + "%");
            }
        }
    }

    @Override
    public Integer queryCanteensCount(String kw) {
        String sql = "SELECT COUNT(*) FROM " + Hikari.getDbName() + ".`canteen`";
        ConditionAndParam condAndParam = new ConditionAndParam(kw);

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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}