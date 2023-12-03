package cn.vvbbnn00.canteen.model;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserPointLog implements Serializable {
    private Integer logId;
    private Integer userId;
    private Integer point;
    private String detail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
}