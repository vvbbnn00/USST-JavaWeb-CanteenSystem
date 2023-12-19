package cn.vvbbnn00.canteen.controller.auth;

import cn.vvbbnn00.canteen.dto.request.UserRegisterRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.UserLoginResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.service.UserPointLogService;
import cn.vvbbnn00.canteen.service.UserService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import cn.vvbbnn00.canteen.util.SafetyUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "RegisterServlet", value = {"/auth/register"})
public class RegisterServlet extends HttpServlet {
    private static final UserPointLogService userPointLogService = new UserPointLogService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        UserRegisterRequest userRegisterRequest;
        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        try {
            userRegisterRequest = GsonFactory.getGson().fromJson(requestBody, UserRegisterRequest.class);
            // LogUtils.info(requestBody);
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }
        try {
            RequestValidatorUtils.doHibernateValidate(userRegisterRequest);
        } catch (Exception e) {
            resp.sendError(400, e.getMessage());
            return;
        }

        String timestampStr = userRegisterRequest.getTimestamp();
        long timestamp;
        try {
            timestamp = Long.parseLong(timestampStr);
        } catch (Exception e) {
            resp.sendError(400, "时间戳格式错误");
            return;
        }
        if (Math.abs(System.currentTimeMillis() - timestamp) > 1000 * 60 * 2) {
            resp.sendError(400, "请求已过期，请检查本地时间是否正确");
            return;
        }

        String raw = userRegisterRequest.getUsername() + "\0" + userRegisterRequest.getPassword() + "\0" + userRegisterRequest.getEmail();
        String checkSign = SafetyUtils.doLoginSign(raw, timestampStr);
        if (!checkSign.equals(userRegisterRequest.getSign())) {
            resp.sendError(400, "签名错误");
            return;
        }

        if (!userRegisterRequest.getPassword().equals(userRegisterRequest.getConfirmPassword())) {
            resp.sendError(400, "两次密码不一致");
            return;
        }

        try {
            UserService userService = new UserService();
            User user = new User();
            user.setUsername(userRegisterRequest.getUsername());
            user.setPassword(userRegisterRequest.getPassword());
            user.setEmail(userRegisterRequest.getEmail());
            User userResult = userService.createUser(user);
            if (userResult == null) {
                resp.sendError(400, "注册失败");
                return;
            }

            req.getSession().setAttribute("user", userResult);

            UserLoginResponse userLoginResponse = new UserLoginResponse();
            userLoginResponse.setAvatar(userResult.getAvatar());
            userLoginResponse.setRole(userResult.getRole());
            userLoginResponse.setUserId(userResult.getUserId());
            userLoginResponse.setUsername(userResult.getUsername());
            userLoginResponse.setEmail(userResult.getEmail());

            BasicDataResponse response = new BasicDataResponse();
            response.setMessage("注册成功");
            response.setData(userLoginResponse);

            resp.setStatus(200);
            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(response));

            userPointLogService.changeUserPoint(userResult.getUserId(), 1, "注册奖励");
        } catch (Exception e) {
            resp.sendError(500, e.getMessage());
            return;
        }
    }
}
