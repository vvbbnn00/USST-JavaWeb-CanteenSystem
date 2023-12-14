package cn.vvbbnn00.canteen.controller.restful;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.request.CuisineListRequest;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.Cuisine;
import cn.vvbbnn00.canteen.service.CuisineService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.LogUtils;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import com.google.gson.Gson;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "CuisineListServlet", value = {"/restful/cuisine/list/*"})
public class CuisineListServlet extends HttpServlet {
    @Override
    @CheckRole("user")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        CuisineService cuisineService = new CuisineService();
        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            response.sendError(404);
            return;
        }

        CuisineListRequest cuisineListRequest;

        try {
            cuisineListRequest = (CuisineListRequest) RequestValidatorUtils.validate(request, CuisineListRequest.class);
        } catch (IllegalArgumentException e) {
            response.sendError(400, e.getMessage());
            return;
        } catch (Exception e) {
            LogUtils.error(e.getMessage(), e);
            response.sendError(500);
            return;
        }

        List<Cuisine> cuisineList = cuisineService.getCuisineList(
                cuisineListRequest.getCurrentPage(),
                cuisineListRequest.getPageSize(),
                cuisineListRequest.getKw(),
                cuisineListRequest.getOrderBy(),
                cuisineListRequest.getAsc(),
                cuisineListRequest.getCanteenId()
        );

        int count = cuisineService.getCuisineListCount(
                cuisineListRequest.getKw(),
                cuisineListRequest.getCanteenId()
        );

        Gson gson = GsonFactory.getGson();

        BasicListResponse basicListResponse = new BasicListResponse();
        basicListResponse.setTotal(count);
        basicListResponse.setList(cuisineList);
        basicListResponse.setCurrentPage(cuisineListRequest.getCurrentPage());
        basicListResponse.setPageSize(cuisineListRequest.getPageSize());

        String json = gson.toJson(basicListResponse);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);

    }
}