package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import org.hibernate.validator.constraints.Length;

@Path("/hello")
@RequestScoped
public class HelloResource {
    @Context
    SecurityContext securityContext;

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse hello(
            @PathParam("id") @NotNull @Min(value = 5, message = "无效的Id") Integer id,
            @QueryParam("name") @Length(min = 1, max = 20, message = "用户名的长度不符合要求") String name
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(id, name);
        BasicDataResponse response = new BasicDataResponse();
        response.setMessage("Hello, " + name + "!");
        response.setData(id);
        return response;
    }

    @POST
    @Path("/post")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse helloPost(User user) {
        // 校验请求参数
        RequestValidatorUtils.doHibernateValidate(user);
        String username = securityContext.getUserPrincipal().getName();
        BasicDataResponse response = new BasicDataResponse();
        response.setMessage("Hello, " + username + "!");
        response.setData(user.getUserId());
        return response;
    }
}