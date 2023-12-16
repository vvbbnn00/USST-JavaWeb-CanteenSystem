package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.CuisineDao;
import cn.vvbbnn00.canteen.dao.impl.CuisineDaoImpl;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.model.Cuisine;
import cn.vvbbnn00.canteen.model.User;

import java.util.List;
import java.util.logging.Logger;

public class CuisineService {

    private final CuisineDao cuisineDao = new CuisineDaoImpl();
    private final CanteenAdminService canteenAdminService = new CanteenAdminService();

    /**
     * 获取菜品列表
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @param kw       关键词
     * @param orderBy  排序字段
     * @param asc      是否升序
     * @param canteenId 食堂id
     * @return 菜品列表
     */
    public List<Cuisine> getCuisineList(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc, Integer canteenId) {
        return cuisineDao.queryCuisines(
                page,
                pageSize,
                kw,
                orderBy,
                asc,
                canteenId
        );
    }

    /**
     * 获取菜品列表数量
     *
     * @param kw 关键词
     * @param canteenId 食堂id
     * @return 菜品列表数量
     */
    public int getCuisineListCount(String kw, Integer canteenId) {
        return cuisineDao.queryCuisinesCount(
                kw,
                canteenId
        );
    }


    /**
     * 根据id获取菜品
     *
     * @param id 菜品id
     * @return 菜品
     */
    public Cuisine getCuisineById(Integer id) {

        return cuisineDao.queryCuisineById(id);

    }

    /**
     * 根据id删除菜品
     *
     * @param id 菜品id
     * @param user 用户，用于判断用户是否有权限删除菜品
     * @return 是否成功
     */
    public boolean deleteCuisineById(Integer id, User user) {
        Cuisine cuisine = getCuisineById(id);
        if (cuisine == null) {
            throw new RuntimeException("菜品不存在");
        }
        if (!canteenAdminService.checkHasCanteenAdmin(cuisine.getCanteenId(), user.getUserId())) {
            throw new RuntimeException("无权限删除菜品");
        }
        return cuisineDao.delete(id);
    }

    /**
     * 更新菜品信息
     *
     * @param cuisine 菜品，此处判断更新的菜品是根据id来判断的
     * @param user    用户，用于判断用户是否有权限更新菜品
     * @return 是否成功
     */
    public boolean updateCuisine(Cuisine cuisine, User user) {
        if (cuisine.getCuisineId() == null) {
            throw new RuntimeException("菜品id不能为空");
        }
        Cuisine existCuisine = getCuisineById(cuisine.getCuisineId());
        if (existCuisine == null) {
            throw new RuntimeException("菜品不存在");
        }
        if (cuisine.getName() != null) {
            existCuisine.setName(cuisine.getName());
        }
        if (!canteenAdminService.checkHasCanteenAdmin(existCuisine.getCanteenId(), user.getUserId())) {
            throw new RuntimeException("无权限更新菜品");
        }
        boolean result = cuisineDao.update(existCuisine);
        if (result) {
            return true;
        }
        throw new RuntimeException("更新菜品失败");
    }


    /**
     * 插入一个菜品
     *
     * @param cuisine 菜品，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     * @param user    用户，用于判断用户是否有权限插入菜品
     * @return 是否成功
     */
    public boolean createCuisine(Cuisine cuisine, User user) {
        if (cuisine.getName() == null || cuisine.getName().isEmpty()) {
            throw new RuntimeException("菜品名不能为空");
        }
        if (cuisine.getCanteenId() == null) {
            throw new RuntimeException("食堂id不能为空");
        }
        Canteen canteen = new CanteenService().getCanteenById(cuisine.getCanteenId());
        if (canteen == null) {
            throw new RuntimeException("食堂不存在");
        }
        if (!canteenAdminService.checkHasCanteenAdmin(cuisine.getCanteenId(), user.getUserId())) {
            throw new RuntimeException("无权限插入菜品");
        }
        boolean success = cuisineDao.insert(cuisine);
        if (!success) {
            throw new RuntimeException("插入菜品失败");
        }
        return true;
    }
}
