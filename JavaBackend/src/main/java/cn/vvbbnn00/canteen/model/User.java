package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
public class User implements Serializable {
    private Integer userId;

    @NotEmpty(message = "用户名不能为空")
    private String username;

    // 此处的密码是加密后的密码
    @NotEmpty(message = "密码不能为空")
    private String password;
    private String name;
    private String employeeId;
    private Integer level;
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

    public enum Role {
        user, canteen_admin, admin
    }
}
