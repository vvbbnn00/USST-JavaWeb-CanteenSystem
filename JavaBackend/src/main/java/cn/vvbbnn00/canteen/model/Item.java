package cn.vvbbnn00.canteen.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JavaBean
public class Item implements Serializable {
    private Integer itemId;

    @NotBlank(message = "菜品名称不能为空")
    @Size(max = 255, message = "菜品名称长度不能超过255个字符")
    private String name;
    private Integer cuisineId;

    @DecimalMin(value = "0.00", message = "菜品价格不能为负")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "促销价格不能为负")
    private BigDecimal promotionPrice;

    @Column(name = "introduction", columnDefinition = "TEXT")
    private String introduction;

    @DecimalMin(value = "0.00", message = "综合评分不能低于0.00")
    @DecimalMax(value = "5.00", message = "综合评分不能高于5.00")
    private BigDecimal compScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    private Cuisine cuisine;
}
