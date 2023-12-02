package cn.vvbbnn00.canteensystemjavabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Date;

@Data
public class User {
    @Id
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
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginAt;

    public enum Role {
        user, canteen_admin, admin
    }
}
