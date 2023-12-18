package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.UserPointLog;

public interface UserPointLogDao {

    /**
     * 这个方法是用来插入一个UserPointLog实例到数据库中。
     *
     * @param userPointLog 是 一个 UserPointLog 实例 ，这个实例包含了用户的点数日志。
     */
    public void insert(UserPointLog userPointLog);
}