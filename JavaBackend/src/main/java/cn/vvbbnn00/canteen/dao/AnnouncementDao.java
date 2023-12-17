package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Announcement;

import java.util.List;

/**
 * 这是公告数据接口
 */
public interface AnnouncementDao {

    /**
     * 插入新公告的方法
     *
     * @param announcement 公告内容
     * @return 新公告的编号，如果插入失败，返回null
     */
    Integer insert(Announcement announcement);

    /**
     * 删除一个公告的方法
     *
     * @param announcementId 公告编号
     * @return 如果删除成功，则返回真，否则返回假
     */
    boolean delete(Integer announcementId);

    /**
     * 更新公告的方法
     *
     * @param announcement 公告内容
     * @return 如果更新成功，则返回真，否则返回假
     */
    boolean update(Announcement announcement);

    /**
     * 根据编号查询公告的方法
     *
     * @param announcementId 公告的编号
     * @return retorno 公告信息，如果编号不存在，则返回null
     */
    Announcement queryById(Integer announcementId);

    /**
     * 根据餐厅编号查询公告的方法
     *
     * @param canteenId 食堂编号
     * @return 返回该餐厅的所有公告，如果无公告，则返回空列表
     */
    List<Announcement> queryByCanteenId(Integer canteenId);

    /**
     * 查询公告列表的方法
     *
     * @param page      页数
     * @param pageSize  每页公告数量
     * @param canteenId 餐厅编号
     * @param kw        关键字
     * @param orderBy   排序字段
     * @param asc       是否升序
     * @return 符合条件的公告列表
     */
    List<Announcement> query(Integer page, Integer pageSize, Integer canteenId, String kw, String orderBy, Boolean asc);

    /**
     * 查询公告数量的方法
     *
     * @param canteenId 餐厅编号
     * @param kw        关键字
     * @return 符合条件的公告数量
     */
    Integer count(Integer canteenId, String kw);
}