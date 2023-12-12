package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class CuisineListRequest extends BasicListRequest{
    @Pattern(regexp = "^(userId|username|name|employeeId|level|point|isVerified|available|role|createdAt|updatedAt|lastLoginAt)$",
            message = "orderBy必须是userId,username,name,employeeId,level,point,isVerified,available,role,createdAt,updatedAt,lastLoginAt中的一个")
    private String orderBy;
}
