package cn.vvbbnn00.canteen.controller;

import cn.vvbbnn00.canteen.dao.UserDao;
import cn.vvbbnn00.canteen.dao.impl.UserDaoImpl;
import cn.vvbbnn00.canteen.dto.response.BasicListResponse;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.GsonFactory;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "TestServlet", value = "/test")
public class TestServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDao userDao = new UserDaoImpl();
        List<User> userList = userDao.queryUsers(1, 10, null, null, User.Role.admin, null, null, null);
        int count = userDao.queryUsersCount(null, null, User.Role.admin, null);

        Gson gson = GsonFactory.getGson(); // 获取Gson对象

        BasicListResponse basicListResponse = new BasicListResponse();
        basicListResponse.setTotal(count);
        basicListResponse.setList(userList);

        String json = gson.toJson(basicListResponse);
        response.getWriter().println(json);
    }
}
