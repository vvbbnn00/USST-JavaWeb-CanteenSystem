package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ImageGenerationResponse extends BasicResponse {
    private String fileKey;
    private String url;
}
