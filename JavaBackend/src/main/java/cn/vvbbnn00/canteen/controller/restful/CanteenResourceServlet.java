package cn.vvbbnn00.canteen.controller.restful;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.service.CanteenService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "CanteenResourceServlet", value = {"/restful/canteen/*"})
public class CanteenResourceServlet extends HttpServlet {

    @Override
    @CheckRole("admin")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CanteenService canteenService = new CanteenService();
        String pathInfo = req.getPathInfo();

        // POST操作只能是创建食堂
        if (pathInfo != null && !pathInfo.equals("/")) {
            resp.sendError(404);
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        Canteen canteen;
        try {
            canteen = GsonFactory.getGson().fromJson(requestBody, Canteen.class);
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }
        try {
            RequestValidatorUtils.doHibernateValidate(canteen);
        } catch (Exception e) {
            resp.sendError(400, e.getMessage());
            return;
        }

        // 创建食堂
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(canteenService.createCanteen(canteen));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(409, e.getMessage());
        }
    }

    @Override
    @CheckRole("user")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CanteenService canteenService = new CanteenService();
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer canteenId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (canteenId == null) {
            return;
        }

        // 获取食堂
        Canteen canteen = canteenService.getCanteenById(canteenId);
        if (canteen == null) {
            resp.sendError(404, "食堂不存在");
            return;
        }

        BasicDataResponse basicDataResponse = new BasicDataResponse();
        basicDataResponse.setData(canteen);

        resp.setContentType("application/json;charset=utf-8");
        resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
    }

    @Override
    @CheckRole("admin")
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CanteenService canteenService = new CanteenService();
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer canteenId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (canteenId == null) {
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        Canteen canteen;
        try {
            canteen = GsonFactory.getGson().fromJson(requestBody, Canteen.class);
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }
        try {
            RequestValidatorUtils.doHibernateValidate(canteen);
        } catch (Exception e) {
            resp.sendError(400, e.getMessage());
            return;
        }

        // 更新食堂
        try {
            canteen.setCanteenId(canteenId);
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(canteenService.updateCanteen(canteen));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

    @Override
    @CheckRole("admin")
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CanteenService canteenService = new CanteenService();
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer canteenId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (canteenId == null) {
            return;
        }

        // 删除食堂
        try {
            canteenService.deleteCanteen(canteenId);
            GsonFactory.makeSuccessResponse(resp, "删除食堂成功");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
        }
    }

}
