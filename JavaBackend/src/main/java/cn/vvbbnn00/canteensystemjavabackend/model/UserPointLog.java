package cn.vvbbnn00.canteensystemjavabackend.model;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserPointLog {
    @Id
    private Integer logId;
    private Integer userId;
    private Integer point;
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
}