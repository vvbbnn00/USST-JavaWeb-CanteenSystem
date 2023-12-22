package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.ComplaintDao;
import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.model.Complaint;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ComplaintDaoImpl implements ComplaintDao {

    @Override
    public Complaint insert(Complaint complaint) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, complaint, new String[]{
                    "createdBy", "title", "content", "canteenId"
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
    public Complaint update(Integer complaintId, Complaint.Status status) {
        // 只能更新投诉的状态，其他字段不可更新，且投诉不能被删除
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("UPDATE complaint SET status = ? WHERE complaint_id = ?");
            ps.setString(1, status.name());
            ps.setInt(2, complaintId);
            ps.executeUpdate();
            return queryById(complaintId);
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public Complaint queryById(Integer complaintId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = SqlStatementUtils.generateBasicSelectSql(Complaint.class, new String[]{
                    "complaintId", "createdBy", "title", "content", "status", "createdAt", "updatedAt",
                    "canteenId"
            });
            PreparedStatement ps = connection.prepareStatement(sql + " WHERE complaint_id = ?");
            ps.setInt(1, complaintId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Complaint) SqlStatementUtils.makeEntityFromResult(rs, Complaint.class);
            }
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
        return null;
    }

    public static class ConditionAndParam {
        public List<String> conditions = new ArrayList<>();
        public List<Object> params = new ArrayList<>();

        public ConditionAndParam(String kw, Integer userId, Integer canteenId, Complaint.Status status) {
            if (kw != null && !kw.isEmpty()) {
                conditions.add("(title LIKE ?)");
                params.add("%" + kw + "%");
            }
            if (userId != null) {
                conditions.add("created_by = ?");
                params.add(userId);
            }
            if (canteenId != null) {
                conditions.add("canteen_id = ?");
                params.add(canteenId);
            }
            if (status != null) {
                conditions.add("status = ?");
                params.add(status.name());
            }
        }
    }


    @Override
    public List<Complaint> queryComplaintList(String kw, Integer userId, Integer canteenId,
                                              Complaint.Status status, String orderBy, Boolean asc,
                                              Integer page, Integer pageSize) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Complaint.class, new String[]{
                "complaintId",
                "(complaint.complaint_id) as canteen_id", "createdBy", "title", "(LEFT(content,50)) as content", "status",
                "(complaint.created_at) as created_at",
                "(complaint.updated_at) as updated_at",
                "(canteen.name) as canteenName"
        });
        sql += " LEFT JOIN canteen ON complaint.canteen_id = canteen.canteen_id";
        ConditionAndParam conditionAndParam = new ConditionAndParam(kw, userId, canteenId, status);
        sql += SqlStatementUtils.generateWhereSql(conditionAndParam.conditions);
        if (orderBy != null && !orderBy.isEmpty()) {
            sql += " ORDER BY " + SqlStatementUtils.camelToSnakeQuote(orderBy);
            if (Boolean.TRUE.equals(asc)) {
                sql += " ASC";
            } else {
                sql += " DESC";
            }
        }

        if (page != null && pageSize != null) {
            sql += " LIMIT ?, ?";
            conditionAndParam.params.add((page - 1) * pageSize);
            conditionAndParam.params.add(pageSize);
        }

        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement(sql);
            int i = 1;
            for (Object param : conditionAndParam.params) {
                ps.setObject(i++, param);
            }
            ResultSet rs = ps.executeQuery();
            List<Complaint> complaints = new ArrayList<>();
            while (rs.next()) {
                Complaint complaint = (Complaint) SqlStatementUtils.makeEntityFromResult(rs, Complaint.class);
                Canteen canteen = new Canteen();
                canteen.setName(rs.getString("canteenName"));
                canteen.setCanteenId(rs.getInt("canteen_id"));
                complaint.setCanteen(canteen);
                complaints.add(complaint);
            }
            return complaints;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    public Integer countComplaintList(String kw, Integer userId, Integer canteenId, Complaint.Status status) {
        String sql = "SELECT COUNT(*) FROM complaint";
        ConditionAndParam conditionAndParam = new ConditionAndParam(kw, userId, canteenId, status);
        sql += SqlStatementUtils.generateWhereSql(conditionAndParam.conditions);

        try (Connection connection = Hikari.getConnection()) {
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

    @Override
    public void delete(Integer complaintId) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = connection.prepareStatement("DELETE FROM complaint WHERE complaint_id = ?");
            ps.setInt(1, complaintId);
            ps.executeUpdate();
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }
}
