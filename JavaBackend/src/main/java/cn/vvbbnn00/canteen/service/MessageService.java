package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.MessageDao;
import cn.vvbbnn00.canteen.dao.impl.MessageDaoImpl;
import cn.vvbbnn00.canteen.model.UserMessage;

import java.util.List;

public class MessageService {
    private final MessageDao messageDao = new MessageDaoImpl();

    /**
     * 获取消息列表
     *
     * @param toUserId   接收者id
     * @return 消息列表
     */
    public List<UserMessage> getMessageList(Integer toUserId) {
        return messageDao.queryMessages(
                toUserId
        );
    }
    /**
     * 获取消息列表数量
     *
     * @param toUserId   接收者id
     * @return 消息列表数量
     */
    public int getMessageListCount(Integer toUserId) {
        return messageDao.queryMessagesCount(
                toUserId
        );
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @return 是否成功
     */
    public boolean sendMessage(UserMessage message) {
        return messageDao.insert(message);
    }
    /**
     * 删除消息
     *
     * @param id 消息id
     * @return 是否成功
     */
    public boolean deleteMessage(Integer id) {
        return messageDao.delete(id);
    }
}
