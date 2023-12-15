package cn.vvbbnn00.canteen.util;

import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.model.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;

import java.io.IOException;
import java.time.LocalDateTime;

public class GsonFactory {
    /**
     * 获取Gson对象，该对象已经注册了必要的适配器
     *
     * @return Gson对象
     */
    public static Gson getGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }


    /**
     * 将对象转换为 JSON 格式。
     *
     * @param obj 需要被转换为 JSON 的对象。
     * @return 对象的 JSON 字符串表示形式。
     */
    public static String toJson(Object obj) {
        return getGson().toJson(obj);
    }

    /**
     * 生成错误响应
     *
     * @param response HttpServletResponse对象
     * @param code     响应码
     * @param message  响应信息
     */
    public static void makeErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(GsonFactory.getGson().toJson(new BasicResponse(code, message)));
    }

    /**
     * 生成成功响应
     *
     * @param response HttpServletResponse对象
     * @param message  响应信息
     */
    public static void makeSuccessResponse(HttpServletResponse response, String message) throws IOException {
        makeErrorResponse(response, 200, message);
    }

    /**
     * 生成错误响应
     *
     * @param code    响应码
     * @param message 响应信息
     * @return 响应字符串
     */
    public static Response generateErrorResponse(int code, String message) {
        Response.ResponseBuilder builder = Response.status(code);
        builder.entity(GsonFactory.getGson().toJson(new BasicResponse(code, message)));
        return builder.build();
    }

    /**
     * 生成成功响应
     *
     * @param message 响应信息
     * @return 响应字符串
     */
    public static Response generateSuccessResponse(String message) {
        return generateErrorResponse(200, message);
    }
}
