package cn.vvbbnn00.canteen.controller.restful;

import cn.vvbbnn00.canteen.dto.request.CommentListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.Comment;
import cn.vvbbnn00.canteen.service.CommentService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CommentListServlet", value = {"/restful/comment/list/*"})
public class CommentListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        CommentService commentService = new CommentService();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            response.sendError(404);
            return;
        }

        CommentListRequest commentListRequest;

        try {
            commentListRequest = (CommentListRequest) RequestValidatorUtils.validate(request, CommentListRequest.class);
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
            return;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            response.sendError(500);
            return;
        }

        List<Comment> commentList = commentService.getCommentList(
                commentListRequest.getCurrentPage(),
                commentListRequest.getPageSize(),
                commentListRequest.getKw(),
                commentListRequest.getType(),
                commentListRequest.getOrderBy(),
                commentListRequest.getAsc()
        );

        int count = commentService.getCommentListCount(
                commentListRequest.getKw(),
                commentListRequest.getType()
        );

        Gson gson = GsonFactory.getGson();

        BasicListResponse basicListResponse = new BasicListResponse();
        basicListResponse.setTotal(count);
        basicListResponse.setList(commentList);
        basicListResponse.setCurrentPage(commentListRequest.getCurrentPage());
        basicListResponse.setPageSize(commentListRequest.getPageSize());

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(gson.toJson(basicListResponse));
    }
}