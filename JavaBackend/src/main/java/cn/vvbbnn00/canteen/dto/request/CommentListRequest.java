package cn.vvbbnn00.canteen.dto.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CommentListRequest extends BasicListRequest{
    private String type;

    @Pattern(regexp = "^(comment_id|type|reference_id|created_by|content|score|parent_id|created_at|updated_at)$",
            message = "orderBy必须是comment_id,type,reference_id,created_by,content,score,parent_id,created_at,updated_at中的一个")
    private String orderBy;
}
