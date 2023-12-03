package cn.vvbbnn00.canteen.util;

import cn.vvbbnn00.canteen.dto.response.BasicResponse;
import cn.vvbbnn00.canteen.model.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.http.HttpServletResponse;

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
     * 生成错误响应
     * @param response HttpServletResponse对象
     * @param code 响应码
     * @param message 响应信息
     */
    public static void makeErrorResponse(HttpServletResponse response, int code, String message) throws IOException {
        response.setStatus(code);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(GsonFactory.getGson().toJson(new BasicResponse(code, message)));
    }
}
