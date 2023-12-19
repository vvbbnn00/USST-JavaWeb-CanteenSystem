package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.UserDao;
import cn.vvbbnn00.canteen.dao.impl.UserDaoImpl;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.ConfigUtils;
import cn.vvbbnn00.canteen.util.SafetyUtils;

import java.util.List;

public class UserService {
    private static final UserDao userDao = new UserDaoImpl();
    private static final UserPointLogService userPointLogService = new UserPointLogService();

    /**
     * 获取用户列表
     *
     * @param page       页码
     * @param pageSize   每页大小
     * @param kw         关键词
     * @param available  是否可用
     * @param role       角色
     * @param isVerified 是否已验证
     * @param orderBy    排序字段
     * @param asc        是否升序
     * @return 用户列表
     */
    public List<User> getUserList(Integer page, Integer pageSize, String kw, Boolean available, User.Role role, Boolean isVerified, String orderBy, Boolean asc) {
        return userDao.queryUsers(
                page,
                pageSize,
                kw,
                available,
                role,
                isVerified,
                orderBy,
                asc
        );
    }

    /**
     * 获取用户列表数量
     *
     * @param kw         关键词
     * @param available  是否可用
     * @param role       角色
     * @param isVerified 是否已验证
     * @return 用户列表数量
     */
    public int getUserListCount(String kw, Boolean available, User.Role role, Boolean isVerified) {
        return userDao.queryUsersCount(
                kw,
                available,
                role,
                isVerified
        );
    }

    /**
     * 根据id获取用户，不返回密码
     *
     * @param id 用户id
     * @return 用户
     */
    public User getUserById(Integer id) {
        User user = userDao.queryUserById(id);
        if (user == null) {
            return null;
        }
        user.setPassword(null); // 不返回密码
        return user;
    }

    /**
     * 根据用户名获取用户，不返回密码
     *
     * @param username 用户名
     * @return 用户
     */
    public User getUserByUsername(String username) {
        User user = userDao.queryUserByUsername(username);
        if (user == null) {
            return null;
        }
        user.setPassword(null); // 不返回密码
        return user;
    }

    /**
     * 更新用户信息
     *
     * @param user 用户，此处判断更新的用户是根据id来判断的，注意更新密码时，需要先对密码进行加密
     * @return 更新后的用户
     */
    public User updateUser(User user) {
        String adminUsername = ConfigUtils.getEnv("ADMIN_USERNAME", "admin");
        User newUser = userDao.queryUserById(user.getUserId());
        if (newUser == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getUsername() != null && !user.getUsername().isEmpty()) {
            if (!user.getUsername().equals(newUser.getUsername())) {
                User existUser = userDao.queryUserByUsername(user.getUsername());
                if (existUser != null) {
                    throw new RuntimeException("用户名已存在");
                }
            }
            newUser.setUsername(user.getUsername());
        }
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            newUser.setPassword(SafetyUtils.passwordDoBCrypt(user.getPassword()));
        }
        if (user.getName() != null && !user.getName().isEmpty()) {
            newUser.setName(user.getName());
        }
        if (user.getEmployeeId() != null && !user.getEmployeeId().isEmpty()) {
            newUser.setEmployeeId(user.getEmployeeId());
        }
        if (user.getAvailable() != null) {
            if (adminUsername.equals(newUser.getUsername()) && !user.getAvailable()) {
                throw new RuntimeException("系统内置用户不能禁用");
            }
            newUser.setAvailable(user.getAvailable());
        }
        if (user.getRole() != null) {
            if (adminUsername.equals(newUser.getUsername()) && !user.getRole().equals(User.Role.admin)) {
                throw new RuntimeException("系统内置用户不能修改角色");
            }
            newUser.setRole(user.getRole());
        }
        if (user.getIsVerified() != null) {
            newUser.setIsVerified(user.getIsVerified());
        }
        if (user.getPoint() != null) {
            newUser.setPoint(user.getPoint());
        }
        if (user.getLevel() != null) {
            newUser.setLevel(user.getLevel());
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            newUser.setEmail(user.getEmail());
        }
        boolean success = userDao.update(newUser);
        if (success) {
            return getUserById(newUser.getUserId());
        }
        throw new RuntimeException("更新用户失败");
    }

    /**
     * 删除用户
     *
     * @param id 用户id
     */
    public void deleteUser(Integer id) {
        User user = userDao.queryUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (ConfigUtils.getEnv("ADMIN_USERNAME", "admin").equals(user.getUsername())) {
            throw new RuntimeException("系统内置用户不能删除");
        }
        boolean success = userDao.delete(id);
        if (success) {
            return;
        }
        throw new RuntimeException("删除用户失败");
    }

    /**
     * 创建用户
     *
     * @param user 用户
     * @return 创建成功的用户
     */
    public User createUser(User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            throw new RuntimeException("用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new RuntimeException("密码不能为空");
        }
        user.setPassword(SafetyUtils.passwordDoBCrypt(user.getPassword()));

        User existUser = userDao.queryUserByUsername(user.getUsername());
        if (existUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        user.setCreatedAt(null);
        user.setUpdatedAt(null);
        user.setLastLoginAt(null);
        user.setUserId(null);

        if (user.getRole() == null) {
            user.setRole(User.Role.user);
        }
        if (user.getAvailable() == null) {
            user.setAvailable(true);
        }
        if (user.getIsVerified() == null) {
            user.setIsVerified(false);
        }
        if (user.getEmail() == null) {
            user.setEmail("");
        }
        if (user.getPoint() == null) {
            user.setPoint(0L);
        }
        if (user.getLevel() == null) {
            user.setLevel(0);
        }
        boolean success = userDao.insert(user);
        if (success) {
            return getUserByUsername(user.getUsername());
        }
        throw new RuntimeException("创建用户失败");
    }


    /**
     * 验证用户
     *
     * @param id         用户id
     * @param employeeId 工号
     * @param name       姓名
     */
    public void verifyUser(Integer id, String employeeId, String name) {
        User user = userDao.queryUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (user.getIsVerified()) {
            throw new RuntimeException("用户已验证");
        }

        user.setEmployeeId(employeeId);
        user.setName(name);
        user.setIsVerified(true);

        boolean success = userDao.update(user);
        if (!success) {
            throw new RuntimeException("验证用户失败");
        }

        userPointLogService.changeUserPoint(user.getUserId(), 20, "验证用户" + user.getUserId());
    }


    /**
     * 修改用户密码
     *
     * @param userId      用户id
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    public void changeUserPassword(Integer userId, String oldPassword, String newPassword) {
        User user = userDao.queryUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!SafetyUtils.passwordCheckBCrypt(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }
        user.setPassword(SafetyUtils.passwordDoBCrypt(newPassword));
        boolean success = userDao.update(user);
        if (!success) {
            throw new RuntimeException("修改密码失败");
        }
    }
}
