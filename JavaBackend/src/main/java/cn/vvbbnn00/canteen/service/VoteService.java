package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.VoteDao;
import cn.vvbbnn00.canteen.dao.impl.VoteDaoImpl;
import cn.vvbbnn00.canteen.model.*;

import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

public class VoteService {
    private final VoteDao voteDao = new VoteDaoImpl();
    private final UserPointLogService userPointLogService = new UserPointLogService();

    /**
     * 发起一个投票
     *
     * @param vote 投票，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    public Vote createVote(Vote vote) {
        // 检查投票时间是否合法，结束时间必须大于开始时间
        if (vote.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < vote.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
            throw new RuntimeException("结束时间必须大于开始时间");
        }
        if (vote.getVoteName() == null || vote.getVoteName().isBlank()) {
            throw new RuntimeException("投票名称不能为空");
        }
        Vote result = voteDao.insert(vote);
        if (result == null) {
            throw new RuntimeException("创建投票失败");
        }
        return result;
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
     * @param voteId 投票id
     * @return 投票选项列表
     */
    public List<VoteOption> getVoteOptionList(Integer voteId) {
        return voteDao.getVoteOptionList(voteId);
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

    /**
     * 更新投票选项
     *
     * @param voteOption 投票选项
     * @param userId     用户id
     * @return 是否成功
     */
    public boolean updateVoteOption(VoteOption voteOption, Integer userId) {
        if (voteOption.getVoteOptionId() == null) {
            throw new RuntimeException("投票选项id不能为空");
        }
        VoteOption oldVoteOption = queryVoteOptionById(voteOption.getVoteOptionId());
        if (oldVoteOption == null) {
            throw new RuntimeException("投票选项不存在");
        }

        if (!Objects.equals(oldVoteOption.getVoteId(), voteOption.getVoteId())) {
            throw new RuntimeException("投票选项id与投票id不匹配");
        }

        Vote vote = queryVoteById(voteOption.getVoteId());
        if (vote == null) {
            throw new RuntimeException("投票不存在");
        }
        if (vote.getIsStarted()) {
            throw new RuntimeException("投票已经开始，不能修改投票选项");
        }

        User user = new UserService().getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!vote.getCreatedBy().equals(user.getUserId()) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("您不是投票创建者，不能修改投票选项");
        }

        if (voteOption.getName() != null && !voteOption.getName().isBlank()) {
            oldVoteOption.setName(voteOption.getName());
        }

        boolean success = voteDao.updateVoteOption(oldVoteOption);
        if (!success) {
            throw new RuntimeException("投票选项更新失败");
        }
        return true;
    }

    /**
     * 删除投票选项
     *
     * @param voteOption 投票选项
     * @param userId     用户id
     * @return 是否成功
     */
    public boolean deleteVoteOption(VoteOption voteOption, Integer userId) {
        if (voteOption.getVoteOptionId() == null) {
            throw new RuntimeException("投票选项id不能为空");
        }
        if (queryVoteById(voteOption.getVoteId()).getIsStarted()) {
            throw new RuntimeException("投票已经开始，不能删除投票选项");
        }

        VoteOption oldVoteOption = queryVoteOptionById(voteOption.getVoteOptionId());
        if (!Objects.equals(oldVoteOption.getVoteId(), voteOption.getVoteId())) {
            throw new RuntimeException("投票选项id与投票id不匹配");
        }

        User user = new UserService().getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Vote vote = queryVoteById(voteOption.getVoteId());
        if (vote == null) {
            throw new RuntimeException("投票不存在");
        }

        if (!vote.getCreatedBy().equals(user.getUserId()) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("您不是投票创建者，不能删除投票选项");
        }

        boolean success = voteDao.deleteVoteOption(voteOption);
        if (!success) {
            throw new RuntimeException("投票选项删除失败");
        }
        return true;
    }

    /**
     * 根据id查询投票选项
     *
     * @param id 投票选项id
     * @return 投票选项
     */
    public VoteOption queryVoteOptionById(Integer id) {
        return voteDao.queryVoteOptionById(id);
    }

