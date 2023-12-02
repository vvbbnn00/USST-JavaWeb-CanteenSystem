package cn.vvbbnn00.canteen.util;

import cn.vvbbnn00.canteen.model.adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
}
