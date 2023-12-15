package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JavaBean
public class TopicLike implements Serializable {
    private Integer topicId;
    private Integer userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Topic topic;
    private User user;
}
