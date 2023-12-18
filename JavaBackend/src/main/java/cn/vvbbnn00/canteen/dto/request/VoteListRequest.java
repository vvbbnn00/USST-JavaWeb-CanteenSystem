package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.JavaBean;

@EqualsAndHashCode(callSuper = true)
@Data
@JavaBean
public class VoteListRequest extends BasicListRequest {
    @Pattern(regexp = "^(startTime|endTime|status)$",
            message = "orderBy必须是startTime,endTime,status中的一个")
    private String orderBy;
    private Integer status;
}
