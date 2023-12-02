package cn.vvbbnn00.canteen.dao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Hikari数据库连接池类，用于获取数据库连接
 */
public class Hikari {
    private static final HikariDataSource HIKARI_DATA_SOURCE;
    private static final String DB_NAME = "canteen_community";

    static {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(getEnv("DB_URL", "jdbc:mysql://192.168.19.2:3306/canteen_community?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&useSSL=false"));
        config.setUsername(getEnv("DB_USERNAME", "root"));
        config.setPassword(getEnv("DB_PASSWORD", "password"));
        config.setDriverClassName(getEnv("DB_DRIVER", "com.mysql.cj.jdbc.Driver"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        HIKARI_DATA_SOURCE = new HikariDataSource(config);
    }

    /**
     * 获取一个数据库连接
     *
     * @return 数据库连接
     */
    public static Connection getConnection() throws SQLException {
        return HIKARI_DATA_SOURCE.getConnection();
    }

    /**
     * 关闭数据库连接池
     */
    public static void close() {
        if (HIKARI_DATA_SOURCE != null && !HIKARI_DATA_SOURCE.isClosed()) {
            HIKARI_DATA_SOURCE.close();
        }
    }

    /**
     * 获取环境变量
     *
     * @param key          环境变量名
     * @param defaultValue 默认值
     * @return 环境变量值
     */
    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * 获取数据库名
     * @return 数据库名
     */
    public static String getDbName() {
        return DB_NAME;
    }
}