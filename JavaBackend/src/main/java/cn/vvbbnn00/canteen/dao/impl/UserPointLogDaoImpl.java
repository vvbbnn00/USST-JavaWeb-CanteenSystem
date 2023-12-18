package cn.vvbbnn00.canteen.dao.impl;

import cn.vvbbnn00.canteen.dao.Hikari;
import cn.vvbbnn00.canteen.dao.UserPointLogDao;
import cn.vvbbnn00.canteen.model.UserPointLog;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.SqlStatementUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class UserPointLogDaoImpl implements UserPointLogDao {
    @Override
    public void insert(UserPointLog userPointLog) {
        try (Connection connection = Hikari.getConnection()) {
            PreparedStatement ps = SqlStatementUtils.generateInsertStatement(connection, userPointLog, new String[]{
                    "userId", "point", "detail"
            });
            ps.executeUpdate();
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
        }
    }
}
