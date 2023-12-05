package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.CanteenDao;
import cn.vvbbnn00.canteen.dao.UserDao;
import cn.vvbbnn00.canteen.dao.impl.CanteenDaoImpl;
import cn.vvbbnn00.canteen.dao.impl.UserDaoImpl;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.util.ConfigUtils;
import cn.vvbbnn00.canteen.util.SafetyUtils;

import java.math.BigDecimal;
import java.util.List;

public class CanteenService {
    private final CanteenDao canteenDao = new CanteenDaoImpl();

    /**
     * 获取食堂列表
     *
     * @param page     页码
     * @param pageSize 每页大小
     * @param kw       关键词
     * @param orderBy  排序字段
     * @param asc      是否升序
     * @return 食堂列表
     */
    public List<Canteen> getCanteenList(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc) {
        return canteenDao.queryCanteens(
                page,
                pageSize,
                kw,
                orderBy,
                asc
        );
    }

    /**
     * 获取食堂列表数量
     *
     * @param kw 关键词
     * @return 食堂列表数量
     */
    public int getCanteenListCount(String kw) {
        return canteenDao.queryCanteensCount(
                kw
        );
    }

    /**
     * 根据id获取食堂
     *
     * @param id 食堂id
     * @return 食堂
     */
    public Canteen getCanteenById(Integer id) {

        return canteenDao.queryCanteenById(id);

    }

    /**
     * 更新食堂信息
     *
     * @param canteen 食堂
     * @return 更新成功的食堂
     */
    public Canteen updateCanteen(Canteen canteen) {
        if (canteen.getCanteenId() == null) {
            throw new RuntimeException("食堂id不能为空");
        }
        Canteen existCanteen = getCanteenById(canteen.getCanteenId());
        if (existCanteen == null) {
            throw new RuntimeException("食堂不存在");
        }
        if (canteen.getLocation() != null) {
            existCanteen.setLocation(canteen.getLocation());
        }
        if (canteen.getName() != null) {
            existCanteen.setName(canteen.getName());
        }
        if (canteen.getIntroduction() != null) {
            existCanteen.setIntroduction(canteen.getIntroduction());
        }
        if (canteen.getCompScore() != null) {
            existCanteen.setCompScore(canteen.getCompScore());
        }
        boolean success = canteenDao.update(existCanteen);
        if (success) {
            return getCanteenById(existCanteen.getCanteenId());
        }
        throw new RuntimeException("更新食堂失败");
    }


    /**
     * 创建食堂
     *
     * @param canteen 食堂
     * @return 创建成功的食堂
     */
    public Canteen createCanteen(Canteen canteen) {
        if (canteen.getName() == null || canteen.getName().isEmpty()) {
            throw new RuntimeException("食堂名不能为空");
        }
        if (canteen.getLocation() == null || canteen.getLocation().isEmpty()) {
            throw new RuntimeException("食堂位置不能为空");
        }
        if (canteen.getIntroduction() == null || canteen.getIntroduction().isEmpty()) {
            throw new RuntimeException("食堂介绍不能为空");
        }
        canteen.setCompScore(BigDecimal.valueOf(0.00D));
        int success = canteenDao.insert(canteen);
        if (success != -1) {
            return getCanteenById(canteen.getCanteenId());
        }
        throw new RuntimeException("创建食堂失败");
    }

    public void deleteCanteen(Integer canteenId) {
        Canteen existCanteen = getCanteenById(canteenId);
        if (existCanteen == null) {
            throw new RuntimeException("食堂不存在");
        }
        boolean success = canteenDao.delete(canteenId);
        if (!success) {
            throw new RuntimeException("删除食堂失败");
        }
    }
}



