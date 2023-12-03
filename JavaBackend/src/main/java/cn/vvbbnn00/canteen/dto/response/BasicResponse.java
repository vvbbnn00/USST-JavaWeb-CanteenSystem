package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 响应基类，所有响应都应该继承此类
 */
@Data
public class BasicResponse implements Serializable {
    Integer code; // 响应码，200表示成功，其他表示失败
    String message; // 响应信息，一般用于调试

    public BasicResponse() {
        this.code = 200;
        this.message = "success";
    }

    public BasicResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
