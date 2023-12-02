package cn.vvbbnn00.canteensystemjavabackend.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserMessage {
    @Id
    private Integer messageId;
    private Integer fromUserId;
    private Integer toUserId;

    @Size(max = 200, message = "消息内容长度不能超过200个字符")
    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private User fromUser;
    private User toUser;
}