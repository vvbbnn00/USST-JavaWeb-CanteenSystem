package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.UserDao;
import cn.vvbbnn00.canteen.dao.impl.UserDaoImpl;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.SafetyUtils;

public class LoginService {
    private final UserDao userDao = new UserDaoImpl();

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户
     */
    public User login(String username, String password) {
        User user = userDao.queryUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }
        String truePass = user.getPassword();
        if (!SafetyUtils.passwordCheckBCrypt(password, truePass)) {
            throw new RuntimeException("用户名或密码错误");
        }
        user.setPassword(null);
        return user;
    }
}
