package cn.vvbbnn00.canteen.dao;

import cn.vvbbnn00.canteen.model.Complaint;

import java.util.List;

public interface ComplaintDao {
    /**
     * 提交投诉
     *
     * @param complaint 投诉信息
     * @return 新投诉的完整信息
     */
    Complaint insert(Complaint complaint);

    /**
     * 更新投诉状态
     *
     * @param complaintId 投诉的id
     * @param status      投诉的新状态
     * @return 更新后的投诉信息
     */
    Complaint update(Integer complaintId, Complaint.Status status);

    /**
     * 通过id查询投诉
     *
     * @param complaintId 投诉的id
     * @return 对应id的投诉信息
     */
    Complaint queryById(Integer complaintId);

    /**
     * 查询投诉列表
     *
     * @param kw        关键词
     * @param userId    用户的id
     * @param canteenId 食堂的id
     * @param status    投诉的状态
     * @param orderBy   排序方式
     * @param asc       是否升序
     * @param page      分页查询的页码
     * @param pageSize  分页查询的每页数量
     * @return 投诉信息列表
     */
    List<Complaint> queryComplaintList(String kw, Integer userId, Integer canteenId,
                                              Complaint.Status status, String orderBy, Boolean asc,
                                              Integer page, Integer pageSize);

    /**
     * 统计投诉数量
     *
     * @param kw        关键词
     * @param userId    用户的id
     * @param canteenId 食堂的id
     * @param status    投诉的状态
     * @return 总投诉量
     */
    Integer countComplaintList(String kw, Integer userId, Integer canteenId,
                                      Complaint.Status status);

    /**
     * 删除投诉
     * @param complaintId 投诉的id
     */
    void delete(Integer complaintId);
}