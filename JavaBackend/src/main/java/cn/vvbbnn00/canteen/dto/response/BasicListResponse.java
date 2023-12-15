package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.JavaBean;

/**
 * 基础列表响应类，用于返回列表数据
 */
@EqualsAndHashCode(callSuper = true)
@Data
@JavaBean
public class BasicListResponse extends BasicResponse {
    private Integer total;
    private Object list;
    private Integer currentPage;
    private Integer pageSize;
}
