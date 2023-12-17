package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.filter.AllowCorsRestFilter;
import cn.vvbbnn00.canteen.filter.IllegalArgumentExceptionMapper;
import cn.vvbbnn00.canteen.filter.SecurityContextRestfulFilter;
import cn.vvbbnn00.canteen.filter.role_check.RoleCheckRestfulFilter;
import cn.vvbbnn00.canteen.model.adapter.JacksonContextResolver;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/rest")
public class RestfulApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();
        // include resolver
        classes.add(JacksonContextResolver.class);

        // include filters
        classes.add(RoleCheckRestfulFilter.class);
        classes.add(SecurityContextRestfulFilter.class);
        classes.add(IllegalArgumentExceptionMapper.class);
        classes.add(AllowCorsRestFilter.class);

        // include resource
        classes.add(UserResource.class);
        classes.add(CanteenResource.class);
        classes.add(CanteenAdminResource.class);
        classes.add(CuisineResource.class);
        classes.add(AnnouncementResource.class);
        classes.add(ImageResource.class);
        classes.add(ItemResource.class);

        return classes;
    }
}