    /**
     * 更新投票信息
     *
     * @param vote   投票
     * @param userId 用户id
     * @return 是否成功
     */
    public boolean updateVote(Vote vote, Integer userId) {
        if (vote.getVoteId() == null) {
            throw new RuntimeException("投票id不能为空");
        }
        Vote oldVote = queryVoteById(vote.getVoteId());
        if (oldVote == null) {
            throw new RuntimeException("投票不存在");
        }

        User user = new UserService().getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!oldVote.getCreatedBy().equals(user.getUserId()) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("您不是投票创建者，不能修改投票");
        }

        if (vote.getVoteName() != null && !vote.getVoteName().isBlank()) {
            oldVote.setVoteName(vote.getVoteName());
        }
        if (vote.getStartTime() != null) {
            oldVote.setStartTime(vote.getStartTime());
        }
        if (vote.getEndTime() != null) {
            oldVote.setEndTime(vote.getEndTime());
        }
        if (vote.getVoteIntro() != null && !vote.getVoteIntro().isBlank()) {
            oldVote.setVoteIntro(vote.getVoteIntro());
        }
        if (vote.getIsStarted() != null) {
            oldVote.setIsStarted(vote.getIsStarted());
        }

        if (oldVote.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() < oldVote.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()) {
            throw new RuntimeException("结束时间必须大于开始时间");
        }
        boolean success = voteDao.updateVote(oldVote);
        if (!success) {
            throw new RuntimeException("投票信息更新失败");
        }
        return true;
    }

    /**
     * 删除投票
     *
     * @param vote   投票
     * @param userId 用户id
     * @return 是否成功
     */
    public boolean deleteVote(Vote vote, Integer userId) {
        Vote oldVote = queryVoteById(vote.getVoteId());
        if (oldVote == null) {
            throw new RuntimeException("投票不存在");
        }
        User user = new UserService().getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!oldVote.getCreatedBy().equals(user.getUserId()) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("您不是投票创建者，不能删除投票");
        }

        boolean success = voteDao.deleteVote(vote);
        if (!success) {
            throw new RuntimeException("投票删除失败");
        }
        return true;
    }

    /**
     * 获取投票结果
     *
     * @param voteId 投票id
     * @param userId 用户id
     * @return 投票结果
     */
    public List<VoteOptionInfo> getVoteOptionStat(Integer voteId, Integer userId) {
        Vote vote = queryVoteById(voteId);
        if (vote == null) {
            throw new RuntimeException("投票不存在");
        }
        User user = new UserService().getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!vote.getCreatedBy().equals(userId) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("您不是投票创建者，不能查看投票结果");
        }
        return voteDao.queryVoteResultStat(voteId);
    }

    /**
     * 创建投票结果
     *
     * @param voter 投票结果
     */
    public void createVoteResult(Voter voter) {
        Vote vote = queryVoteById(voter.getVoteId());
        if (vote == null) {
            throw new RuntimeException("投票不存在");
        }
        if (!vote.getIsStarted()) {
            throw new RuntimeException("投票未开始");
        }
        VoteOption voteOption = queryVoteOptionById(voter.getOptionId());
        if (voteOption == null) {
            throw new RuntimeException("投票选项不存在");
        }
        if (!voteOption.getVoteId().equals(vote.getVoteId())) {
            throw new RuntimeException("投票选项id与投票id不匹配");
        }
        if (voteDao.queryVoteResultByUserId(voter.getUserId(), voter.getVoteId()) != null) {
            throw new RuntimeException("您已经投过票了");
        }
        boolean success = voteDao.insertVoteResult(voter);
        if (!success) {
            throw new RuntimeException("投票失败");
        }
        userPointLogService.changeUserPoint(
                voter.getUserId(),
                3,
                "参与投票" + vote.getVoteId()
        );
    }

    /**
     * 获取投票结果
     *
     * @param voteId 投票id
     * @param userId 用户id
     * @return 投票结果
     */
    public VoteOption getVoteResultByVoteId(Integer voteId, Integer userId) {
        return voteDao.queryVoteOptionById(voteDao.queryVoteResultByUserId(userId, voteId).getOptionId());
    }

    /**
     * 获取投票列表数量
     * @param status 状态
     * @return 投票列表数量
     */
    public Integer countVoteList(Integer status) {
        return voteDao.getVoteListCount(status);
    }
}
