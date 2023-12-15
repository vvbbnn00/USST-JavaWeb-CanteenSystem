package cn.vvbbnn00.canteen.dto.request;


import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.JavaBean;

@EqualsAndHashCode(callSuper = true)
@Data
@JavaBean
public class CanteenListRequest extends BasicListRequest{
    @Pattern(regexp = "^(canteenId|name|location|introduction|compScore|createdAt|updatedAt)$",
            message = "orderBy必须是canteenId,name,location,introduction,compScore,createdAt,updatedAt中的一个")
    private String orderBy;

}
