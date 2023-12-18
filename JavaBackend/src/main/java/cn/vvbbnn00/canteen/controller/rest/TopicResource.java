package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.TopicListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.model.Topic;
import cn.vvbbnn00.canteen.model.TopicLike;
import cn.vvbbnn00.canteen.service.TopicLikeService;
import cn.vvbbnn00.canteen.service.TopicService;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.SecurityContext;

import java.util.List;

@Path("/topic")
@RequestScoped
public class TopicResource {

    @Context
    SecurityContext securityContext;

    TopicService topicService = new TopicService();
    TopicLikeService topicLikeService = new TopicLikeService();

    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restGetTopicList(
            TopicListRequest topicListRequest
    ) {
        RequestValidatorUtils.doHibernateValidate(topicListRequest);

        BasicListResponse response = new BasicListResponse();
        try {
            Integer count = topicService.getTopicListCount(topicListRequest.getUserId());
            response.setTotal(count);
            response.setPageSize(topicListRequest.getPageSize());
            response.setCurrentPage(topicListRequest.getCurrentPage());

            List<Topic> topicList = topicService.getTopicList(
                    topicListRequest.getUserId(),
                    topicListRequest.getCurrentPage(),
                    topicListRequest.getPageSize(),
                    topicListRequest.getOrderBy(),
                    topicListRequest.getAsc(),
                    Integer.parseInt(securityContext.getUserPrincipal().getName())
            );

            response.setList(topicList);
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @GET
    @Path("/hot")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restGetHotTopicList(
    ) {
        BasicListResponse response = new BasicListResponse();
        try {
            List<Topic> topicList = topicService.getTopicList(
                    null,
                    1,
                    5,
                    "compValue",
                    false,
                    Integer.parseInt(securityContext.getUserPrincipal().getName())
            );
            response.setList(topicList);
        } catch (Exception e) {
            response.setCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restAddTopic(
            Topic topic
    ) {
        RequestValidatorUtils.doHibernateValidate(topic);

        BasicDataResponse response = new BasicDataResponse();
        try {
            topic.setCreatedBy(Integer.parseInt(securityContext.getUserPrincipal().getName()));
            Topic newTopic = topicService.addTopic(topic);
            response.setData(List.of(newTopic));
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @GET
    @Path("/{topicId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetTopic(
            @PathParam("topicId") @NotNull @Min(value = 1, message = "无效的话题Id") Integer topicId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(topicId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Topic topic = topicService.getTopicById(
                    topicId,
                    Integer.parseInt(securityContext.getUserPrincipal().getName())
            );
            response.setData(List.of(topic));
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @DELETE
    @Path("/{topicId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicResponse restDeleteTopic(
            @PathParam("topicId") @NotNull @Min(value = 1, message = "无效的话题Id") Integer topicId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(topicId);

        BasicResponse response = new BasicResponse();
        try {
            topicService.deleteTopic(
                    topicId,
                    Integer.parseInt(securityContext.getUserPrincipal().getName())
            );
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @POST
    @Path("/{topicId}/like")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicResponse restLikeTopic(
            @PathParam("topicId") @NotNull @Min(value = 1, message = "无效的话题Id") Integer topicId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(topicId);

        BasicResponse response = new BasicResponse();
        try {
            topicLikeService.addTopicLike(
                    Integer.parseInt(securityContext.getUserPrincipal().getName()),
                    topicId
            );
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @DELETE
    @Path("/{topicId}/like")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicResponse restDislikeTopic(
            @PathParam("topicId") @NotNull @Min(value = 1, message = "无效的话题Id") Integer topicId
    ) {
        RequestValidatorUtils.doHibernateParamsValidate(topicId);

        BasicResponse response = new BasicResponse();
        try {
            topicLikeService.deleteTopicLike(
                    Integer.parseInt(securityContext.getUserPrincipal().getName()),
                    topicId
            );
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
