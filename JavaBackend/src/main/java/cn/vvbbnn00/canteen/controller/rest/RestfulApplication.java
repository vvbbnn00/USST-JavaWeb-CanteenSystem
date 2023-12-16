package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.filter.IllegalArgumentExceptionMapper;
import cn.vvbbnn00.canteen.filter.SecurityContextRestfulFilter;
import cn.vvbbnn00.canteen.filter.role_check.RoleCheckRestfulFilter;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api/rest")
public class RestfulApplication extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();

        // include filters
        classes.add(RoleCheckRestfulFilter.class);
        classes.add(SecurityContextRestfulFilter.class);
        classes.add(IllegalArgumentExceptionMapper.class);

        // include hello resource
        classes.add(HelloResource.class);
        return classes;
    }
}
