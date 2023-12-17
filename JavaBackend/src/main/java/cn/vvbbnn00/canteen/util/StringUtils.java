package cn.vvbbnn00.canteen.util;

public class StringUtils {
    private static final String AVATAR_URL = "https://gravatar.kuibu.net/avatar/";

    /**
     * 将驼峰命名转换为下划线命名
     *
     * @param camelCaseString 驼峰命名的字符串
     * @return 下划线命名的字符串
     */
    public static String camelToSnake(String camelCaseString) {
        return camelCaseString.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }


    /**
     * 将下划线命名转换为驼峰命名
     *
     * @param snakeCaseString 下划线命名的字符串
     * @return 驼峰命名的字符串
     */
    public static String snakeToCamel(String snakeCaseString) {
        String[] words = snakeCaseString.split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(word.substring(0, 1).toUpperCase()).append(word.substring(1));
        }
        return sb.toString();
    }

    /**
     * MD5加密
     *
     * @param str 加密的字符串
     * @return 加密后的字符串
     */
    public static String md5(String str) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(str.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : array) {
                sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            LogUtils.error("MD5加密失败", e);
        }
        return "";
    }

    /**
     * 获取头像地址
     *
     * @param email 邮箱
     * @return 头像地址
     */
    public static String getAvatarUrl(String email) {
        if (email == null || email.isEmpty()) {
            return AVATAR_URL;
        }
        return AVATAR_URL + md5(email);
    }
}
