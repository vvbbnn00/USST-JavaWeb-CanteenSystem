package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.VoteDao;
import cn.vvbbnn00.canteen.model.*;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class VoteDaoImpl implements VoteDao {

    @Override
    public Vote insert(Vote vote) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, vote, new String[]{
                    "voteName", "startTime", "endTime", "isStarted", "voteIntro", "createdBy"
            });
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();

            if (generatedKeys.next()) {
                vote.setVoteId(generatedKeys.getInt(1));
                return vote;
            } else {
                // Handle the case where no key was generated
                throw new SQLException("Insertion failed, no ID obtained.");
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<Vote> getVoteList(Integer page, Integer pageSize, Integer status, String orderBy, Boolean asc) {
        String sql = SqlStatementUtils.generateBasicSelectSql(Vote.class, new String[]{
                "voteId", "voteName", "startTime", "endTime", "isStarted", "voteIntro", "createdBy", "createdAt", "updatedAt"
        });

        List<Object> params = new ArrayList<>();
        if (status != null) {
            sql += " WHERE `is_started` = ?";
            params.add(status);
        }

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
            List<Vote> votes = new ArrayList<>();
            while (rs.next()) {
                votes.add((Vote) SqlStatementUtils.makeEntityFromResult(rs, Vote.class));
            }
            return votes;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return null;
        }
    }

    @Override
    public Vote queryVoteById(Integer id) {
        try {
            Connection connection = Hikari.getConnection();
            String sql = SqlStatementUtils.generateBasicSelectSql(Vote.class, new String[]{
                    "voteId", "voteName", "startTime", "endTime", "isStarted", "voteIntro", "createdBy", "createdAt", "updatedAt"
            }) + " WHERE `vote_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Vote) SqlStatementUtils.makeEntityFromResult(rs, Vote.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<VoteOption> getVoteOptionList(Integer voteId) {
        try {
            Connection connection = Hikari.getConnection();
            String sql = SqlStatementUtils.generateBasicSelectSql(VoteOption.class, new String[]{
                    "vote_option_id", "vote_id", "name", "created_at", "updated_at"
            });
            sql += " WHERE `vote_id` = ?;";

            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, voteId);

            ResultSet rs = ps.executeQuery();
            List<VoteOption> voteOptions = new ArrayList<>();
            while (rs.next()) {
                voteOptions.add((VoteOption) SqlStatementUtils.makeEntityFromResult(rs, VoteOption.class));
            }
            return voteOptions;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void insertVoteOption(VoteOption voteOption) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, voteOption, new String[]{
                    "voteId", "name"
            });
            ps.executeUpdate();
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
    }

    @Override
    public boolean updateVoteOption(VoteOption voteOption) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateUpdateStatement(connection, voteOption, new String[]{
                    "name"
            }, new String[]{
                    "voteOptionId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteVoteOption(VoteOption voteOption) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`vote_option`" + " WHERE vote_option_id=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, voteOption.getVoteOptionId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean updateVote(Vote vote) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateUpdateStatement(connection, vote, new String[]{
                    "voteName", "startTime", "endTime", "isStarted", "voteIntro"
            }, new String[]{
                    "voteId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean deleteVote(Vote vote) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "DELETE FROM " + Hikari.getDbName() + ".`vote`" + " WHERE vote_id=?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, vote.getVoteId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public VoteOption queryVoteOptionById(Integer id) {
        try {
            Connection connection = Hikari.getConnection();
            String sql = SqlStatementUtils.generateBasicSelectSql(VoteOption.class, new String[]{
                    "vote_option_id", "vote_id", "name", "created_at", "updated_at"
            }) + " WHERE `vote_option_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (VoteOption) SqlStatementUtils.makeEntityFromResult(rs, VoteOption.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public boolean insertVoteResult(Voter voter) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, voter, new String[]{
                    "userId", "voteId", "optionId"
            });
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return false;
        }
    }

    @Override
    public Voter queryVoteResultByUserId(Integer userId, Integer voteId) {
        try {
            Connection connection = Hikari.getConnection();
            String sql = SqlStatementUtils.generateBasicSelectSql(Voter.class, new String[]{
                    "userId", "voteId", "optionId"
            }) + " WHERE `user_id` = ? AND `vote_id` = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, userId);
            ps.setInt(2, voteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return (Voter) SqlStatementUtils.makeEntityFromResult(rs, Voter.class);
            }
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<VoteOptionInfo> queryVoteResultStat(Integer voteId) {
        try (Connection connection = Hikari.getConnection()) {
            String sql = "SELECT vo.vote_option_id, vo.name, COUNT(v.voter_id) AS count " +
                    "FROM " + Hikari.getDbName() + ".`vote_option` AS vo " +
                    "LEFT JOIN " + Hikari.getDbName() + ".`voter` AS v " +
                    "ON vo.vote_option_id = v.option_id " +
                    "WHERE vo.vote_id = ? " +
                    "GROUP BY vo.vote_option_id, vo.name " +
                    "ORDER BY count DESC;";

            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, voteId);
            ResultSet rs = ps.executeQuery();

            List<VoteOptionInfo> result = new ArrayList<>();
            while (rs.next()) {
                VoteOptionInfo optionInfo = new VoteOptionInfo();
                optionInfo.setVoteOptionId(rs.getInt("vote_option_id"));
                optionInfo.setName(rs.getString("name"));
                optionInfo.setCount(rs.getInt("count"));
                result.add(optionInfo);
            }
            return result;
        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Integer getVoteListCount(Integer status) {
        String sql = "SELECT COUNT(*) FROM vote";
        List<Object> params = new ArrayList<>();
        if (status != null) {
            sql += " WHERE `is_started` = ?";
            params.add(status);
        }

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
