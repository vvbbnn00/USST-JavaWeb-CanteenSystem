package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.User;

import java.util.List;

public interface UserDao {
    /**
     * 插入一个用户
     *
     * @param user 用户，由于作为新数据插入数据库，其id、createdAt、updatedAt属性会被忽略
     */
    boolean insert(User user);

    /**
     * 根据id查询用户
     *
     * @param id 用户id
     * @return 用户
     */
    User queryUserById(Integer id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户
     */
    User queryUserByUsername(String username);

    /**
     * 更新用户信息
     *
     * @param user 用户，此处判断更新的用户是根据id来判断的，注意更新密码时，需要先对密码进行加密
     * @return 是否成功
     */
    boolean update(User user);

    /**
     * 根据id删除用户
     *
     * @param id 用户id
     * @return 是否成功
     */
    boolean delete(Integer id);

    /**
     * 查询用户列表，参数为空可忽略这个条件
     *
     * @param page       页码 从1开始
     * @param pageSize   每页大小 默认10
     * @param kw         关键词，模糊匹配用户名、姓名、工号
     * @param available  是否可用
     * @param role       角色
     * @param isVerified 是否已验证
     * @param orderBy    排序字段，支持：id、username、name、employeeId、level、point、available、role、isVerified、createdAt、updatedAt、lastLoginAt
     * @param asc        是否升序
     * @return 用户列表
     */
    List<User> queryUsers(Integer page, Integer pageSize, String kw, Boolean available, User.Role role, Boolean isVerified, String orderBy, Boolean asc);

    /**
     * 查询用户数量，参数为空可忽略这个条件
     *
     * @param kw         关键词，模糊匹配用户名、姓名、工号
     * @param available  是否可用
     * @param role       角色
     * @param isVerified 是否已验证
     * @return 用户数量
     */
    Integer queryUsersCount(String kw, Boolean available, User.Role role, Boolean isVerified);

    /**
     * 批量查询用户
     *
     * @param userIds 用户id列表
     * @return 用户列表
     */
    List<User> batchQueryUsers(List<Integer> userIds);

}
