package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.VoteListRequest;
import cn.vvbbnn00.canteen.dto.response.*;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.Vote;
import cn.vvbbnn00.canteen.model.VoteOption;
import cn.vvbbnn00.canteen.model.Voter;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;
import cn.vvbbnn00.canteen.service.VoteService;

@Path("/vote")
@RequestScoped
public class VoteResource {
    @Context
    SecurityContext securityContext;
    VoteService voteService = new VoteService();

    @Context
    private HttpServletRequest request;

    @POST
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restPostVote(Vote vote) {
        RequestValidatorUtils.doHibernateValidate(vote);
        BasicDataResponse response = new BasicDataResponse();
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        try {
            vote.setCreatedBy(userId);
            response.setData(voteService.createVote(vote));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/{voteId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public VoteListResponse restGetVote(
            @PathParam("voteId") @NotNull Integer voteId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId);
        VoteListResponse response = new VoteListResponse();

        Vote vote = voteService.queryVoteById(voteId);
        if (vote == null) {
            response.setCode(404);
            response.setMessage("投票不存在");
            return response;
        }
        response.setData(vote);
        try {
            response.setList(voteService.getVoteOptionList(voteId));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @POST
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restGetVoteList(VoteListRequest voteListRequest) {
        RequestValidatorUtils.doHibernateValidate(voteListRequest);
        BasicListResponse response = new BasicListResponse();
        Integer status = null;
        if (voteListRequest.getIsStarted() != null) {
            status = voteListRequest.getIsStarted() ? 1 : 0;
        }
        response.setList(voteService.getVoteList(
                voteListRequest.getCurrentPage(),
                voteListRequest.getPageSize(),
                voteListRequest.getUserId(),
                status,
                voteListRequest.getOrderBy(),
                voteListRequest.getAsc()
        ));
        response.setTotal(voteService.countVoteList(
                voteListRequest.getUserId(),
                status
        ));
        response.setPageSize(voteListRequest.getPageSize());
        response.setCurrentPage(voteListRequest.getCurrentPage());
        return response;
    }

    @POST
    @Path("/{voteId}/option/")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicResponse restPostVoteOption(
            @PathParam("voteId") @NotNull Integer voteId,
            VoteOption newVoteOption
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId, newVoteOption);
        RequestValidatorUtils.doHibernateValidate(newVoteOption);

        String name = newVoteOption.getName();
        BasicResponse response = new BasicResponse();
        try {
            VoteOption voteOption = new VoteOption();
            voteOption.setVoteId(voteId);
            voteOption.setName(name);
            voteService.createVoteOption(voteOption);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PUT
    @Path("/{voteId}/option/{voteOptionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restPutVoteOption(
            @PathParam("voteId") @NotNull Integer voteId,
            @PathParam("voteOptionId") @NotNull Integer voteOptionId,
            VoteOption newVoteOption
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId, voteOptionId, newVoteOption);
        RequestValidatorUtils.doHibernateValidate(newVoteOption);
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        BasicDataResponse response = new BasicDataResponse();
        try {
            VoteOption voteOption = new VoteOption();
            voteOption.setVoteId(voteId);
            voteOption.setVoteOptionId(voteOptionId);
            voteOption.setName(newVoteOption.getName());
            response.setData(voteService.updateVoteOption(voteOption, userId));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
            LogUtils.severe(e.getMessage(), e);
        }
        return response;
    }

    @DELETE
    @Path("/{voteId}/option/{voteOptionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restDeleteVoteOption(
            @PathParam("voteId") @NotNull Integer voteId,
            @PathParam("voteOptionId") @NotNull Integer voteOptionId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId, voteOptionId);
        BasicDataResponse response = new BasicDataResponse();

        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());

        try {
            VoteOption voteOption = new VoteOption();
            voteOption.setVoteId(voteId);
            voteOption.setVoteOptionId(voteOptionId);
            response.setData(voteService.deleteVoteOption(voteOption, userId));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @PUT
    @Path("/{voteId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restPutVote(
            @PathParam("voteId") @NotNull Integer voteId,
            Vote vote
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId, vote);
        BasicDataResponse response = new BasicDataResponse();
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
        try {
            vote.setVoteId(voteId);
            response.setData(voteService.updateVote(vote, userId));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @DELETE
    @Path("/{voteId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restDeleteVote(
            @PathParam("voteId") @NotNull Integer voteId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId);
        BasicDataResponse response = new BasicDataResponse();
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
        try {
            Vote vote = new Vote();
            vote.setVoteId(voteId);
            response.setData(voteService.deleteVote(vote, userId));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/{voteId}/stat")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicListResponse restGetVoteStat(
            @PathParam("voteId") @NotNull Integer voteId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId);
        BasicListResponse response = new BasicListResponse();
        Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
        try {
            response.setList(voteService.getVoteOptionStat(voteId, userId));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
            LogUtils.severe(e.getMessage(), e);
        }
        return response;
    }

    @POST
    @Path("/{voteId}/result/{voteOptionId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicResponse restPostVoteResult(
            @PathParam("voteId") @NotNull Integer voteId,
            @PathParam("voteOptionId") @NotNull Integer voteOptionId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId, voteOptionId);
        BasicResponse response = new BasicResponse();
        User user = (User) request.getSession().getAttribute("user");
        Voter voter = new Voter();
        voter.setUserId(user.getUserId());
        voter.setVoteId(voteId);
        voter.setOptionId(voteOptionId);
        try {
            voteService.createVoteResult(voter);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/{voteId}/result")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetVoteResult(
            @PathParam("voteId") @NotNull Integer voteId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(voteId);
        BasicDataResponse response = new BasicDataResponse();
        User user = (User) request.getSession().getAttribute("user");
        try {
            response.setData(voteService.getVoteResultByVoteId(voteId, user.getUserId()));
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        response.setCode(200);
        return response;
    }
}
