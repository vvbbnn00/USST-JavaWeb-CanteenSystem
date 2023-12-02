package cn.vvbbnn00.canteensystemjavabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Cuisine {
    @Id
    private Integer cuisineId;

    @NotBlank(message = "菜系名称不能为空")
    @Size(max = 50, message = "菜系名称长度不能超过50个字符")
    private String name;
    private Integer canteenId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Canteen canteen;
}
