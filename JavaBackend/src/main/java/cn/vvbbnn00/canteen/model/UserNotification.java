package cn.vvbbnn00.canteen.model;
import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserNotification implements Serializable {
    private Integer notificationId;
    private Integer userId;
    private String content;
    private Boolean isRead; // tinyint can be represented as Boolean
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
}