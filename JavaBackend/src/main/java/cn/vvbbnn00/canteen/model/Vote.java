package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JavaBean
public class Vote implements Serializable {
    private Integer voteId;

    @Size(max = 255, message = "投票名称长度不能超过255个字符")
    private String voteName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Size(max = 500, message = "投票简介长度不能超过500个字符")
    private String voteIntro;
    private Boolean isStarted = false;

    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
    // private List<VoteOption> voteOptions;
}