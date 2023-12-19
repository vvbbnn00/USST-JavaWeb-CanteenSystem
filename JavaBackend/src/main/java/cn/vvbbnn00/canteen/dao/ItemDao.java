package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Item;

import java.util.List;

public interface ItemDao {

    /**
     * 插入新菜品
     *
     * @param item 要插入的条目
     * @return 插入的条目
     */
    Item insert(Item item);

    /**
     * 根据ID查询菜品
     *
     * @param itemId 要查询的菜品的ID
     * @return 查询的菜品
     */
    Item queryById(Integer itemId);

    /**
     * 删除菜品
     *
     * @param itemId 要删除的菜品的ID
     * @return 是否删除成功
     */
    boolean delete(Integer itemId);

    /**
     * 更新菜品
     *
     * @param item 要更新的菜品
     * @return 是否更新成功
     */
    boolean update(Item item);

    /**
     * 查询菜品列表
     *
     * @param kw            关键词
     * @param cuisineId     菜系Id
     * @param canteenId     食堂Id
     * @param isRecommended 是否推荐
     * @param page          页码
     * @param pageSize      每页大小
     * @param orderBy       排序依据
     * @param asc           是否升序
     * @return 返回查询到的菜品列表
     */
    List<Item> query(String kw, Integer cuisineId, Integer canteenId, Boolean isRecommended,
                     Integer page, Integer pageSize, String orderBy, Boolean asc);


    /**
     * 查询菜品数量
     *
     * @param kw            关键词
     * @param cuisineId     菜系Id
     * @param canteenId     食堂Id
     * @param isRecommended 是否推荐
     * @return 返回查询到的菜品数量
     */
    Integer count(String kw, Integer cuisineId, Integer canteenId, Boolean isRecommended);
}