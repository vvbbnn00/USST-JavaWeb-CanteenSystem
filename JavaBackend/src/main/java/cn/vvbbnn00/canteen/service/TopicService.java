package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.TopicDao;
import cn.vvbbnn00.canteen.dao.impl.TopicDaoImpl;
import cn.vvbbnn00.canteen.dto.response.ImageInfoResponse;
import cn.vvbbnn00.canteen.model.Image;
import cn.vvbbnn00.canteen.model.Topic;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.util.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class TopicService {
    private static final TopicDao topicDao = new TopicDaoImpl();
    private static final ImageService imageService = new ImageService();
    private static final UserPointLogService userPointLogService = new UserPointLogService();
    private static final UserService userService = new UserService();

    /**
     * 添加话题
     *
     * @param topic 话题
     * @return 话题
     */
    public Topic addTopic(Topic topic) {
        if (topic.getTitle() == null || topic.getTitle().isBlank()) {
            throw new RuntimeException("标题不能为空");
        }
        if (topic.getContent() == null || topic.getContent().isBlank()) {
            throw new RuntimeException("内容不能为空");
        }
        if (topic.getCreatedBy() == null) {
            throw new RuntimeException("创建者不能为空");
        }

        Topic newTopic = topicDao.insert(topic, topic.getCreatedBy());
        if (newTopic == null) {
            throw new RuntimeException("添加话题失败");
        }

        List<String> fileKeyList = topic.getImages();
        if (fileKeyList == null || fileKeyList.isEmpty()) {
            return newTopic;
        }

        if (fileKeyList.size() > 9) {
            topicDao.delete(newTopic.getTopicId());
            throw new RuntimeException("图片数量不能超过9张");
        }

        List<ImageInfoResponse> imageInfoList = new ArrayList<>();
        for (String fileKey : fileKeyList) {
            if (!imageService.ifImageExist(fileKey)) {
                continue;
            }
            Image image = new Image();
            image.setFileId(fileKey);
            image.setReferenceId(newTopic.getTopicId());
            image.setType(Image.ImageType.topic);
            new ImageService().addImage(image);
            imageInfoList.add(imageService.getImageInfo(fileKey, false));
        }
        newTopic.setImageInfoList(imageInfoList);

        userPointLogService.changeUserPoint(topic.getCreatedBy(), 5, "发布话题" + topic.getTopicId());

        return newTopic;
    }


    /**
     * 删除话题
     *
     * @param topicId 话题id
     * @param userId  用户id
     */
    public void deleteTopic(Integer topicId, Integer userId) {
        Topic topic = topicDao.queryTopicById(topicId, userId);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }
        User user = new UserService().getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!topic.getCreatedBy().equals(user.getUserId()) && !user.getRole().equals(User.Role.admin)) {
            throw new RuntimeException("只能删除自己的话题");
        }

        userPointLogService.changeUserPoint(topic.getCreatedBy(), -5, "删除话题" + topic.getTopicId());

        topicDao.delete(topicId);
    }


    /**
     * 根据id获取话题
     *
     * @param topicId 话题id
     * @param userId  用户id
     * @return 话题
     */
    public Topic getTopicById(Integer topicId, Integer userId) {
        Topic topic = topicDao.queryTopicById(topicId, userId);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }
        List<String> imageList = topic.getImages();
        List<ImageInfoResponse> imageInfoList = new ArrayList<>();
        User user = userService.getUserById(topic.getCreatedBy());
        topic.setUser(user);

        if (imageList == null || imageList.isEmpty()) {
            topic.setImageInfoList(imageInfoList);
            return topic;
        }
        for (String fileKey : imageList) {
            imageInfoList.add(imageService.getImageInfo(fileKey, false));
        }
        topic.setImageInfoList(imageInfoList);
        return topic;
    }


    /**
     * 获取话题列表
     *
     * @param userId      用户id
     * @param page        页码
     * @param pageSize    页大小
     * @param orderBy     排序字段
     * @param asc         是否升序
     * @param queryUserId 查询用户id
     * @return 话题列表
     */
    public List<Topic> getTopicList(
            Integer userId, Integer page, Integer pageSize, String orderBy, Boolean asc,
            Integer queryUserId
    ) {
        List<Topic> topicList = topicDao.queryTopics(userId, page, pageSize, orderBy, asc, queryUserId);
        for (Topic topic : topicList) {
            List<String> imageList = topic.getImages();
            if (imageList == null || imageList.isEmpty()) {
                continue;
            }
            List<ImageInfoResponse> imageInfoList = new ArrayList<>();
            for (String fileKey : imageList) {
                imageInfoList.add(imageService.getImageInfo(fileKey, false));
            }
            topic.setImageInfoList(imageInfoList);
        }
        return topicList;
    }


    /**
     * 获取话题列表数量
     *
     * @param userId 用户id
     * @return 话题列表数量
     */
    public Integer getTopicListCount(Integer userId) {
        return topicDao.count(userId);
    }
}
