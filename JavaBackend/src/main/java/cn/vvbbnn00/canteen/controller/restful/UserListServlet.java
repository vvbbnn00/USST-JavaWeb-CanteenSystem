package cn.vvbbnn00.canteen.controller.restful;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.UserListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.service.UserService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import com.google.gson.Gson;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "UserListServlet", value = {"/restful/user/list/*"})
public class UserListServlet extends HttpServlet {
    @Override
    @CheckRole("admin")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = new UserService();
        String pathInfo = req.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            resp.sendError(404);
            return;
        }
        UserListRequest userListRequest;

        try {
            userListRequest = (UserListRequest) RequestValidatorUtils.validate(req, UserListRequest.class);
        } catch (IllegalArgumentException e) {
            resp.sendError(400, e.getMessage());
            return;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            resp.sendError(500);
            return;
        }

        List<User> userList = userService.getUserList(
                userListRequest.getCurrentPage(),
                userListRequest.getPageSize(),
                userListRequest.getKw(),
                userListRequest.getAvailable(),
                userListRequest.getRole(),
                userListRequest.getIsVerified(),
                userListRequest.getOrderBy(),
                userListRequest.getAsc()
        );
        int count = userService.getUserListCount(
                userListRequest.getKw(),
                userListRequest.getAvailable(),
                userListRequest.getRole(),
                userListRequest.getIsVerified()
        );

        Gson gson = GsonFactory.getGson(); // 获取Gson对象

        BasicListResponse basicListResponse = new BasicListResponse();
        basicListResponse.setTotal(count);
        basicListResponse.setList(userList);
        basicListResponse.setCurrentPage(userListRequest.getCurrentPage());
        basicListResponse.setPageSize(userListRequest.getPageSize());

        String json = gson.toJson(basicListResponse);
        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().println(json);
    }
}
