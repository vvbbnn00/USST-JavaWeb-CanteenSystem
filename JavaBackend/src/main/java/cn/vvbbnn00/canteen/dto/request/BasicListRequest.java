package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;

@Data
@JavaBean
public class BasicListRequest {
    @Min(value = 1, message = "currentPage必须大于等于1")
    private Integer currentPage;
    @Min(value = 1, message = "pageSize必须大于等于1")
    @Max(value = 100, message = "pageSize必须小于等于100")
    private Integer pageSize;

    @Pattern(regexp = "^[a-zA-Z_][a-zA-Z0-9_]*$", message = "orderBy必须是字母数字下划线组成，且不能以数字开头")
    private String orderBy;
    private Boolean asc;
    private String kw;

    public BasicListRequest() {
        this.currentPage = 1;
        this.pageSize = 10;
    }
}
