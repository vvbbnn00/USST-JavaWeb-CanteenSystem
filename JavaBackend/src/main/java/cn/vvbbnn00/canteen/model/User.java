package cn.vvbbnn00.canteen.model;

import cn.vvbbnn00.canteen.util.StringUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@JavaBean
public class User implements Serializable {
    private Integer userId;

    @Pattern(regexp = "^[a-zA-Z0-9_]{4,16}$", message = "用户名只能包含字母、数字和下划线，长度为4-16位")
    private String username;

    // 此处的密码是加密后的密码
    @Pattern(regexp = "^\\S{6,16}$", message = "密码不能包含空格，长度为6-16位")
    private String password;
    private String name;
    private String employeeId;
    @Min(value = 0, message = "level不能小于0")
    @Max(value = 20, message = "level不能大于20")
    private Integer level;
    @Min(value = 0, message = "point不能小于0")
    private Long point;
    private Boolean available;

    @Enumerated(EnumType.STRING)
    private Role role;
    private Boolean isVerified;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastLoginAt;

    @Email(message = "邮箱格式不正确")
    private String email;

    public String getAvatar() {
        return StringUtils.getAvatarUrl(this.email);
    }

    public enum Role {
        user, canteen_admin, admin
    }
}
