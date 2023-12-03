package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 基础数据响应类，用于返回单个数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BasicDataResponse extends BasicResponse {
    private Object data;
}
