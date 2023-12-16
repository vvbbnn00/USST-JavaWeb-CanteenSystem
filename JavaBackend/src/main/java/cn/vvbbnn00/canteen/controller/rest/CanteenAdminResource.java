package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.UserIdRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.service.CanteenAdminService;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/canteen")
@RequestScoped
public class CanteenAdminResource {
    @Context
    SecurityContext securityContext;
    CanteenAdminService canteenAdminService = new CanteenAdminService();

    @GET
    @Path("/managed")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restGetUserManagedCanteens() {
        BasicDataResponse response = new BasicDataResponse();
        try {
            String userId = securityContext.getUserPrincipal().getName();
            response.setData(canteenAdminService.getUserManagedCanteens(Integer.parseInt(userId)));
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/{canteenId}/admins")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse restGetCanteenAdminList(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId);
        BasicDataResponse response = new BasicDataResponse();
        try {
            response.setData(canteenAdminService.getCanteenAdminList(canteenId));
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @POST
    @Path("/{canteenId}/admins")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicResponse restAddCanteenAdmin(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            UserIdRequest userIdRequest
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, userIdRequest);
        RequestValidatorUtils.doHibernateValidate(userIdRequest);

        BasicResponse response = new BasicResponse();
        try {
            canteenAdminService.addCanteenAdmin(canteenId, userIdRequest.getUserId());
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @DELETE
    @Path("/{canteenId}/admins/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicResponse restDeleteCanteenAdmin(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            @PathParam("userId") @NotNull @Min(value = 1, message = "无效的Id") Integer userId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, userId);

        BasicResponse response = new BasicResponse();
        try {
            canteenAdminService.removeCanteenAdmin(canteenId, userId);
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
