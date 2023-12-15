package cn.vvbbnn00.canteen.filter.role_check;

import cn.vvbbnn00.canteen.model.User;

public class RoleCheckFilter {

    /**
     * 检查用户是否具有所需的角色。
     *
     * @param user          用户对象
     * @param requiredRole  所需的角色字符串
     * @return 如果用户具有所需的角色，则返回 1；如果没有，则返回 0；如果用户为 null，则返回 -1
     */
    public static int checkRole(User user, String requiredRole) {
        if (user == null) {
            return -1;
        }
        boolean checkPass = false;
        String userRole = user.getRole().toString();
        switch (requiredRole) {
            case "admin":
                if (userRole.equals(User.Role.admin.toString())) {
                    checkPass = true;
                }
                break;
            case "canteen_admin":
                if (userRole.equals(User.Role.canteen_admin.toString())) {
                    checkPass = true;
                }
                if (userRole.equals(User.Role.admin.toString())) {
                    checkPass = true;
                }
                break;
            default:
                checkPass = true;
                break;
        }
        return checkPass ? 1 : 0;
    }
}
