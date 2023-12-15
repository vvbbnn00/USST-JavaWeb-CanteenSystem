package cn.vvbbnn00.canteen.filter;

import cn.vvbbnn00.canteen.filter.role_check.RoleCheckFilter;
import cn.vvbbnn00.canteen.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

import java.security.Principal;

/**
 * This class implements the ContainerRequestFilter interface, allowing it to act as a filter for restful requests.
 * It provides a custom security context by implementing the SecurityContext interface and overriding its methods.
 * The class is responsible for filtering incoming requests and setting the custom security context in the request context.
 */
public class SecurityContextRestfulFilter implements ContainerRequestFilter {

    @Context
    private HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        SecurityContext customSecurityContext = new SecurityContext() {
            final User user = (User) request.getSession().getAttribute("user"); // 从session中获取用户

            @Override
            public Principal getUserPrincipal() {
                if (user == null) {
                    return null;
                }
                return user.getUserId().toString()::toString;
            }

            @Override
            public boolean isUserInRole(String role) {
                return RoleCheckFilter.checkRole(user, role) == 1;
            }

            @Override
            public boolean isSecure() {
                return request.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return "Bearer";
            }
        };

        requestContext.setSecurityContext(customSecurityContext);
    }
}
