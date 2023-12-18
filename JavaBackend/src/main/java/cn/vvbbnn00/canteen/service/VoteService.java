package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.VoteDao;
import cn.vvbbnn00.canteen.dao.impl.VoteDaoImpl;
import cn.vvbbnn00.canteen.model.*;

import java.time.ZoneId;
import java.util.List;

public class VoteService {
    private final VoteDao voteDao = new VoteDaoImpl();

    /**
     * 发起一个投票
     *
     * @param vote 投票，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    public void createVote(Vote vote) {
        // 检查投票时间是否合法,开始时间必须大于当前时间，结束时间必须大于开始时间
        if (vote.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < System.currentTimeMillis()) {
            throw new RuntimeException("开始时间必须大于当前时间");
        }
        if (vote.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < vote.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
            throw new RuntimeException("结束时间必须大于开始时间");
        }
        if (vote.getVoteName() == null || vote.getVoteName().isBlank()) {
            throw new RuntimeException("投票名称不能为空");
        }
        boolean result = voteDao.insert(vote);
        if (!result) {
            throw new RuntimeException("投票失败");
        }
    }

    /**
     * 获取投票列表
     *
     * @param page     页码 从1开始
     * @param pageSize 每页大小 默认10
     * @param status   状态
     * @param orderBy  排序字段，支持：start_time、end_time
     * @param asc      是否升序
     * @return 投票列表
     */
    public List<Vote> getVoteList(Integer page, Integer pageSize, Integer status, String orderBy, Boolean asc) {
        return voteDao.getVoteList(page, pageSize, status, orderBy, asc);
    }

    /**
     * 根据id查询投票信息
     *
     * @param id 投票id
     * @return 投票信息
     */
    public Vote queryVoteById(Integer id) {
        return voteDao.queryVoteById(id);
    }

    /**
     * 返回投票选项列表
     *
     * @return 投票选项列表
     */
    public List<VoteOption> getVoteOptionList() {
        return voteDao.getVoteOptionList();
    }

    public void createVoteOption(VoteOption voteOption) {
        if (voteOption.getVoteId() == null) {
            throw new RuntimeException("投票id不能为空");
        }
        if (voteOption.getName() == null || voteOption.getName().isBlank()) {
            throw new RuntimeException("投票选项名称不能为空");
        }
        if (queryVoteById(voteOption.getVoteId()).getIsStarted()) {
            throw new RuntimeException("投票已经开始，不能增加投票选项");
        }
        try {
            voteDao.insertVoteOption(voteOption);
        } catch (Exception e) {
            throw new RuntimeException("投票选项创建失败");
        }
    }

    public boolean updateVoteOption(VoteOption voteOption) {
        if (voteOption.getVoteOptionId() == null) {
            throw new RuntimeException("投票选项id不能为空");
        }
        if (voteOption.getName() == null || voteOption.getName().isBlank()) {
            throw new RuntimeException("投票选项名称不能为空");
        }
        Vote vote = queryVoteById(voteOption.getVoteId());
        if (vote == null) {
            throw new RuntimeException("投票不存在");
        }
        if (vote.getIsStarted()) {
            throw new RuntimeException("投票已经开始，不能修改投票选项");
        }
        boolean success = voteDao.updateVoteOption(voteOption);
        if (!success) {
            throw new RuntimeException("投票选项更新失败");
        }
        return true;
    }

    public boolean deleteVoteOption(VoteOption voteOption) {
        if (voteOption.getVoteOptionId() == null) {
            throw new RuntimeException("投票选项id不能为空");
        }
        if (queryVoteById(voteOption.getVoteId()).getIsStarted()) {
            throw new RuntimeException("投票已经开始，不能删除投票选项");
        }
        boolean success = voteDao.deleteVoteOption(voteOption);
        if (!success) {
            throw new RuntimeException("投票选项删除失败");
        }
        return true;
    }

    public VoteOption queryVoteOptionById(Integer id) {
        return voteDao.queryVoteOptionById(id);
    }

    public boolean updateVote(Vote vote) {
        if (vote.getVoteId() == null) {
            throw new RuntimeException("投票id不能为空");
        }
        if (vote.getVoteName() == null || vote.getVoteName().isBlank()) {
            throw new RuntimeException("投票名称不能为空");
        }
        if (vote.getStartTime() == null) {
            throw new RuntimeException("投票开始时间不能为空");
        }
        if (vote.getEndTime() == null) {
            throw new RuntimeException("投票结束时间不能为空");
        }
        if (vote.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < System.currentTimeMillis()) {
            throw new RuntimeException("开始时间必须大于当前时间");
        }
        if (vote.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < vote.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
            throw new RuntimeException("结束时间必须大于开始时间");
        }
        boolean success = voteDao.updateVote(vote);
        if (!success) {
            throw new RuntimeException("投票信息更新失败");
        }
        return true;
    }

    public boolean deleteVote(Vote vote) {
        boolean success = voteDao.deleteVote(vote);
        if (!success) {
            throw new RuntimeException("投票删除失败");
        }
        return true;
    }

    public List<VoteOptionInfo> getVoteOptionStat(Integer voteId) {
        return voteDao.queryVoteResultStat(voteId);
    }

    public boolean createVoteResult(Voter voter) {
        if (voteDao.queryVoteById(voter.getVoteId()) == null) {
            throw new RuntimeException("投票不存在");
        }
        if (voteDao.queryVoteOptionById(voter.getOptionId()) == null) {
            throw new RuntimeException("投票选项不存在");
        }
        if (voteDao.queryVoteResultByUserId(voter.getUserId(), voter.getVoteId()) != null) {
            throw new RuntimeException("您已经投过票了");
        }
        boolean success = voteDao.insertVoteResult(voter);
        if (!success) {
            throw new RuntimeException("投票失败");
        }
        return true;
    }

    public VoteOption getVoteResultByVoteId(Integer voteId, Integer userId) {
        return voteDao.queryVoteOptionById(voteDao.queryVoteResultByUserId(userId, voteId).getOptionId());
    }
}
