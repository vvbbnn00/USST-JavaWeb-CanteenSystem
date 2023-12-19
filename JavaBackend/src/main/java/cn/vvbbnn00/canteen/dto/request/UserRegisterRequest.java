package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;

@Data
@JavaBean
public class UserRegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9_]{2,20}$", message = "用户名不合法，只能包含字母、数字、下划线，长度为2-20位")
    private String username;

    @NotBlank(message = "密码不能为空")
    // 密码长度为6-20位，必须包含数字和字母，可以包含特殊字符
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)[\\s\\S]{6,20}$", message = "密码不合法，长度为6-20位，必须包含数字和字母，可以包含特殊字符")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @NotBlank(message = "电子邮箱不能为空")
    @Email(message = "电子邮箱不合法")
    private String email;

    @NotEmpty(message = "签名不能为空")
    @Size(min = 64, max = 64, message = "签名不合法")
    private String sign;

    @NotEmpty(message = "时间戳不能为空")
    @Size(min = 13, max = 13, message = "时间戳不合法")
    private String timestamp;
}
