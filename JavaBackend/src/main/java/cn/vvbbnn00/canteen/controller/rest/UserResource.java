package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.BasicListRequest;
import cn.vvbbnn00.canteen.dto.request.UserListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.service.UserService;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
@RequestScoped
public class UserResource {
    @Context
    SecurityContext securityContext;
    UserService userService = new UserService();

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicListResponse restListUser(
            UserListRequest userListRequest
    ) {
        RequestValidatorUtils.doHibernateValidate(userListRequest);
        BasicListResponse response = new BasicListResponse();
        response.setList(userService.getUserList(
                userListRequest.getCurrentPage(),
                userListRequest.getPageSize(),
                userListRequest.getKw(),
                userListRequest.getAvailable(),
                userListRequest.getRole(),
                userListRequest.getIsVerified(),
                userListRequest.getOrderBy(),
                userListRequest.getAsc()
        ));
        response.setTotal(userService.getUserListCount(
                userListRequest.getKw(),
                userListRequest.getAvailable(),
                userListRequest.getRole(),
                userListRequest.getIsVerified()
        ));
        response.setPageSize(userListRequest.getPageSize());
        response.setCurrentPage(userListRequest.getCurrentPage());
        return response;
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse restPostUser(User user) {
        // 校验请求参数
        RequestValidatorUtils.doHibernateValidate(user);

        BasicDataResponse response = new BasicDataResponse();
        try {
            response.setData(userService.createUser(user));
        } catch (Exception e) {
            response.setCode(409);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @GET
    @Path("/me")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetMe() {
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
        BasicDataResponse response = new BasicDataResponse();
        User user = userService.getUserById(userId);
        response.setData(user);
        return response;
    }


    @GET
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetUser(
            @PathParam("userId") @NotNull @Min(value = 1, message = "无效的Id") Integer userId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(userId);

        User user = userService.getUserById(userId);
        if (user == null) {
            return new BasicDataResponse(404, "用户不存在");
        }
        BasicDataResponse response = new BasicDataResponse();
        response.setData(user);
        return response;
    }


    @PUT
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse restPutUser(
            @PathParam("userId") @NotNull @Min(value = 1, message = "无效的Id") Integer userId,
            User user
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(userId, user);
        // 校验请求参数
        RequestValidatorUtils.doHibernateValidate(user);

        BasicDataResponse response = new BasicDataResponse();
        user.setUserId(userId);
        try {
            response.setData(userService.updateUser(user));
        } catch (Exception e) {
            response.setCode(409);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @DELETE
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse restDeleteUser(
            @PathParam("userId") @NotNull @Min(value = 1, message = "无效的Id") Integer userId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(userId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            userService.deleteUser(userId);
        } catch (Exception e) {
            response.setCode(409);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}