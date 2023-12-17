package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.AnnouncementDao;
import cn.vvbbnn00.canteen.dao.impl.AnnouncementDaoImpl;
import cn.vvbbnn00.canteen.model.Announcement;
import cn.vvbbnn00.canteen.model.Canteen;

import java.util.List;

public class AnnouncementService {
    CanteenAdminService canteenAdminService = new CanteenAdminService();
    AnnouncementDao announcementDao = new AnnouncementDaoImpl();
    CanteenService canteenService = new CanteenService();

    /**
     * 添加一个公告
     *
     * @param title     公告标题
     * @param content   公告内容
     * @param canteenId 餐厅ID
     * @param userId    用户ID
     * @return 添加的公告
     */
    public Announcement addAnnouncement(String title, String content, Integer canteenId, Integer userId) {
        if (!canteenAdminService.checkHasCanteenAdmin(canteenId, userId)) {
            throw new RuntimeException("您不是该餐厅的管理员");
        }
        Announcement announcement = new Announcement();
        announcement.setTitle(title);
        announcement.setContent(content);
        announcement.setCanteenId(canteenId);

        try {
            Integer insertId = announcementDao.insert(announcement);
            if (insertId == null) {
                throw new RuntimeException("添加公告失败");
            }
            return announcementDao.queryById(insertId);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 删除一个公告
     *
     * @param announcementId 公告ID
     * @param canteenId      餐厅ID
     * @param userId         用户ID
     * @return 是否删除成功
     */
    public boolean deleteAnnouncement(Integer announcementId, Integer canteenId, Integer userId) {
        if (!canteenAdminService.checkHasCanteenAdmin(canteenId, userId)) {
            throw new RuntimeException("您不是该餐厅的管理员");
        }
        Announcement announcement = announcementDao.queryById(announcementId);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        if (!announcement.getCanteenId().equals(canteenId)) {
            throw new RuntimeException("没有找到该公告");
        }
        return announcementDao.delete(announcementId);
    }

    /**
     * 更新一个公告
     *
     * @param announcementId 公告ID
     * @param title          公告标题
     * @param content        公告内容
     * @param canteenId      餐厅ID
     * @param userId         用户ID
     * @return 是否更新成功
     */
    public boolean updateAnnouncement(Integer announcementId, String title, String content, Integer canteenId, Integer userId) {
        if (!canteenAdminService.checkHasCanteenAdmin(canteenId, userId)) {
            throw new RuntimeException("您不是该餐厅的管理员");
        }
        Announcement announcement = announcementDao.queryById(announcementId);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        if (!announcement.getCanteenId().equals(canteenId)) {
            throw new RuntimeException("没有找到该公告");
        }
        announcement.setTitle(title);
        announcement.setContent(content);
        return announcementDao.update(announcement);
    }

    /**
     * 查询一个公告
     *
     * @param announcementId 公告ID
     * @param canteenId      餐厅ID
     * @return 查询到的公告
     */
    public Announcement getAnnouncement(Integer announcementId, Integer canteenId) {
        Announcement announcement = announcementDao.queryById(announcementId);
        if (announcement == null) {
            throw new RuntimeException("公告不存在");
        }
        if (!announcement.getCanteenId().equals(canteenId)) {
            throw new RuntimeException("没有找到该公告");
        }
        return announcement;
    }

    /**
     * 查询公告列表
     *
     * @param page      页数
     * @param pageSize  每页公告数量
     * @param canteenId 餐厅ID
     * @param kw        关键字
     * @param orderBy   排序字段
     * @param asc       是否升序
     * @return 公告列表
     */
    public List<Announcement> getAnnouncements(Integer page, Integer pageSize, Integer canteenId, String kw, String orderBy, Boolean asc) {
        return announcementDao.query(page, pageSize, canteenId, kw, orderBy, asc);
    }

    /**
     * 查询公告列表数量
     *
     * @param canteenId 餐厅ID
     * @param kw        关键字
     * @return 公告列表数量
     */
    public int getAnnouncementsCount(Integer canteenId, String kw) {
        return announcementDao.count(canteenId, kw);
    }

    /**
     * 查询餐厅的公告列表
     *
     * @param canteenId 餐厅ID
     * @return 公告列表
     */
    public List<Announcement> getAnnouncementsByCanteenId(Integer canteenId) {
        Canteen canteen = canteenService.getCanteenById(canteenId);
        if (canteen == null) {
            throw new RuntimeException("餐厅不存在");
        }
        return announcementDao.queryByCanteenId(canteenId);
    }
}
