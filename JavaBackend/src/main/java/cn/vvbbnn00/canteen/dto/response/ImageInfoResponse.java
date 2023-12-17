package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;

@Data
public class ImageInfoResponse {
    private String fileKey;
    private String originalUrl;
    private String x128Url;
    private String x256Url;
    private String x384Url;
}
