package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.CuisineDao;
import cn.vvbbnn00.canteen.dao.impl.CuisineDaoImpl;
import cn.vvbbnn00.canteen.model.Cuisine;

import java.util.List;

public class CuisineService {

    private final CuisineDao cuisineDao = new CuisineDaoImpl();

    /**
     * 获取菜品列表
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @param kw       关键词
     * @param orderBy  排序字段
     * @param asc      是否升序
     * @return 菜品列表
     */
    public List<Cuisine> getCuisineList(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc) {
        return cuisineDao.queryCuisines(
                page,
                pageSize,
                kw,
                orderBy,
                asc
        );
    }

    /**
     * 获取菜品列表数量
     *
     * @param kw 关键词
     * @return 菜品列表数量
     */
    public int getCuisineListCount(String kw) {
        return cuisineDao.queryCuisinesCount(
                kw
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
     * @return 是否成功
     */
    public boolean deleteCuisineById(Integer id) {
        return cuisineDao.delete(id);
    }

    /**
     * 更新菜品信息
     *
     * @param cuisine 菜品，此处判断更新的菜品是根据id来判断的
     * @return 是否成功
     */
    public boolean updateCuisine(Cuisine cuisine) {
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
        if (cuisine.getCanteenId() != null) {
            existCuisine.setCanteenId(cuisine.getCanteenId());
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
     * @return 是否成功
     */
    public boolean createCuisine(Cuisine cuisine) {
        if (cuisine.getName() == null || cuisine.getName().isEmpty()) {
            throw new RuntimeException("菜品名不能为空");
        }
        if (cuisine.getCanteenId() == null) {
            throw new RuntimeException("食堂id不能为空");
        }
        return cuisineDao.insert(cuisine);
    }
}
