package cn.vvbbnn00.canteen.dao;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.model.CanteenAdmin;

import java.util.List;

public interface CanteenAdminDao {
    /**
     * 插入一个餐厅管理员对象
     * @param canteenAdmin 餐厅管理员对象
     * @return 是否插入成功
     */
    boolean insert(CanteenAdmin canteenAdmin);

    /**
     * 移除一个餐厅管理员
     * @param canteenAdmin 餐厅管理员对象
     * @return 是否移除成功
     */
    boolean remove(CanteenAdmin canteenAdmin);

    /**
     * 查询餐厅管理员列表
     * @param canteenId 可选，餐厅ID
     * @param userId 可选，用户ID
     * @return 满足条件的列表
     */
    List<CanteenAdmin> query(Integer canteenId, Integer userId);

    /**
     * Queries the list of CanteenAdmin objects based on the given canteenId.
     *
     * @param canteenId the canteen ID to search for
     * @return a list of CanteenAdmin objects that match the given canteenId
     */
    List<CanteenAdmin> queryByCanteenId(Integer canteenId);

    /**
     * Queries the list of CanteenAdmin objects based on the given userId.
     *
     * @param userId the user ID to search for
     * @return a list of CanteenAdmin objects that match the given userId
     */
    List<CanteenAdmin> queryByUserId(Integer userId);

}
