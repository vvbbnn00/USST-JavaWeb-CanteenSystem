package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.service.UserNotificationService;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/user")
public class UserNotificationResource {
    @Context
    SecurityContext securityContext;

    @Context
    private HttpServletRequest request;
    UserNotificationService userNotificationService = new UserNotificationService();

    @GET
    @Path("/notification/list")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restListUserNotification(
    ) {
        BasicListResponse response = new BasicListResponse();
        User user = (User) request.getSession().getAttribute("user");
        try {
            response.setList(userNotificationService.getUserNotificationList(null, user.getUserId()));
            response.setTotal(userNotificationService.getUserNotificationList(null, user.getUserId()).size());
            response.setPageSize(200);
            response.setCurrentPage(1);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/notification/unread")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restListUnreadUserNotification(
    ) {
        BasicListResponse response = new BasicListResponse();
        User user = (User) request.getSession().getAttribute("user");
        try {
            response.setList(userNotificationService.getUserNotificationList(false, user.getUserId()));
            response.setTotal(userNotificationService.getUserNotificationList(false, user.getUserId()).size());
            response.setPageSize(200);
            response.setCurrentPage(1);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/notification/unread/count")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restCountReadUserNotification(
    ) {
        BasicDataResponse response = new BasicDataResponse();
        User user = (User) request.getSession().getAttribute("user");
        try {
            response.setData(userNotificationService.count(user.getUserId()));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PUT
    @Path("/notification/{notificationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicResponse restReadUserNotification(
            @PathParam("notificationId") @NotNull @Min(value = 1, message = "无效的Id") Integer notificationId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(notificationId);
        BasicResponse response = new BasicResponse();
        User user = (User) request.getSession().getAttribute("user");
        try {
            userNotificationService.readUserNotification(user.getUserId(), notificationId);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PUT
    @Path("/notification/all")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicResponse restReadAllUserNotification(
    ) {
        BasicResponse response = new BasicResponse();
        User user = (User) request.getSession().getAttribute("user");
        try {
            userNotificationService.readAllUserNotification(user.getUserId());
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @DELETE
    @Path("/notification/{notificationId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicResponse restDeleteUserNotification(
            @PathParam("notificationId") @NotNull @Min(value = 1, message = "无效的Id") Integer notificationId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(notificationId);
        BasicResponse response = new BasicResponse();
        User user = (User) request.getSession().getAttribute("user");
        try {
            userNotificationService.deleteUserNotification(notificationId, user.getUserId());
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
