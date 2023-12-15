package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JavaBean
public class Topic implements Serializable {
    private Integer topicId;

    @Size(max = 255, message = "标题长度不能超过255个字符")
    private String title;

    @Size(max = 5000, message = "内容长度不能超过5000个字符")
    private String content;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private User user;
}
