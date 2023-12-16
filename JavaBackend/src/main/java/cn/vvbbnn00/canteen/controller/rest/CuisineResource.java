package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.CuisineListRequest;
import cn.vvbbnn00.canteen.dto.request.UserIdRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.Cuisine;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.service.CuisineService;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

@Path("/canteen")
@RequestScoped
public class CuisineResource {
    @Context
    SecurityContext securityContext;

    @Context
    private HttpServletRequest request;

    CuisineService cuisineService = new CuisineService();

    @POST
    @Path("/{canteenId}/cuisine/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restListCuisine(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            CuisineListRequest cuisineListRequest
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, cuisineListRequest);
        RequestValidatorUtils.doHibernateValidate(cuisineListRequest);

        BasicListResponse response = new BasicListResponse();
        response.setList(cuisineService.getCuisineList(
                cuisineListRequest.getCurrentPage(),
                cuisineListRequest.getPageSize(),
                cuisineListRequest.getKw(),
                cuisineListRequest.getOrderBy(),
                cuisineListRequest.getAsc(),
                canteenId
        ));
        response.setTotal(cuisineService.getCuisineListCount(
                cuisineListRequest.getKw(),
                canteenId
        ));
        return response;
    }


    @GET
    @Path("/{canteenId}/cuisine/{cuisineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetCuisine(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            @PathParam("cuisineId") @NotNull @Min(value = 1, message = "无效的Id") Integer cuisineId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, cuisineId);

        BasicDataResponse response = new BasicDataResponse();
        Cuisine cuisine = cuisineService.getCuisineById(cuisineId);
        if (cuisine == null) {
            return new BasicDataResponse(404, "菜系不存在");
        }
        response.setData(cuisine);
        return response;
    }


    @POST
    @Path("/{canteenId}/cuisine")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restPostCuisine(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            Cuisine cuisine
    ) {
        // 校验请求参数
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, cuisine);
        RequestValidatorUtils.doHibernateValidate(cuisine);

        BasicDataResponse response = new BasicDataResponse();
        try {
            User user = (User) request.getSession().getAttribute("user");
            cuisine.setCanteenId(canteenId);
            response.setData(cuisineService.createCuisine(cuisine, user));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PUT
    @Path("/{canteenId}/cuisine/{cuisineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restPutCuisine(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            @PathParam("cuisineId") @NotNull @Min(value = 1, message = "无效的Id") Integer cuisineId,
            Cuisine cuisine
    ) {
        // 校验请求参数
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, cuisineId, cuisine);
        RequestValidatorUtils.doHibernateValidate(cuisine);

        BasicDataResponse response = new BasicDataResponse();
        try {
            User user = (User) request.getSession().getAttribute("user");
            cuisine.setCuisineId(cuisineId);
            response.setData(cuisineService.updateCuisine(cuisine, user));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @DELETE
    @Path("/{canteenId}/cuisine/{cuisineId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restDeleteCuisine(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            @PathParam("cuisineId") @NotNull @Min(value = 1, message = "无效的Id") Integer cuisineId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, cuisineId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            cuisineService.deleteCuisineById(cuisineId, (User) request.getSession().getAttribute("user"));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
