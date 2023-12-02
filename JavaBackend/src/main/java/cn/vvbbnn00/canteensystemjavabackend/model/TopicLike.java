package cn.vvbbnn00.canteensystemjavabackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TopicLike {
    @Id
    private Integer topicId;
    @Id
    private Integer userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Topic topic;
    private User user;
}
