package cn.vvbbnn00.canteen.util;
import at.favre.lib.crypto.bcrypt.BCrypt;

public class SafetyUtils {
    /**
     * BCrypt加密
     *
     * @param password 密码
     * @return 加密后的密码
     */
    public static String passwordDoBCrypt(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    /**
     * BCrypt校验
     *
     * @param password 校验的密码
     * @param hash     加密后的密码
     * @return 校验结果
     */
    public static boolean passwordCheckBCrypt(String password, String hash) {
        BCrypt.Result result = BCrypt.verifyer().verify(password.toCharArray(), hash);
        return result.verified;
    }

    /**
     * XSS过滤
     *
     * @param str 过滤的字符串
     * @return 过滤后的字符串
     */
    public static String xssFilter(String str) {
        return str
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#39;")
                .replaceAll("\n", "<br/>")
                .replaceAll("\r", "");
    }
}
