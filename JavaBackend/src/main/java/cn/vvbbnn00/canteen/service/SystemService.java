package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.ConfigUtils;
import cn.vvbbnn00.canteen.util.LogUtils;

public class SystemService {
    public void init(){
        LogUtils.info("System init start...");
        UserService userService = new UserService();
        User user = userService.getUserByUsername(ConfigUtils.getEnv("ADMIN_USERNAME", "admin"));
        if (user == null) {
            LogUtils.info("Create admin user...");
            User userInsert = new User();
            userInsert.setUsername(ConfigUtils.getEnv("ADMIN_USERNAME", "admin"));
            userInsert.setPassword(ConfigUtils.getEnv("ADMIN_PASSWORD", "admin"));
            userInsert.setRole(User.Role.admin);
            userInsert.setAvailable(true);
            userService.createUser(userInsert);
        }
        LogUtils.info("System init success.");
    }
}
