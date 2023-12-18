package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserMessage;
import cn.vvbbnn00.canteen.service.UserNotificationService;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

public class UserNotificationResource {
    @Path("/user/notification")
    public class UserNotificationResource {
        @Context
        SecurityContext securityContext;

        @Context
        private HttpServletRequest request;
        UserNotificationService userNotificationService = new UserNotificationService();
        @GET
        @Path("/target/list")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        @CheckRole("user")
        public BasicListResponse restListUser(
        ) {
            BasicListResponse response = new BasicListResponse();
            User user = (User) request.getSession().getAttribute("user");
            try {
                response.setList(userNotificationService.getUserNotificationList(user.getUserId()));
            } catch (Exception e) {
                response.setCode(409);
                response.setMessage(e.getMessage());
            }
            return response;
        }

        @GET
        @Path("/target/{userId}")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        @CheckRole("user")
        public BasicListResponse restListUserNotification(
                @PathParam("userId") @NotNull @Min(value = 1, message = "无效的Id") Integer userId
        ) {
            RequestValidatorUtils.doHibernateParamsValidate(userId);
            BasicListResponse response = new BasicListResponse();
            User user = (User) request.getSession().getAttribute("user");
            try {
                response.setList(userNotificationService.getUserNotificationList(user.getUserId(),userId));
            } catch (Exception e) {
                response.setCode(409);
                response.setMessage(e.getMessage());
            }
            return response;
        }
        @POST
        @Path("/target/{userId}")
        @Produces(MediaType.APPLICATION_JSON)
        @Consumes(MediaType.APPLICATION_JSON)
        @CheckRole("user")
        public BasicResponse restInsertUserNotification(
                @PathParam("userId") @NotNull @Min(value = 1, message = "无效的Id") Integer userId,
                UserMessage userMessage
        ) {
            RequestValidatorUtils.doHibernateParamsValidate(userId);
            BasicResponse response = new BasicResponse();
            User user = (User) request.getSession().getAttribute("user");
            try {
                userNotificationService.insertNotification(user.getUserId(),userId,userMessage.getContent());
            } catch (Exception e) {
                response.setCode(409);
                response.setMessage(e.getMessage());
            }
            return response;
        }
    }
