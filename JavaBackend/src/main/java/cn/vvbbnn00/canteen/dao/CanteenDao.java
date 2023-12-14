package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Canteen;

import java.util.List;

public interface CanteenDao {
    /**
     * 插入一个食堂
     *
     * @param canteen 食堂，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     * @return 是否成功
     */
    int insert(Canteen canteen);

    /**
     * 根据id查询食堂
     *
     * @param id 食堂id
     * @return 食堂
     */
    Canteen queryCanteenById(Integer id);

    /**
     * 更新食堂信息
     *
     * @param canteen 食堂，此处判断更新的食堂是根据id来判断的
     * @return 是否成功
     */
    boolean update(Canteen canteen);

    /**
     * 根据id删除食堂
     *
     * @param id 食堂id
     * @return 是否成功
     */
    boolean delete(Integer id);

    /**
     * 查询食堂列表，参数为空可忽略这个条件
     *
     * @param page     页码 从1开始
     * @param pageSize 每页大小 默认10
     * @param kw       关键词，模糊匹配食堂名称
     * @param orderBy  排序字段，支持：id、name、createdAt、updatedAt
     * @param asc      是否升序
     * @return 食堂列表
     */
    List<Canteen> queryCanteens(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc);

    /**
     * 查询食堂数量，参数为空可忽略这个条件
     *
     * @param kw 关键词，模糊匹配食堂名称
     * @return 食堂数量
     */
    Integer queryCanteensCount(String kw);

    /**
     * Queries the list of Canteen objects based on the given list of canteen IDs.
     *
     * @param canteenIds the list of canteen IDs to search for
     * @return a list of Canteen objects that match the given canteen IDs
     */
    List<Canteen> batchQueryCanteens(List<Integer> canteenIds);
}
