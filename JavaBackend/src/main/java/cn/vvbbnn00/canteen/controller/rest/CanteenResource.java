package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.CanteenListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.service.CanteenService;
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
public class CanteenResource {
    @Context
    SecurityContext securityContext;
    CanteenService canteenService = new CanteenService();

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restListCanteen(
            CanteenListRequest canteenListRequest
    ) {
        RequestValidatorUtils.doHibernateValidate(canteenListRequest);
        BasicListResponse response = new BasicListResponse();
        response.setList(canteenService.getCanteenList(
                canteenListRequest.getCurrentPage(),
                canteenListRequest.getPageSize(),
                canteenListRequest.getKw(),
                canteenListRequest.getOrderBy(),
                canteenListRequest.getAsc()
        ));
        response.setTotal(canteenService.getCanteenListCount(canteenListRequest.getKw()));
        response.setPageSize(canteenListRequest.getPageSize());
        response.setCurrentPage(canteenListRequest.getCurrentPage());
        return response;
    }

    @GET
    @Path("/{canteenId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetCanteen(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId);

        BasicDataResponse response = new BasicDataResponse();
        Canteen canteen = canteenService.getCanteenById(canteenId);
        if (canteen == null) {
            return new BasicDataResponse(404, "食堂不存在");
        }
        response.setData(canteen);
        return response;
    }

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse restPostCanteen(Canteen canteen) {
        // 校验请求参数
        RequestValidatorUtils.doHibernateValidate(canteen);

        BasicDataResponse response = new BasicDataResponse();
        try {
            response.setData(canteenService.createCanteen(canteen));
        } catch (Exception e) {
            response.setCode(409);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @PUT
    @Path("/{canteenId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse restPutCanteen(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            Canteen canteen
    ) {
        // 校验请求参数
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, canteen);
        RequestValidatorUtils.doHibernateValidate(canteen);

        BasicDataResponse response = new BasicDataResponse();
        try {
            canteen.setCanteenId(canteenId);
            response.setData(canteenService.updateCanteen(canteen));
        } catch (Exception e) {
            response.setCode(409);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @DELETE
    @Path("/{canteenId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("admin")
    public BasicDataResponse restDeleteCanteen(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            canteenService.deleteCanteen(canteenId);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}