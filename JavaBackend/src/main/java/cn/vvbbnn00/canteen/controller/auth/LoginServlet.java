package cn.vvbbnn00.canteen.controller.auth;

import cn.vvbbnn00.canteen.dto.request.UserLoginRequest;
import cn.vvbbnn00.canteen.dto.response.BasicDataResponse;
import cn.vvbbnn00.canteen.dto.response.UserLoginResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.service.LoginService;
import cn.vvbbnn00.canteen.util.GsonFactory;
import cn.vvbbnn00.canteen.util.RequestValidatorUtils;
import cn.vvbbnn00.canteen.util.SafetyUtils;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "LoginServlet", value = {"/auth/login"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        UserLoginRequest userLoginRequest;
        // 参数校验
        String requestBody = RequestValidatorUtils.getFullBody(req);
        try {
            userLoginRequest = GsonFactory.getGson().fromJson(requestBody, UserLoginRequest.class);
            // LogUtils.info(requestBody);
        } catch (Exception e) {
            resp.sendError(400, "请求体格式错误");
            return;
        }
        try {
            RequestValidatorUtils.doHibernateValidate(userLoginRequest);
        } catch (Exception e) {
            resp.sendError(400, e.getMessage());
            return;
        }

        String timestampStr = userLoginRequest.getTimestamp();
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

        String raw = userLoginRequest.getUsername() + "\0" + userLoginRequest.getPassword();
        String checkSign = SafetyUtils.doLoginSign(raw, timestampStr);

        // LogUtils.info("checkSign: " + checkSign + " sign: " + userLoginRequest.getSign());
        if (!checkSign.equals(userLoginRequest.getSign())) {
            resp.sendError(400, "签名错误");
            return;
        }

        try {
            LoginService loginService = new LoginService();
            User user = loginService.login(userLoginRequest.getUsername(), userLoginRequest.getPassword());

            if (!user.getAvailable()) {
                resp.sendError(403, "账号已被禁用");
                return;
            }

            req.getSession().setAttribute("user", user);

            UserLoginResponse userLoginResponse = new UserLoginResponse();
            userLoginResponse.setAvatar(user.getAvatar());
            userLoginResponse.setRole(user.getRole());
            userLoginResponse.setUserId(user.getUserId());
            userLoginResponse.setUsername(user.getUsername());
            userLoginResponse.setEmail(user.getEmail());

            BasicDataResponse response = new BasicDataResponse();
            response.setMessage("登录成功");
            response.setData(userLoginResponse);

            resp.setContentType("application/json;charset=utf-8");
            resp.getWriter().println(GsonFactory.getGson().toJson(response));
        } catch (Exception e) {
            resp.sendError(401, e.getMessage());
        }
    }
}
