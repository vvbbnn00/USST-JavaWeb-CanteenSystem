package cn.vvbbnn00.canteen.dto.request;

import cn.vvbbnn00.canteen.model.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.beans.JavaBean;


@EqualsAndHashCode(callSuper = true)
@Data
@JavaBean
public class UserListRequest extends BasicListRequest {
    private Boolean available;
    private User.Role role;
    private Boolean isVerified;

    @Pattern(regexp = "^(userId|username|name|employeeId|level|point|isVerified|available|role|createdAt|updatedAt|lastLoginAt)$",
            message = "orderBy必须是userId,username,name,employeeId,level,point,isVerified,available,role,createdAt,updatedAt,lastLoginAt中的一个")
    private String orderBy;
}
