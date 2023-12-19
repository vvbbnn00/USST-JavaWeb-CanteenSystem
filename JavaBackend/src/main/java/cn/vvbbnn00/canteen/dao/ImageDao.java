package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Image;

import java.util.List;

/**
 * 图片数据访问对象接口
 */
public interface ImageDao {

    /**
     * 插入图片
     *
     * @param image 图片对象
     * @return 返回新插入的图片
     */
    Image insert(Image image);

    /**
     * 根据引用查询图片
     *
     * @param type        类型
     * @param referenceId 引用Id
     * @return 返回查询到的图片列表
     */
    List<Image> queryByReference(String type, Integer referenceId);

    /**
     * 根据Id查询图片
     *
     * @param imageId 图片Id
     * @return 返回查询到的图片
     */
    Image queryById(Integer imageId);

    /**
     * 根据Id删除图片
     *
     * @param imageId 图片Id
     * @return 如果成功删除则返回true，否则返回false
     */
    boolean delete(Integer imageId);

    /**
     * 根据引用删除图片
     *
     * @param type        类型
     * @param referenceId 引用Id
     * @return 如果成功删除则返回true，否则返回false
     */
    boolean deleteByReference(String type, Integer referenceId);
}