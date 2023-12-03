package cn.vvbbnn00.canteen.controller.admin;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.service.UserService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Objects;

@WebServlet(name = "UserResourceServlet", value = {"/admin/user/*"})
public class UserResourceServlet extends HttpServlet {

    @Override
    @CheckRole("admin")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = new UserService();
        String pathInfo = req.getPathInfo();

        // post操作只能是创建用户
        if (pathInfo != null && !pathInfo.equals("/")) {
            resp.sendError(404);
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        User user;
        try {
            user = GsonFactory.getGson().fromJson(requestBody, User.class);
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }
        try {
            RequestValidatorUtils.doHibernateValidate(user);
        } catch (Exception e) {
            resp.sendError(400, e.getMessage());
            return;
        }

        // 创建用户
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(userService.createUser(user));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(409, e.getMessage());
        }
    }

    @Override
    @CheckRole("admin")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = new UserService();
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer userId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (userId == null) {
            return;
        }

        // 获取用户
        User user = userService.getUserById(userId);
        if (user == null) {
            resp.sendError(404, "用户不存在");
            return;
        }

        BasicDataResponse basicDataResponse = new BasicDataResponse();
        basicDataResponse.setData(user);

        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
    }

    @Override
    @CheckRole("admin")
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = new UserService();
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer userId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (userId == null) {
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        User user;
        try {
            user = GsonFactory.getGson().fromJson(requestBody, User.class);
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }
        try {
            RequestValidatorUtils.doHibernateValidate(user);
        } catch (Exception e) {
            resp.sendError(400, e.getMessage());
            return;
        }

        // 更新用户
        try {
            user.setUserId(userId);
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(userService.updateUser(user));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    @CheckRole("admin")
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserService userService = new UserService();
        String pathInfo = req.getPathInfo();
        User user = (User) req.getSession().getAttribute("user");

        // 解析路径参数
        Integer userId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (userId == null) {
            return;
        }

        if (Objects.equals(user.getUserId(), userId)) {
            resp.sendError(403, "不能删除自己");
            return;
        }

        // 删除用户
        try {
            userService.deleteUser(userId);
            GsonFactory.makeSuccessResponse(resp, "删除用户成功");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

}
