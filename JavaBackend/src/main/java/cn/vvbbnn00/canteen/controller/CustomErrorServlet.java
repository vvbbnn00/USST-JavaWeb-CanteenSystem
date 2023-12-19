package cn.vvbbnn00.canteen.controller;

import cn.vvbbnn00.canteen.util.GsonFactory;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

/**
 * 自定义错误处理Servlet，当调用sendError方法，或者抛出异常时，会被该Servlet处理
 */
@WebServlet(name = "CustomErrorServlet", value = "/error")
public class CustomErrorServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getAttribute("jakarta.servlet.error.status_code") == null) {
            request.setAttribute("jakarta.servlet.error.status_code", 404);
        }
        handleCustomError(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (request.getAttribute("jakarta.servlet.error.status_code") == null) {
            request.setAttribute("jakarta.servlet.error.status_code", 404);
        }
        handleCustomError(request, response);
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("jakarta.servlet.error.status_code") == null) {
            request.setAttribute("jakarta.servlet.error.status_code", 404);
        }
        handleCustomError(request, response);
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("jakarta.servlet.error.status_code") == null) {
            request.setAttribute("jakarta.servlet.error.status_code", 404);
        }
        handleCustomError(request, response);
    }

    protected void doHead(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("jakarta.servlet.error.status_code") == null) {
            request.setAttribute("jakarta.servlet.error.status_code", 404);
        }
        handleCustomError(request, response);
    }

    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("jakarta.servlet.error.status_code") == null) {
            request.setAttribute("jakarta.servlet.error.status_code", 404);
        }
        handleCustomError(request, response);
    }

    protected void doTrace(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getAttribute("jakarta.servlet.error.status_code") == null) {
            request.setAttribute("jakarta.servlet.error.status_code", 404);
        }
        handleCustomError(request, response);
    }

    private void handleCustomError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
        String message = (String) request.getAttribute("jakarta.servlet.error.message");

        if (statusCode == null) {
            statusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        }

        if (message == null || message.isEmpty()) {
            switch (statusCode) {
                case HttpServletResponse.SC_NOT_FOUND:
                    message = "Not Found";
                    break;
                case HttpServletResponse.SC_UNAUTHORIZED:
                    message = "Unauthorized";
                    break;
                case HttpServletResponse.SC_FORBIDDEN:
                    message = "Forbidden";
                    break;
                case HttpServletResponse.SC_BAD_REQUEST:
                    message = "Bad Request";
                    break;
                case HttpServletResponse.SC_INTERNAL_SERVER_ERROR:
                    throw new RuntimeException("Internal Server Error");
                    // message = "Internal Server Error";
                    // break;
                default:
                    message = "Unknown Error";
            }
        }

        GsonFactory.makeErrorResponse(response, statusCode, message);
    }
}
