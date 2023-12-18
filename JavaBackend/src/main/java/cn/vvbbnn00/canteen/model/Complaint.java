package cn.vvbbnn00.canteen.model;

import cn.vvbbnn00.canteen.dto.response.ImageInfoResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.beans.JavaBean;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JavaBean
public class Complaint implements Serializable {
    private Integer complaintId;

    @Min(value = 1, message = "无效的用户ID")
    private Integer createdBy;

    @Min(value = 1, message = "无效的食堂ID")
    private Integer canteenId;

    @Size(max = 100, message = "投诉标题长度不能超过100个字符")
    private String title;

    @Size(max = 2000, message = "投诉内容长度不能超过2000个字符")
    private String content;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Status {
        pending, processing, replied, finished
    }

    private List<String> imageList;
    private List<Comment> comments;
    private List<ImageInfoResponse> imageInfoList;
}
