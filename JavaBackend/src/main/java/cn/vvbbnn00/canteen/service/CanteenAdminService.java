package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.CanteenAdminDao;
import cn.vvbbnn00.canteen.dao.impl.CanteenAdminDaoImpl;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.model.CanteenAdmin;
import cn.vvbbnn00.canteen.model.User;

import java.util.List;

public class CanteenAdminService {
    private final CanteenAdminDao canteenAdminDao = new CanteenAdminDaoImpl();

    /**
     * 检查是否可以添加或移除餐厅管理员
     *
     * @param canteenId 餐厅ID
     * @param userId    用户ID
     */
    private void checkUpdateAvailable(Integer canteenId, Integer userId) {
        User user = new UserService().getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getRole() != User.Role.canteen_admin && user.getRole() != User.Role.admin) {
            throw new RuntimeException("用户不是餐厅管理员");
        }
        Canteen canteen = new CanteenService().getCanteenById(canteenId);
        if (canteen == null) {
            throw new RuntimeException("餐厅不存在");
        }
    }

    /**
     * 添加一个餐厅管理员
     *
     * @param canteenId 餐厅ID
     * @param userId    用户ID
     * @return 是否添加成功
     */
    public boolean addCanteenAdmin(Integer canteenId, Integer userId) {
        checkUpdateAvailable(canteenId, userId);
        CanteenAdmin canteenAdmin = new CanteenAdmin();
        canteenAdmin.setCanteenId(canteenId);
        canteenAdmin.setUserId(userId);
        List<CanteenAdmin> list = canteenAdminDao.query(canteenId, userId);
        if (!list.isEmpty()) {
            throw new RuntimeException("餐厅管理员已存在");
        }
        boolean result = canteenAdminDao.insert(canteenAdmin);
        if (!result) {
            throw new RuntimeException("添加失败");
        }
        return true;
    }


    /**
     * 移除一个餐厅管理员
     *
     * @param canteenId 餐厅ID
     * @param userId    用户ID
     * @return 是否移除成功
     */
    public boolean removeCanteenAdmin(Integer canteenId, Integer userId) {
        checkUpdateAvailable(canteenId, userId);
        CanteenAdmin canteenAdmin = new CanteenAdmin();
        canteenAdmin.setCanteenId(canteenId);
        canteenAdmin.setUserId(userId);

        boolean result = canteenAdminDao.remove(canteenAdmin);
        if (!result) {
            throw new RuntimeException("移除失败");
        }
        return true;
    }

    /**
     * 查询用户是否为餐厅管理员
     *
     * @param canteenId 餐厅ID
     * @param userId    用户ID
     * @return 是否为餐厅管理员
     */
    public boolean checkHasCanteenAdmin(Integer canteenId, Integer userId) {
        try {
            checkUpdateAvailable(canteenId, userId);
        } catch (Exception e) {
            // LogUtils.error(e.getMessage(), e);
            return false;
        }
        List<CanteenAdmin> list = canteenAdminDao.query(canteenId, userId);
        if (list == null) {
            throw new RuntimeException("查询失败");
        }
        return !list.isEmpty();
    }
}
