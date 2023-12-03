package cn.vvbbnn00.canteen.util;

public class ConfigUtils {
    /**
     * 获取环境变量
     *
     * @param key          环境变量名
     * @param defaultValue 默认值
     * @return 环境变量值
     */
    public static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null) {
            value = defaultValue;
        }
        return value;
    }

}
