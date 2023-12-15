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
}
