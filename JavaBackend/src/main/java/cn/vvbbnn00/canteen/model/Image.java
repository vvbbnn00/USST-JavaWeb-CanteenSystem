package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class Image implements Serializable {
    @Id
    private Integer imageId;

    @NotBlank(message = "文件ID不能为空")
    @Size(max = 255, message = "文件ID长度不能超过255个字符")
    private String fileId;

    @Enumerated(EnumType.STRING)
    private ImageType type; // canteen, item, complaint, topic
    private Integer referenceId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ImageType {
        canteen, item, complaint, topic
    }
}
