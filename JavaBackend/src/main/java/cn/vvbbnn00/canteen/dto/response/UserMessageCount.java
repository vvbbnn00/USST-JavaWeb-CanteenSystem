package cn.vvbbnn00.canteen.dto.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserMessageCount {
    private Integer userId;
    private String username;
    private Boolean isVerified;
    private Integer level;
    private String avatar;
    private BigDecimal totalMessages;
}
