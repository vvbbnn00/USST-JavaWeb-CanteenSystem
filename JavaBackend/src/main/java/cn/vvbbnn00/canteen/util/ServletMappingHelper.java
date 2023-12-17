package cn.vvbbnn00.canteen.util;

import java.util.Map;

/**
 * 该工具类可生成一个HashMap，用于将Servlet的路径映射到对应的Servlet类
 * 该工具类的作用是为了解决在Filter中获取Servlet类的Class对象的问题
 */
public class ServletMappingHelper {
    // 全局唯一的HashMap，用于存储Servlet的路径映射
    private static Map<String, Class<?>> SERVLET_MAPPING = getServletMapping();

    public static Map<String, Class<?>> getServletMapping() {
        if (SERVLET_MAPPING != null) {
            return SERVLET_MAPPING;
        }

        // 生成Servlet的路径映射
        SERVLET_MAPPING = Map.ofEntries(

//                Map.entry("/restful/item/list", cn.vvbbnn00.canteen.controller.restful.CuisineResourceServlet.class),
//                Map.entry("/restful/item", cn.vvbbnn00.canteen.controller.restful.CuisineResourceServlet.class)
        );

        return SERVLET_MAPPING;
    }

}