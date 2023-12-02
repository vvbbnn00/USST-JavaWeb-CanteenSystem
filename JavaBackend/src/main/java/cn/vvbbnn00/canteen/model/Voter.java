package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Voter implements Serializable {
    @Id
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
