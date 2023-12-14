package cn.vvbbnn00.canteen.controller.admin;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.UserIdRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.service.CanteenAdminService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 该Servlet用于通过路径参数获取食堂管理员列表
 */
@WebServlet(name = "CanteenAdminServlet", value = {"/admin/canteen_admin/canteen/*", "/admin/canteen_admin/canteen"})
public class CanteenAdminListServlet extends HttpServlet {
    @Override
    @CheckRole("admin")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer canteenId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (canteenId == null) {
            return;
        }

        // 获取食堂管理员列表
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(new CanteenAdminService().getCanteenAdminList(canteenId));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(409, e.getMessage());
        }
    }

    @Override
    @CheckRole("admin")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CanteenAdminService canteenAdminService = new CanteenAdminService();
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer canteenId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (canteenId == null) {
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        Integer userId;
        try {
            userId = GsonFactory.getGson().fromJson(requestBody, UserIdRequest.class).getUserId();
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }

        // 创建食堂管理员
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(canteenAdminService.addCanteenAdmin(canteenId, userId));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(409, e.getMessage());
        }
    }

    @Override
    @CheckRole("admin")
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CanteenAdminService canteenAdminService = new CanteenAdminService();
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer canteenId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (canteenId == null) {
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        Integer userId;
        try {
            userId = GsonFactory.getGson().fromJson(requestBody, UserIdRequest.class).getUserId();
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }

        // 删除食堂管理员
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(canteenAdminService.removeCanteenAdmin(canteenId, userId));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(409, e.getMessage());
        }
    }
}