package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.UserDao;
import cn.vvbbnn00.canteen.dao.UserPointLogDao;
import cn.vvbbnn00.canteen.dao.impl.UserDaoImpl;
import cn.vvbbnn00.canteen.dao.impl.UserPointLogDaoImpl;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserPointLog;
import cn.vvbbnn00.canteen.util.LogUtils;

public class UserPointLogService {

    private static final UserPointLogDao userPointLogDao = new UserPointLogDaoImpl();
    private static final UserDao userDao = new UserDaoImpl();
    private static final Integer MAX_TODAY_POINT = 1000;

    /**
     * 修改用户积分
     *
     * @param userId 用户ID
     * @param point  积分
     * @param detail 详情
     */
    public void changeUserPoint(Integer userId, Integer point, String detail) {
        try {
            User user = userDao.queryUserById(userId);
            if (user == null) {
                throw new RuntimeException("用户不存在");
            }
            Integer todayPoint = userPointLogDao.countTodayPointByUserId(userId);
            if (todayPoint == null) {
                todayPoint = 0;
            }
            if (todayPoint + point > MAX_TODAY_POINT) {
                LogUtils.info("用户" + userId + "今日积分已达上限");
                return;
            }
            userDao.changeUserPoint(userId, point);
            UserPointLog userPointLog = new UserPointLog();
            userPointLog.setUserId(userId);
            userPointLog.setPoint(point);
            userPointLog.setDetail(detail);
            userPointLogDao.insert(userPointLog);
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }
}
