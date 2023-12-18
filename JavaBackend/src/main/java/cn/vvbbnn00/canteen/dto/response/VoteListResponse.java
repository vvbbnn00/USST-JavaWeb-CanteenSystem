package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.beans.JavaBean;

@EqualsAndHashCode(callSuper = true)
@Data
@JavaBean
public class VoteListResponse extends BasicResponse {
    private Object data;
    private Object list;
}
