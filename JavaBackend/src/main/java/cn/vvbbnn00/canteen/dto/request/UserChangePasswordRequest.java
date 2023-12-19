package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.beans.JavaBean;

@Data
@JavaBean
public class UserChangePasswordRequest {
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    @NotBlank(message = "新密码不能为空")
    // 密码长度为6-20位，必须包含数字和字母，可以包含特殊字符
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[\\s\\S]{6,20}$", message = "密码不合法，长度为6-20位，必须包含数字和字母，可以包含特殊字符")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
