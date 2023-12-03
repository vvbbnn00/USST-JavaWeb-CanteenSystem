package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Comment implements Serializable {
    private Integer commentId;

    @Enumerated(EnumType.STRING)
    private CommentType type;

    @NotNull(message = "评论对象ID不能为空")
    private Integer referenceId;

    @Column(name = "created_by")
    private Integer createdBy;

    @Size(max = 500, message = "评论内容长度不能超过500个字符")
    private String content;

    @DecimalMin(value = "0.00", message = "评分不能低于0.00")
    @DecimalMax(value = "5.00", message = "评分不能高于5.00")
    private BigDecimal score;
    private Integer parentId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum CommentType {
        canteen, item, complaint, topic
    }

    private User user;
    private Canteen canteen;
}