package cn.vvbbnn00.canteen.controller.admin;

import cn.vvbbnn00.canteen.annotation.CheckRole;
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
 * 该Servlet用于通过路径参数获取特定用户管理的食堂列表
 */
@WebServlet(name = "CanteenAdminUserListServlet", value = {"/admin/canteen_admin/user/*", "/admin/canteen_admin/user"})
public class CanteenAdminUserListServlet extends HttpServlet {
    @Override
    @CheckRole("admin")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();

        // 解析路径参数
        Integer userId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, resp);
        if (userId == null) {
            return;
        }

        // 获取食堂管理员列表
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(new CanteenAdminService().getUserManagedCanteens(userId));
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            resp.sendError(409, e.getMessage());
        }
    }
}