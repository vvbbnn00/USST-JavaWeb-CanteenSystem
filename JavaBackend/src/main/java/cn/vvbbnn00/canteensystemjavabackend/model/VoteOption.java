package cn.vvbbnn00.canteensystemjavabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VoteOption {
    @Id
    private Integer voteOptionId;
    private Integer voteId;

    @Size(max = 100, message = "选项名称长度不能超过100个字符")
    private String name;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Vote vote;
}