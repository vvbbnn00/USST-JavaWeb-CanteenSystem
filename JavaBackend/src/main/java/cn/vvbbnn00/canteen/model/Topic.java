package cn.vvbbnn00.canteen.model;

import cn.vvbbnn00.canteen.dto.response.ImageInfoResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

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
    private List<String> images;
    private List<ImageInfoResponse> imageInfoList;
    private Long likeCount;
    private Long commentCount;
    private Boolean isLiked;
    private Boolean flagged;
    private BigDecimal compValue; // 综合评分，用于排序
}
