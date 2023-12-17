package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class AnnouncementListRequest extends BasicListRequest {

    @Min(value = 1, message = "食堂ID必须大于0")
    private Integer canteenId;

    @Pattern(regexp = "^(announcementId|canteenId|createdAt|updatedAt)$",
            message = "orderBy必须是announcementId,canteenId,createdAt,updatedAt中的一个")
    private String orderBy;

}
