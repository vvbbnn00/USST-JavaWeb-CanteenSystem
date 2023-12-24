package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Vote;
import cn.vvbbnn00.canteen.model.VoteOption;
import cn.vvbbnn00.canteen.model.VoteOptionInfo;
import cn.vvbbnn00.canteen.model.Voter;

import java.util.List;

public interface VoteDao {
    /**
     * 发起一个投票
     *
     * @param vote 投票，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    Vote insert(Vote vote);

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
    List<Vote> getVoteList(Integer page, Integer pageSize, Integer status, String orderBy, Boolean asc);

    /**
     * 根据id查询投票信息
     *
     * @param id 投票id
     * @return 投票信息
     */
    Vote queryVoteById(Integer id);

    /**
     * 返回投票选项列表
     *
     * @return 投票选项列表
     */
    List<VoteOption> getVoteOptionList(Integer voteId);

    /**
     * 新建投票选项
     *
     * @param voteOption 投票选项
     */
    void insertVoteOption(VoteOption voteOption);

    /**
     * 更新投票选项
     *
     * @param voteOption 投票选项
     * @return 是否更新成功
     */
    boolean updateVoteOption(VoteOption voteOption);

    /**
     * 删除投票选项
     *
     * @param voteOption 投票选项
     * @return 是否删除成功
     */
    boolean deleteVoteOption(VoteOption voteOption);

    /**
     * 更新投票信息
     *
     * @param vote 投票信息
     * @return 是否更新成功
     */
    boolean updateVote(Vote vote);

    /**
     * 删除投票
     *
     * @param vote 投票信息
     * @return 是否删除成功
     */
    boolean deleteVote(Vote vote);

    /**
     * 根据id查询投票选项
     *
     * @param id 投票选项id
     * @return 投票选项
     */
    VoteOption queryVoteOptionById(Integer id);

    /**
     * 插入投票结果
     *
     * @param voter 投票结果
     * @return 是否插入成功
     */
    boolean insertVoteResult(Voter voter);

    /**
     * 根据用户id和投票id查询投票结果
     *
     * @param userId 用户id
     * @param voteId 投票id
     * @return 投票结果
     */
    Voter queryVoteResultByUserId(Integer userId, Integer voteId);

    /**
     * 统计投票结果
     *
     * @param voteId 投票id
     * @return 投票结果
     */
    List<VoteOptionInfo> queryVoteResultStat(Integer voteId);

    /**
     * 获取投票列表数量
     *
     * @param status 状态
     * @return 投票列表数量
     */
    Integer getVoteListCount(Integer status);

}
