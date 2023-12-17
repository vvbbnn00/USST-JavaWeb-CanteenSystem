package cn.vvbbnn00.canteen.controller.message;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserMessage;
import cn.vvbbnn00.canteen.service.MessageService;

import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "MessageServlet", value = "/message/*")
public class MessageServlet extends HttpServlet {

    @Override
    @CheckRole("user")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MessageService messageService = new MessageService();
        String pathInfo = request.getPathInfo();
        User user = (User) request.getSession().getAttribute("user");

        // post操作只能是创建消息
        if (pathInfo != null && !pathInfo.equals("/")) {
            response.sendError(404);
            return;
        }

        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(request);
        UserMessage message;
        try {
            message = GsonFactory.getGson().fromJson(requestBody, UserMessage.class);
        } catch (Exception e) {
            response.sendError(400, "请求体格式错误");
            return;
        }

        // 执行Hibernate校验
        try {
            RequestValidatorUtils.doHibernateValidate(message);
        } catch (Exception e) {
            response.sendError(400, e.getMessage());
            return;
        }

        // 创建消息
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(messageService.sendMessage(message));
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            response.sendError(409, e.getMessage());
        }
    }

    @Override
    @CheckRole("user")
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MessageService messageService = new MessageService();
        String pathInfo = request.getPathInfo();
        User user = (User) request.getSession().getAttribute("user");

        // get操作只能是获取消息列表
        if (pathInfo != null && !pathInfo.equals("/")) {
            response.sendError(404);
            return;
        }

        // 获取消息列表
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(messageService.getMessageList(user.getUserId()));
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            response.sendError(409, e.getMessage());
        }
    }

    @Override
    @CheckRole("user")
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        MessageService messageService = new MessageService();
        String pathInfo = request.getPathInfo();
        User user = (User) request.getSession().getAttribute("user");

        // delete操作只能是删除消息
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendError(404);
            return;
        }

        // 解析路径参数
        Integer messageId = RequestValidatorUtils.parseRestIdFromPathInfo(pathInfo, response);
        if (messageId == null) {
            return;
        }

        // 删除消息
        try {
            BasicDataResponse basicDataResponse = new BasicDataResponse();
            basicDataResponse.setData(messageService.deleteMessage(messageId));
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().println(GsonFactory.getGson().toJson(basicDataResponse));
        } catch (Exception e) {
            response.sendError(409, e.getMessage());
        }
    }
}