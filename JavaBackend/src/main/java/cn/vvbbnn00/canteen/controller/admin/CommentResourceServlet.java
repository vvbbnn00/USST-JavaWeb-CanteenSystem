package cn.vvbbnn00.canteen.controller.admin;

import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.service.CommentService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import cn.vvbbnn00.canteen.model.Comment;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "CommentResourceServlet", value = "/admin/comment/*")
public class CommentResourceServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CommentService commentService = new CommentService();
        String pathInfo = request.getPathInfo();

        // 解析路径参数
        Integer cuisineId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, response);
        if (cuisineId == null) {
            return;
        }

        // 查询评论
        Comment comment = commentService.getCommentById(cuisineId);
        if (comment == null) {
            response.sendError(404);
            return;
        }

        BasicDataResponse basicDataResponse = new BasicDataResponse();
        basicDataResponse.setData(comment);

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CommentService commentService = new CommentService();
        String pathInfo = request.getPathInfo();

        // 解析路径参数
        Integer cuisineId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, response);
        if (cuisineId == null) {
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(request);
        Comment comment;
        try {
            comment = GsonFactory.getGson().fromJson(requestBody, Comment.class);
        } catch (Exception e) {
            response.sendError(400, "请求体格式错误");
            return;
        }

        // 执行Hiberante校验
        try {
            RequestValidatorUtils.doHibernateValidate(comment);
        } catch (Exception e) {
            response.sendError(400, e.getMessage());
            return;
        }

        // 创建评论
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(commentService.createComment(comment));
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            response.sendError(409, e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CommentService commentService = new CommentService();
        String pathInfo = request.getPathInfo();

        // 解析路径参数
        Integer cuisineId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, response);
        if (cuisineId == null) {
            return;
        }

        // 删除评论
        try {
            commentService.deleteComment(cuisineId);
            GsonFactory.makeSuccessResponse(response, "删除评论成功");
        } catch (Exception e) {
            response.sendError(500, e.getMessage());
        }
    }
}