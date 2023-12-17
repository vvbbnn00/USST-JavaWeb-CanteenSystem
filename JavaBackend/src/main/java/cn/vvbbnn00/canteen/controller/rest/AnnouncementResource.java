package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.AnnouncementListRequest;
import cn.vvbbnn00.canteen.dto.request.UserIdRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.model.Announcement;
import cn.vvbbnn00.canteen.service.AnnouncementService;
import cn.vvbbnn00.canteen.service.CanteenAdminService;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/canteen")
@RequestScoped
public class AnnouncementResource {
    @Context
    SecurityContext securityContext;
    AnnouncementService announcementService = new AnnouncementService();

    @POST
    @Path("/announcement/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicListResponse restListAnnouncement(
            AnnouncementListRequest announcementListRequest
    ) {
        RequestValidatorUtils.doHibernateValidate(announcementListRequest);
        BasicListResponse response = new BasicListResponse();
        response.setList(announcementService.getAnnouncements(
                announcementListRequest.getCurrentPage(),
                announcementListRequest.getPageSize(),
                announcementListRequest.getCanteenId(),
                announcementListRequest.getKw(),
                announcementListRequest.getOrderBy(),
                announcementListRequest.getAsc()
        ));
        response.setTotal(announcementService.getAnnouncementsCount(
                announcementListRequest.getCanteenId(),
                announcementListRequest.getKw()
        ));
        response.setPageSize(announcementListRequest.getPageSize());
        response.setCurrentPage(announcementListRequest.getCurrentPage());
        return response;
    }

    @GET
    @Path("/{canteenId}/announcement/list")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restGetAnnouncementListByCanteenId(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId);
        BasicListResponse response = new BasicListResponse();
        try {
            List<?> result = announcementService.getAnnouncementsByCanteenId(canteenId);
            response.setList(result);
            response.setTotal(result.size());
            response.setCurrentPage(1);
            response.setPageSize(result.size());
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @POST
    @Path("/{canteenId}/announcement")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restAddAnnouncement(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            Announcement announcement
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, announcement);
        RequestValidatorUtils.doHibernateValidate(announcement);

        BasicDataResponse response = new BasicDataResponse();
        try {
            String userId = securityContext.getUserPrincipal().getName();
            Announcement result = announcementService.addAnnouncement(
                    announcement.getTitle(),
                    announcement.getContent(),
                    canteenId,
                    Integer.parseInt(userId)
            );
            if (result == null) {
                throw new RuntimeException("添加公告失败");
            }
            response.setData(result);
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @GET
    @Path("/{canteenId}/announcement/{announcementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetAnnouncement(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            @PathParam("announcementId") @NotNull @Min(value = 1, message = "无效的Id") Integer announcementId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, announcementId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Announcement result = announcementService.getAnnouncement(announcementId, canteenId);
            if (result == null) {
                throw new RuntimeException("公告不存在");
            }
            response.setData(result);
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @PUT
    @Path("/{canteenId}/announcement/{announcementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicResponse restPutAnnouncement(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            @PathParam("announcementId") @NotNull @Min(value = 1, message = "无效的Id") Integer announcementId,
            Announcement announcement
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, announcementId, announcement);
        RequestValidatorUtils.doHibernateValidate(announcement);

        BasicResponse response = new BasicResponse();
        try {
            String userId = securityContext.getUserPrincipal().getName();
            boolean result = announcementService.updateAnnouncement(
                    announcementId,
                    announcement.getTitle(),
                    announcement.getContent(),
                    canteenId,
                    Integer.parseInt(userId)
            );
            if (!result) {
                throw new RuntimeException("更新公告失败");
            }
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @DELETE
    @Path("/{canteenId}/announcement/{announcementId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicResponse restDeleteAnnouncement(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            @PathParam("announcementId") @NotNull @Min(value = 1, message = "无效的Id") Integer announcementId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, announcementId);

        BasicResponse response = new BasicResponse();
        try {
            String userId = securityContext.getUserPrincipal().getName();
            boolean result = announcementService.deleteAnnouncement(announcementId, canteenId, Integer.parseInt(userId));
            if (!result) {
                throw new RuntimeException("删除公告失败");
            }
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}