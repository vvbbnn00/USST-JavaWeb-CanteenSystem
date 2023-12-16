package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.JavaBean;

/**
 * 基础数据响应类，用于返回单个数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JavaBean
public class BasicDataResponse extends BasicResponse {
    private Object data;

    public BasicDataResponse(int code, String message, Object data) {
        super(code, message);
        this.data = data;
    }

    public BasicDataResponse() {
    }

    public BasicDataResponse(Object data) {
        this.data = data;
    }

    public BasicDataResponse(int code, String message) {
        super(code, message);
    }
}
