package cn.vvbbnn00.canteen.controller;

import cn.vvbbnn00.canteen.service.SystemService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.LogUtils;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * 该Servlet在整个实例运行中仅允许访问一次，用于检查是否存在管理员账户，如果不存在，则创建一个管理员账户
 */
@WebServlet(name = "InitServlet", value = {"/system/init"})
public class InitServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletContext servletContext = getServletContext();
        if (servletContext.getAttribute("isInit") == null) {
            servletContext.setAttribute("isInit", false);
        }
        if ((boolean) getServletContext().getAttribute("isInit")) {
            resp.sendError(403, "System has been initialized");
            return;
        }
        try {
            SystemService systemService = new SystemService();
            systemService.init();
        } catch (Exception e) {
            getServletContext().setAttribute("isInit", false);
            LogUtils.error(e.getMessage(), e);
            resp.sendError(500, e.getMessage());
            return;
        }
        getServletContext().setAttribute("isInit", true);
        GsonFactory.makeSuccessResponse(resp, "System init success.");
    }
}
