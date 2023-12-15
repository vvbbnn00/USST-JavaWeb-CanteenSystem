package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@JavaBean
public class Voter implements Serializable {
    private Integer voterId;
    private Integer voteId;
    private Integer userId;
    private Integer optionId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Vote vote;
    private User user;
    private VoteOption voteOption;
}
