package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JavaBean
public class VoteOption implements Serializable {
    private Integer voteOptionId;
    private Integer voteId;

    @Size(max = 100, message = "选项名称长度不能超过100个字符")
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Vote vote;
}