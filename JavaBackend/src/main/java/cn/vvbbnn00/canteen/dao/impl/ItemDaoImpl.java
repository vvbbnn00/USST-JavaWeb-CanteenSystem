package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.ItemDao;
import cn.vvbbnn00.canteen.model.Item;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemDaoImpl implements ItemDao {
    @Override
    public Item insert(Item item) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, item, new String[]{
                    "name", "cuisineId", "price", "promotionPrice", "introduction", "recommended"
            });
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return queryById(rs.getInt(1));
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Item queryById(Integer itemId) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(SqlStatementUtils.generateBasicSelectSql(Item.class, new String[]{
                    "itemId", "name", "cuisineId", "price", "promotionPrice",
                    "introduction", "compScore", "createdAt", "updatedAt", "recommended"
            }) + " WHERE item_id = ?");
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Item) SqlStatementUtils.makeEntityFromResult(rs, Item.class);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean delete(Integer itemId) {
        String sql = "DELETE FROM item WHERE item_id = ?";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, itemId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean update(Item item) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateUpdateStatement(connection, item, new String[]{
                    "name", "cuisineId", "price", "promotionPrice", "introduction", "recommended"
            }, new String[]{"itemId"});
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }

    public static class ConditionAndParam {
        public List<String> conditions = new ArrayList<>();
        public List<Object> params = new ArrayList<>();

        public ConditionAndParam(String kw, Integer cuisineId, Integer canteenId, Boolean isRecommended) {
            if (kw != null && !kw.isEmpty()) {
                conditions.add("name LIKE ?");
                params.add("%" + kw + "%");
            }
            if (cuisineId != null) {
                conditions.add("cuisine_id = ?");
                params.add(cuisineId);
            }
            if (canteenId != null) {
                conditions.add("cuisine_id in (SELECT cuisine_id FROM cuisine WHERE canteen_id = ?)");
                params.add(canteenId);
            }
            if (isRecommended != null) {
                conditions.add("recommended = ?");
                params.add(isRecommended);
            }
        }
    }

    @Override
    public List<Item> query(String kw, Integer cuisineId, Integer canteenId,
                            Boolean isRecommended, Integer page, Integer pageSize,
                            String orderBy, Boolean asc) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Item.class, new String[]{
                "itemId", "name", "cuisineId", "price", "promotionPrice",
                "introduction", "compScore", "createdAt", "updatedAt", "recommended",
                "(SELECT file_id FROM image WHERE reference_id = item_id AND type = 'item' LIMIT 1) AS file_key"
        });

        // LogUtils.info(sql);

        ConditionAndParam condAndParam = new ConditionAndParam(kw, cuisineId, canteenId, isRecommended);

        List<String> conditions = condAndParam.conditions;
        List<Object> params = condAndParam.params;

        sql += SqlStatementUtils.generateWhereSql(conditions);

        if (orderBy != null) {
            sql += " ORDER BY ?";
            if (asc != null && !asc) {
                sql += " DESC";
            }
            params.add(SqlStatementUtils.camelToSnakeQuote(orderBy));
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
            List<Item> items = new ArrayList<>();
            while (rs.next()) {
                items.add((Item) SqlStatementUtils.makeEntityFromResult(rs, Item.class));
            }
            return items;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Integer count(String kw, Integer cuisineId, Integer canteenId, Boolean isRecommended) {
        String sql = "SELECT COUNT(*) FROM item";

        ConditionAndParam condAndParam = new ConditionAndParam(kw, cuisineId, canteenId, isRecommended);

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
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }
}
