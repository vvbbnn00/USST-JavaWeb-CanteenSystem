package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Cuisine;

import java.util.List;

public interface CuisineDao {
    /**
     * 插入一个菜品
     *
     * @param cuisine 菜品，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     * @return 是否成功
     */
    boolean insert(Cuisine cuisine);

    /**
     * 根据id查询菜品
     *
     * @param id 菜品id
     * @return 菜品
     */
    Cuisine queryCuisineById(Integer id);

    /**
     * 更新菜品信息
     *
     * @param cuisine 菜品，此处判断更新的菜品是根据id来判断的
     * @return 是否成功
     */
    boolean update(Cuisine cuisine);

    /**
     * 根据id删除菜品
     *
     * @param id 菜品id
     * @return 是否成功
     */
    boolean delete(Integer id);

    /**
     * 查询菜品列表，参数为空可忽略这个条件
     *
     * @param kw        关键词，模糊匹配菜品名称
     * @param orderBy   排序字段，支持：id、name、createdAt、updatedAt
     * @param asc       是否升序
     * @param canteenId 食堂id
     * @return 菜品列表
     */
    List<Cuisine> queryCuisines(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc, Integer canteenId);

    /**
     * 查询菜品数量，参数为空可忽略这个条件
     *
     * @param kw        关键词，模糊匹配菜品名称
     * @param canteenId 食堂id
     * @return 菜品数量
     */
    Integer queryCuisinesCount(String kw, Integer canteenId);
}
