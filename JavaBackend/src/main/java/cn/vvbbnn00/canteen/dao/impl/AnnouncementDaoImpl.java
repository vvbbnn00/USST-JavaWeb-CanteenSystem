package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.AnnouncementDao;
import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.model.Announcement;
import cn.vvbbnn00.canteen.model.Cuisine;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnnouncementDaoImpl implements AnnouncementDao {
    @Override
    public Integer insert(Announcement announcement) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, announcement, new String[]{
                    "title", "content", "canteenId"
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
            LogUtils.severe(e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean delete(Integer announcementId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`announcement`" + " WHERE announcement_id=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, announcementId);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public boolean update(Announcement announcement) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateUpdateStatement(connection, announcement, new String[]{
                    "title", "content", "canteenId"
            }, new String[]{
                    "announcementId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public Announcement queryById(Integer announcementId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(Announcement.class, new String[]{
                    "announcementId", "title", "content", "canteenId", "createdAt", "updatedAt"
            }) + " WHERE `announcement_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, announcementId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Announcement) SqlStatementUtils.makeEntityFromResult(rs, Announcement.class);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Announcement> queryByCanteenId(Integer canteenId) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Announcement.class, new String[]{
                "announcementId", "title", "content", "canteenId", "createdAt", "updatedAt"
        });
        sql += " WHERE `canteen_id` = ?;";
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, canteenId);
            List<Announcement> announcements = new ArrayList<>();
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                announcements.add((Announcement) SqlStatementUtils.makeEntityFromResult(rs, Announcement.class));
            }
            return announcements;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    public static class ConditionsAndParams {
        List<String> conditions;
        List<Object> params;

        ConditionsAndParams(Integer canteenId, String kw) {
            conditions = new ArrayList<>();
            params = new ArrayList<>();
            if (canteenId != null) {
                conditions.add("`canteen_id` = ?");
                params.add(canteenId);
            }
            if (kw != null) {
                conditions.add("(`title` LIKE ? OR `content` LIKE ?)");
                params.add("%" + kw + "%");
                params.add("%" + kw + "%");
            }
        }
    }

    @Override
    public List<Announcement> query(Integer page, Integer pageSize, Integer canteenId, String kw, String orderBy, Boolean asc) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Announcement.class, new String[]{
                "announcementId", "title", "content", "canteenId", "createdAt", "updatedAt"
        });

        AnnouncementDaoImpl.ConditionsAndParams conditionAndParams = new AnnouncementDaoImpl.ConditionsAndParams(canteenId, kw);

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
            List<Announcement> announcements = new ArrayList<>();
            while (rs.next()) {
                announcements.add((Announcement) SqlStatementUtils.makeEntityFromResult(rs, Announcement.class));
            }
            return announcements;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Integer count(Integer canteenId, String kw) {
        String sql = "SELECT COUNT(*) FROM " + Hikari.getDbName() + ".`announcement`";

        AnnouncementDaoImpl.ConditionsAndParams conditionAndParams = new AnnouncementDaoImpl.ConditionsAndParams(canteenId, kw);

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
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

}
