package cn.vvbbnn00.canteen.controller.admin;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.CanteenListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.Canteen;
import cn.vvbbnn00.canteen.service.CanteenService;
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
@WebServlet(name = "CanteenListServlet", value = {"/admin/canteen/list/*"})
public class CanteenListServlet extends HttpServlet {

    @Override
    @CheckRole("user")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        CanteenService canteenService = new CanteenService();
        String pathInfo = req.getPathInfo();

        // 确保路径信息正确
        if (pathInfo != null && !pathInfo.equals("/")) {
            resp.sendError(404);
            return;
        }

        CanteenListRequest canteenListRequest;
        try {
            // 使用RequestValidatorUtils.validate()验证请求参数并转换为CanteenListRequest对象
            canteenListRequest = (CanteenListRequest) RequestValidatorUtils.validate(req, CanteenListRequest.class);
        } catch (IllegalArgumentException e) {
            resp.sendError(400, e.getMessage());
            return;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            resp.sendError(500);
            return;
        }

        try{
            // 调用CanteenService获取食堂列表
            List<Canteen> canteenList = canteenService.getCanteenList(
                    canteenListRequest.getCurrentPage(),
                    canteenListRequest.getPageSize(),
                    canteenListRequest.getKw(),
                    canteenListRequest.getOrderBy(),
                    canteenListRequest.getAsc()
            );

            // 调用CanteenService获取食堂总数
            int count = canteenService.getCanteenListCount(canteenListRequest.getKw());

            Gson gson = GsonFactory.getGson(); // 获取Gson对象

            // 构建包含食堂列表信息的JSON响应
            BasicListResponse basicListResponse = new BasicListResponse();
            basicListResponse.setTotal(count);
            basicListResponse.setList(canteenList);
            basicListResponse.setCurrentPage(canteenListRequest.getCurrentPage());
            basicListResponse.setPageSize(canteenListRequest.getPageSize());

            // 将JSON响应写回客户端
            String json = gson.toJson(basicListResponse);
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(json);
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            resp.sendError(500, e.getMessage());
        }

    }
}

