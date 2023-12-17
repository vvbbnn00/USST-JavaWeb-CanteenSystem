package cn.vvbbnn00.canteen.dto.response;

import cn.vvbbnn00.canteen.model.User;
import lombok.Data;

@Data
public class UserLoginResponse {
    String avatar;
    User.Role role;
    String username;
    Integer userId;
    String email;
}
