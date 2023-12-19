package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.beans.JavaBean;

@Data
@JavaBean
public class UserIdRequest {

    @NotNull(message = "userId不能为空")
    @Min(value = 1, message = "userId必须大于0")
    private Integer userId;


}