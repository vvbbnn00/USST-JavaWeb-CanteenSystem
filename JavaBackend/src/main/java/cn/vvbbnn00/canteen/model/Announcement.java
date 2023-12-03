package cn.vvbbnn00.canteen.model;

import lombok.Data;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Announcement implements Serializable {
    private Integer announcementId;

    @NotNull(message = "食堂ID不能为空")
    private Integer canteenId;

    @NotBlank(message = "公告标题不能为空")
    @Size(max = 255, message = "公告标题长度不能超过255个字符")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Size(max = 5000, message = "公告内容长度不能超过5000个字符")
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Canteen canteen;
}
