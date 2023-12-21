package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TopicListRequest extends BasicListRequest {
    @Min(value = 1, message = "无效的用户Id")
    private Integer userId;

    @Pattern(regexp = "^(createdAt|likeCount|commentCount|compValue|createdBy)$",
            message = "无效的排序字段")
    private String orderBy;
}
