package cn.vvbbnn00.canteen.filter.role_check;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.ServletMappingHelper;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * 根据注解检查用户角色
 */
@WebFilter(filterName = "RoleCheckServletFilter", urlPatterns = "/*")
public class RoleCheckServletFilter extends HttpFilter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpSession session = httpRequest.getSession();

        String path = httpRequest.getServletPath();

        // Restful API 的角色检查交给对应的Filter
        if (path.startsWith("/api/rest")) {
            chain.doFilter(request, response);
            return;
        }

        Class<?> clazz = ServletMappingHelper.getServletMapping().get(path);
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
                switch (RoleCheckFilter.checkRole(user, requiredRole)) {
                    case -1:
                        GsonFactory.makeErrorResponse((HttpServletResponse) response, 401, "Unauthorized");
                        return;
                    case 0:
                        GsonFactory.makeErrorResponse((HttpServletResponse) response, 403, "Forbidden");
                        return;
                }

                break;
            }
        }

        chain.doFilter(request, response);
    }
}
