package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.TopicDao;
import cn.vvbbnn00.canteen.dao.impl.TopicDaoImpl;
import cn.vvbbnn00.canteen.dto.response.ImageInfoResponse;
import cn.vvbbnn00.canteen.model.Image;
import cn.vvbbnn00.canteen.model.Topic;
import cn.vvbbnn00.canteen.model.User;

import java.util.ArrayList;
import java.util.List;

public class TopicService {
    private static final TopicDao topicDao = new TopicDaoImpl();
    private static final ImageService imageService = new ImageService();

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

        return newTopic;
    }


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
        topicDao.delete(topicId);
    }


    public Topic getTopicById(Integer topicId, Integer userId) {
        Topic topic = topicDao.queryTopicById(topicId, userId);
        if (topic == null) {
            throw new RuntimeException("话题不存在");
        }
        List<String> imageList = topic.getImages();
        List<ImageInfoResponse> imageInfoList = new ArrayList<>();
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


    public Integer getTopicListCount(Integer userId) {
        return topicDao.count(userId);
    }
}
