package cn.vvbbnn00.canteen.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 1. 标注该注解的方法，在执行前会检查用户是否包含指定角色，若不包含则会返回401或者403<br>
 * 2. 请注意，该注解只有标注在Controller的<b>方法</b>上才会生效，请在使用前，于类RoleCheckFilter中绑定对应的路由，否则不会生效
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CheckRole {
    String value();
}
