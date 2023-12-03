package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Vote implements Serializable {
    private Integer voteId;

    @Size(max = 255, message = "投票名称长度不能超过255个字符")
    private String voteName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Size(max = 500, message = "投票简介长度不能超过500个字符")
    private String voteIntro;
    private Boolean ifMore; // tinyint can be represented as Boolean
    private Integer max;

    @Min(value = 1, message = "最少选择数不能小于1")
    private Integer min;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
}