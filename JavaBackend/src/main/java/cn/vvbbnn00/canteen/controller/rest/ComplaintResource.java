package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.ComplaintListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.Complaint;
import cn.vvbbnn00.canteen.service.ComplaintService;
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
public class ComplaintResource {

    @Context
    SecurityContext securityContext;

    ComplaintService complaintService = new ComplaintService();

    @POST
    @Path("/{canteenId}/complaint")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restCreateComplaint(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的Id") Integer canteenId,
            Complaint complaint
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, complaint);
        RequestValidatorUtils.doHibernateValidate(complaint);

        BasicDataResponse response = new BasicDataResponse();
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        complaint.setCreatedBy(userId);
        complaint.setCanteenId(canteenId);
        try {
            Complaint newComplaint = complaintService.createComplaint(complaint, complaint.getImageList());
            response.setData(newComplaint);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @POST
    @Path("/complaint/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restListComplaint(
            ComplaintListRequest complaintListRequest
    ) {
        RequestValidatorUtils.doHibernateValidate(complaintListRequest);
        BasicListResponse response = new BasicListResponse();
        String status = complaintListRequest.getStatus();
        Complaint.Status statusEnum = null;
        try {
            if (status != null) {
                statusEnum = Complaint.Status.valueOf(status);
            }
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage("无效的状态");
            return response;
        }
        try {
            response.setList(complaintService.getComplaintList(
                    complaintListRequest.getKw(),
                    complaintListRequest.getCreatedBy(),
                    complaintListRequest.getCanteenId(),
                    statusEnum,
                    complaintListRequest.getOrderBy(),
                    complaintListRequest.getAsc(),
                    complaintListRequest.getCurrentPage(),
                    complaintListRequest.getPageSize()
            ));
            response.setTotal(complaintService.countComplaintList(
                    complaintListRequest.getKw(),
                    complaintListRequest.getCreatedBy(),
                    complaintListRequest.getCanteenId(),
                    statusEnum
            ));
            response.setCurrentPage(complaintListRequest.getCurrentPage());
            response.setPageSize(complaintListRequest.getPageSize());
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/complaint/{complaintId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetComplaint(
            @PathParam("complaintId") @NotNull @Min(value = 1, message = "无效的Id") Integer complaintId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(complaintId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            response.setData(complaintService.getComplaintById(complaintId));
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PUT
    @Path("/complaint/{complaintId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restUpdateComplaint(
            @PathParam("complaintId") @NotNull @Min(value = 1, message = "无效的Id") Integer complaintId,
            Complaint complaint
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(complaintId, complaint);
        RequestValidatorUtils.doHibernateValidate(complaint);

        if (complaint.getStatus() != Complaint.Status.finished) {
            return new BasicDataResponse(400, "无效的状态");
        }

        BasicDataResponse response = new BasicDataResponse();
        try {
            response.setData(complaintService.closeComplaint(complaintId,
                    Integer.parseInt(securityContext.getUserPrincipal().getName())));
        } catch (Exception e) {
            response.setCode(404);
            response.setMessage(e.getMessage());
        }
        return response;
    }


}
