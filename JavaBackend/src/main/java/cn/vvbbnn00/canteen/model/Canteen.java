package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Canteen implements Serializable {
    @Id
    private Integer canteenId;

    @NotBlank(message = "食堂名称不能为空")
    @Size(max = 255, message = "食堂名称长度不能超过255个字符")
    private String name;

    @Size(max = 255, message = "食堂位置长度不能超过255个字符")
    private String location;

    @Size(max = 2000, message = "食堂简介长度不能超过2000个字符")
    private String introduction;

    @DecimalMin(value = "0.00", message = "综合评分不能低于0.00")
    @DecimalMax(value = "5.00", message = "综合评分不能高于5.00")
    private BigDecimal compScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
