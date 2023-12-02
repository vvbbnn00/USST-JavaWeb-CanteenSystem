package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class TopicLike implements Serializable {
    @Id
    private Integer topicId;
    @Id
    private Integer userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Topic topic;
    private User user;
}