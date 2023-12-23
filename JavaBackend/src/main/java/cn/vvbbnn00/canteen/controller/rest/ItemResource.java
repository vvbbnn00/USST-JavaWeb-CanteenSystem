package cn.vvbbnn00.canteen.controller.rest;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.ItemListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.Comment;
import cn.vvbbnn00.canteen.model.Item;
import cn.vvbbnn00.canteen.service.CommentService;
import cn.vvbbnn00.canteen.service.ItemService;
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
public class ItemResource {
    @Context
    SecurityContext securityContext;

    ItemService itemService = new ItemService();
    CommentService commentService = new CommentService();

    @POST
    @Path("/{canteenId}/cuisine/{cuisineId}/item")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restAddItem(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的食堂Id") Integer canteenId,
            @PathParam("cuisineId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer cuisineId,
            Item item
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, cuisineId, item);
        RequestValidatorUtils.doHibernateValidate(item);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
            item.setCuisineId(cuisineId);
            Item result = itemService.addItem(
                    item,
                    userId,
                    canteenId
            );
            response.setData(result);
            response.setMessage("创建菜品成功");
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    public BasicDataResponse restGetItem(
            @PathParam("itemId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer itemId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(itemId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Item result = itemService.getItemById(itemId);
            if (result == null) {
                response.setCode(404);
                response.setMessage("菜品不存在");
            } else {
                response.setData(result);
            }
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @GET
    @Path("/{canteenId}/cuisine/{cuisineId}/item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restGetItemAlia1(
            @PathParam("canteenId") @NotNull @Min(value = 1, message = "无效的食堂Id") Integer canteenId,
            @PathParam("cuisineId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer cuisineId,
            @PathParam("itemId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer itemId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(canteenId, cuisineId, itemId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Item result = itemService.getItemById(itemId);
            response.setData(result);
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @POST
    @Path("/item/list")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restGetItemList(
            ItemListRequest request
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateValidate(request);

        BasicListResponse response = new BasicListResponse();
        try {
            response.setList(itemService.getItemList(
                    request.getKw(),
                    request.getCuisineId(),
                    request.getCanteenId(),
                    request.getRecommended(),
                    request.getCurrentPage(),
                    request.getPageSize(),
                    request.getOrderBy(),
                    request.getAsc()
            ));
            response.setTotal(itemService.getItemListCount(
                    request.getKw(),
                    request.getCuisineId(),
                    request.getCanteenId(),
                    request.getRecommended()
            ));
            response.setPageSize(request.getPageSize());
            response.setCurrentPage(request.getCurrentPage());
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @PUT
    @Path("/item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restUpdateItem(
            @PathParam("itemId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer itemId,
            Item item
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(itemId, item);
        RequestValidatorUtils.doHibernateValidate(item);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
            item.setItemId(itemId);
            boolean result = itemService.updateItem(
                    item,
                    userId
            );
            if (result) {
                response.setMessage("更新菜品成功");
            } else {
                response.setCode(400);
                response.setMessage("更新菜品失败");
            }
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    @DELETE
    @Path("/item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("canteen_admin")
    public BasicDataResponse restDeleteItem(
            @PathParam("itemId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer itemId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(itemId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
            itemService.deleteItem(
                    itemId,
                    userId
            );
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @POST
    @Path("/item/{itemId}/comment")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restPostItemComment(
            @PathParam("itemId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer itemId,
            Comment comment
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(itemId, comment);
        RequestValidatorUtils.doHibernateValidate(comment);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
            itemService.commentItem(
                    comment.getContent(),
                    itemId,
                    userId,
                    comment.getParentId(),
                    comment.getScore()
            );
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @GET
    @Path("/item/{itemId}/comment")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicListResponse restGetItemComment(
            @PathParam("itemId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer itemId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(itemId);

        BasicListResponse response = new BasicListResponse();
        try {
            response.setList(commentService.getCommentList(
                    null,
                    Comment.CommentType.item,
                    itemId,
                    null,
                    1,
                    200,
                    "createdAt",
                    false
            ));
            response.setTotal(commentService.getCommentListCount(
                    null,
                    Comment.CommentType.item,
                    itemId,
                    null
            ));
            response.setPageSize(200);
            response.setCurrentPage(1);
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }


    @DELETE
    @Path("/item/{itemId}/comment/{commentId}")
    @Produces(MediaType.APPLICATION_JSON)
    @CheckRole("user")
    public BasicDataResponse restDeleteItemComment(
            @PathParam("itemId") @NotNull @Min(value = 1, message = "无效的菜品Id") Integer itemId,
            @PathParam("commentId") @NotNull @Min(value = 1, message = "无效的评论Id") Integer commentId
    ) {
        // 校验请求参数，请仔细阅读该方法的文档
        RequestValidatorUtils.doHibernateParamsValidate(itemId, commentId);

        BasicDataResponse response = new BasicDataResponse();
        try {
            Integer userId = Integer.parseInt(securityContext.getUserPrincipal().getName());
            commentService.deleteComment(
                    commentId,
                    userId
            );
        } catch (Exception e) {
            response.setCode(400);
            response.setMessage(e.getMessage());
        }
        return response;
    }
}
