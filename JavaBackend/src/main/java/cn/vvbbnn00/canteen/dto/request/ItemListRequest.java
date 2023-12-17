package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ItemListRequest extends BasicListRequest {
    @Min(value = 1, message = "无效的食堂Id")
    private Integer canteenId;
    @Min(value = 1, message = "无效的菜系Id")
    private Integer cuisineId;

    @Pattern(regexp = "^(price|compScore|promotionPrice|recommended|cuisineId|itemId|updatedAt|createdAt)$",
            message = "orderBy必须是price,compScore,promotionPrice,recommended,cuisineId,itemId,updatedAt,createdAt中的一个")
    private String orderBy;

    private Boolean recommended;
}
