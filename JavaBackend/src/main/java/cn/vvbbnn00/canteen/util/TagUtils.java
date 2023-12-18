package cn.vvbbnn00.canteen.util;

import cn.vvbbnn00.canteen.model.*;

public class TagUtils {
    /**
     * 生成带有特定类型，标识符和内容的标签字符串。
     *
     * @param type    标签的类型
     * @param id      标签的标识符
     * @param content 标签的内容
     * @return 返回生成的标签字符串
     */
    public static String generateTag(String type, Integer id, String content) {
        return "<" + type + " id=" + id + ">" + content + "</" + type + ">";
    }

    /**
     * 根据给定的用户生成用户标签字符串。
     *
     * @param user 要生成标签的用户对象
     * @return 返回生成的用户标签字符串，如果用户为空则返回默认的用户标签字符串
     */
    public static String generateTag(User user) {
        if (user == null) {
            return generateTag("user", 0, "N/A");
        }
        return generateTag("user", user.getUserId(), user.getUsername());
    }

    /**
     * 根据给定的话题生成话题标签字符串。
     *
     * @param topic 要生成标签的话题对象
     * @return 返回生成的话题标签字符串，如果话题为空则返回默认的话题标签字符串
     */
    public static String generateTag(Topic topic) {
        if (topic == null) {
            return generateTag("topic", 0, "N/A");
        }
        return generateTag("topic", topic.getTopicId(), topic.getTitle());
    }

    /**
     * 根据给定的食堂生成食堂标签字符串。
     *
     * @param canteen 要生成标签的食堂对象
     * @return 返回生成的食堂标签字符串，如果食堂为空则返回默认的食堂标签字符串
     */
    public static String generateTag(Canteen canteen) {
        if (canteen == null) {
            return generateTag("canteen", 0, "N/A");
        }
        return generateTag("canteen", canteen.getCanteenId(), canteen.getName());
    }

    /**
     * 根据给定的投诉生成投诉标签字符串。
     *
     * @param complaint 要生成标签的投诉对象
     * @return 返回生成的投诉标签字符串，如果投诉为空则返回默认的投诉标签字符串
     */
    public static String generateTag(Complaint complaint) {
        if (complaint == null) {
            return generateTag("complaint", 0, "N/A");
        }
        return generateTag("complaint", complaint.getComplaintId(), complaint.getTitle());
    }

    /**
     * 根据给定的物品生成物品标签字符串。
     *
     * @param item 要生成标签的物品对象
     * @return 返回生成的物品标签字符串，如果物品为空则返回默认的物品标签字符串
     */
    public static String generateTag(Item item) {
        if (item == null) {
            return generateTag("item", 0, "N/A");
        }
        return generateTag("item", item.getItemId(), item.getName());
    }
}