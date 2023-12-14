package cn.vvbbnn00.canteen.filter.role_check;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.GsonFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;

import java.io.IOException;
import java.lang.reflect.Method;

public class RoleCheckRestfulFilter implements ContainerRequestFilter {

    @Context
    private ResourceInfo resourceInfo;

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        HttpSession session = request.getSession();

        // 获取资源类和方法
        Method method = resourceInfo.getResourceMethod();

        // 检查是否存在 CheckPermission 注解
        if (method.isAnnotationPresent(CheckRole.class)) {
            CheckRole permission = method.getAnnotation(CheckRole.class);
            String requiredRole = permission.value();
            User user = (User) session.getAttribute("user");
            switch (RoleCheckFilter.checkRole(user, requiredRole)) {
                case -1:
                    requestContext.abortWith(GsonFactory.generateErrorResponse(401, "Unauthorized"));
                    return;
                case 0:
                    requestContext.abortWith(GsonFactory.generateErrorResponse(403, "Forbidden"));
                    return;
            }
        }
    }
}