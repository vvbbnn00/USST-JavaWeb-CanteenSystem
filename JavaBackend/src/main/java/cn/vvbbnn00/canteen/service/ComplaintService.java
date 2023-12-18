package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.ComplaintDao;
import cn.vvbbnn00.canteen.dao.impl.ComplaintDaoImpl;
import cn.vvbbnn00.canteen.dto.response.ImageInfoResponse;
import cn.vvbbnn00.canteen.model.*;
import cn.vvbbnn00.canteen.util.LogUtils;

import java.util.List;

public class ComplaintService {
    private static final ComplaintDao complaintDao = new ComplaintDaoImpl();
    private static final UserService userService = new UserService();
    private static final CanteenService canteenService = new CanteenService();
    private static final ImageService imageService = new ImageService();
    private static final CanteenAdminService canteenAdminService = new CanteenAdminService();
    private static final CommentService commentService = new CommentService();

    public Complaint createComplaint(Complaint complaint, List<String> fileKeys) {
        User user = userService.getUserById(complaint.getCreatedBy());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Canteen canteen = canteenService.getCanteenById(complaint.getCanteenId());
        if (canteen == null) {
            throw new RuntimeException("食堂不存在");
        }

        Complaint newComplaint = complaintDao.insert(complaint);
        if (newComplaint == null) {
            throw new RuntimeException("创建投诉失败");
        }

        if (fileKeys == null || fileKeys.isEmpty()) {
            return newComplaint;
        }

        if (fileKeys.size() > 3) {
            complaintDao.delete(newComplaint.getComplaintId());
            throw new RuntimeException("最多只能上传3张图片");
        }
        for (String fileKey : fileKeys) {
            if (!imageService.ifImageExist(fileKey)) {
                complaintDao.delete(newComplaint.getComplaintId());
                throw new RuntimeException("图片不存在");
            }
            Image image = new Image();
            image.setFileId(fileKey);
            image.setType(Image.ImageType.complaint);
            image.setReferenceId(newComplaint.getComplaintId());
            if (imageService.addImage(image) == null) {
                complaintDao.delete(newComplaint.getComplaintId());
                throw new RuntimeException("部分图片上传失败，投诉创建失败");
            }
        }

        return newComplaint;
    }

    public Complaint updateComplaint(Integer complaintId, Complaint.Status status) {
        return complaintDao.update(complaintId, status);
    }

    public Complaint closeComplaint(Integer complaintId, Integer userId) {
        Complaint complaint = complaintDao.queryById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }
        if (!complaint.getCreatedBy().equals(userId) &&
                !canteenAdminService.checkHasCanteenAdmin(complaint.getCanteenId(), userId)) {
            throw new RuntimeException("您没有权限关闭该投诉");
        }
        return complaintDao.update(complaintId, Complaint.Status.finished);
    }


    public Complaint getComplaintById(Integer complaintId) {
        Complaint complaint = complaintDao.queryById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }
        List<ImageInfoResponse> images = imageService.getImageInfoList(Image.ImageType.complaint, complaintId, false);
        complaint.setImageInfoList(images);

        List<Comment> comments = commentService.getCommentList(null,
                Comment.CommentType.complaint, complaintId, null, 1, 200,
                "createdAt", null);
        complaint.setComments(comments);

        return complaint;
    }


    public void replyComplaint(Integer complaintId, Integer userId, String content) {
        if (content == null || content.isEmpty()) {
            throw new RuntimeException("回复内容不能为空");
        }
        Complaint complaint = complaintDao.queryById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }
        if (!canteenAdminService.checkHasCanteenAdmin(complaint.getCanteenId(), userId) &&
                !complaint.getCreatedBy().equals(userId)) {
            throw new RuntimeException("您没有权限回复该投诉");
        }
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        Comment comment = new Comment();
        comment.setType(Comment.CommentType.complaint);
        comment.setReferenceId(complaintId);
        comment.setCreatedBy(userId);
        comment.setContent(content);
        comment.setParentId(null);
        try {
            commentService.createComment(comment, userId);
        } catch (Exception e) {
            LogUtils.severe(e.getMessage(), e);
            throw new RuntimeException(e.getMessage() != null ? e.getMessage() : "回复失败");
        }
        if (canteenAdminService.checkHasCanteenAdmin(complaint.getCanteenId(), userId)) {
            updateComplaint(complaintId, Complaint.Status.replied);
        } else {
            updateComplaint(complaintId, Complaint.Status.processing);
        }
    }


    public List<Complaint> getComplaintList(String kw, Integer userId, Integer canteenId,
                                            Complaint.Status status, String orderBy, Boolean asc,
                                            Integer page, Integer pageSize) {
        return complaintDao.queryComplaintList(kw, userId, canteenId, status, orderBy, asc, page, pageSize);
    }

    public Integer countComplaintList(String kw, Integer userId, Integer canteenId,
                                      Complaint.Status status) {
        return complaintDao.countComplaintList(kw, userId, canteenId, status);
    }
}