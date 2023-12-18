package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;


@EqualsAndHashCode(callSuper = true)
@Data
public class ComplaintListRequest extends BasicListRequest {
    @Min(value = 1, message = "无效的食堂ID")
    private Integer canteenId;

    @Min(value = 1, message = "无效的用户ID")
    private Integer createdBy;

    @Pattern(regexp = "pending|processing|replied|finished", message = "无效的状态")
    private String status;

    @Pattern(regexp = "createdBy|canteenId|status|createdAt|updatedAt", message = "无效的排序字段")
    private String orderBy;
}
