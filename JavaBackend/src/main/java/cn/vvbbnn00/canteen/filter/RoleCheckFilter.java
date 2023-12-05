package cn.vvbbnn00.canteen.filter;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.GsonFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 根据注解检查用户角色
 */
@WebFilter(filterName = "RoleCheckFilter", urlPatterns = "/*")
public class RoleCheckFilter extends HttpFilter {
    private Map<String, Class<?>> servletMapping;

    @Override
    public void init(FilterConfig filterConfig) {
        // 添加映射后，请手动重启Tomcat，否则不会生效（Rebel插件也需要重启）
        servletMapping = new HashMap<>();
        servletMapping.put("/admin/user", cn.vvbbnn00.canteen.controller.admin.UserResourceServlet.class);
        servletMapping.put("/admin/user/list", cn.vvbbnn00.canteen.controller.admin.UserListServlet.class);
        servletMapping.put("/admin/canteen", cn.vvbbnn00.canteen.controller.admin.CanteenResourceServlet.class);
        servletMapping.put("/admin/canteen/list", cn.vvbbnn00.canteen.controller.admin.CanteenListServlet.class);
        // 为其他Servlet添加更多映射
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();

        Class<?> clazz = servletMapping.get(httpRequest.getServletPath());
        // LogUtils.info(httpRequest.getServletPath() + " " + clazz);
        if (clazz == null) {
            chain.doFilter(request, response);
            return;
        }

        // 反射查找对应的方法和注解
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(CheckRole.class)) {
                CheckRole annotation = method.getAnnotation(CheckRole.class);
                String requiredRole = annotation.value();

                // 获取用户角色
                User user = (User) session.getAttribute("user");

                // 若用户未登录，则返回401
                if (user == null) {
                    GsonFactory.makeErrorResponse((HttpServletResponse) response, 401, "Unauthorized");
                    return;
                }

                boolean checkPass = false;
                String userRole = user.getRole().toString();
                switch (requiredRole) {
                    case "admin":
                        if (userRole.equals(User.Role.admin.toString())) {
                            checkPass = true;
                        }
                        break;
                    case "canteen_admin":
                        if (userRole.equals(User.Role.canteen_admin.toString())) {
                            checkPass = true;
                        }
                        if (userRole.equals(User.Role.admin.toString())) {
                            checkPass = true;
                        }
                        break;
                    default:
                        checkPass = true;
                        break;
                }

                if (!checkPass) {
                    GsonFactory.makeErrorResponse((HttpServletResponse) response, 403, "Forbidden");
                    return;
                }

                break;
            }
        }

        chain.doFilter(request, response);
    }

}
