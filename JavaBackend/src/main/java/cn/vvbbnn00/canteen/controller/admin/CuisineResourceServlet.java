package cn.vvbbnn00.canteen.controller.admin;

import cn.vvbbnn00.canteen.annotation.CheckRole;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

import java.io.IOException;

@WebServlet(name = "CuisineResourceServlet", value = "/admin/cuisine/*")
public class CuisineResourceServlet extends HttpServlet {

    @Override
    @CheckRole("admin")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}