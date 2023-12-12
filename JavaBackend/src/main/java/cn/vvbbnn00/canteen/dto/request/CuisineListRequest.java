package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CuisineListRequest extends BasicListRequest{
    @Pattern(regexp = "^(cuisine_id|name|canteen_id|created_at|updated_at)$",
            message = "orderBy必须是cuisine_id,name,canteen_id,created_at,updated_at中的一个")
    private String orderBy;
}
