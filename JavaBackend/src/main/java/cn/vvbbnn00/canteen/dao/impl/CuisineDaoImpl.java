package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.CuisineDao;
import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.model.Cuisine;
import cn.vvbbnn00.canteen.util.LogUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CuisineDaoImpl implements CuisineDao {
    @Override
    public int insert(Cuisine cuisine) {
        try (Connection connection = Hikari.getConnection()) {

        } catch (Exception e) {
            LogUtils.severe(e.getMessage());
            return -1;
        }
        return 0;
    }

    @Override
    public Cuisine queryCuisineById(Integer id) {
        return null;
    }

    @Override
    public boolean update(Cuisine cuisine) {
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        return false;
    }

    @Override
    public List<Cuisine> queryCuisines(Integer page, Integer pageSize, String kw, String orderBy, Boolean asc) {
        return null;
    }

    @Override
    public Integer queryCuisinesCount(String kw) {
        return null;
    }
}
