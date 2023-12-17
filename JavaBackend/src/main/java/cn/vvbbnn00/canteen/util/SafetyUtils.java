package cn.vvbbnn00.canteen.util;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SafetyUtils {
    private final static String SHA_KEY = "msgDigest2023";

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

    /**
     * 登录签名函数
     *
     * @param data 需要签名的字符串
     * @param timestamp 时间戳
     * @return 返回签名结果
     */
    public static String doLoginSign(String data, String timestamp) {
        StringBuilder bytes = new StringBuilder();
        bytes.append(timestamp).append("\0");

        for (int i = 0; i < data.length(); i++) {
            char c = (char) ((data.charAt(i) % 256) ^ 10);
            bytes.append(c);
        }
        bytes.append(SHA_KEY);

        // LogUtils.info("bytes: " + bytes.toString());

        String sha1Data = sha256(bytes.toString());
        // LogUtils.info("sha1Data: " + sha1Data);

        return sha1Data.substring(20) + sha1Data.substring(0, 20);
    }

    /**
     * 生成SHA-256摘要
     *
     * @param input 需要生成的字符串
     * @return 返回SHA-256摘要
     */
    private static String sha256(String input) {
        try {
            MessageDigest mDigest = MessageDigest.getInstance("SHA-256");
            byte[] result = mDigest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : result) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not found", e);
        }
    }
}