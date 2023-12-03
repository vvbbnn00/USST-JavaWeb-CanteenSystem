package cn.vvbbnn00.canteen.util;

public class StringUtils {
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
}
