package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.MessageDao;
import cn.vvbbnn00.canteen.dao.impl.MessageDaoImpl;
import cn.vvbbnn00.canteen.dao.UserDao;
import cn.vvbbnn00.canteen.model.User;
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
        if(message.getFromUser()==null){
            throw new RuntimeException("发送者不能为空");
        }
        User fromUser = new UserService().getUserById(message.getFromUser().getUserId());
        if(fromUser==null){
            throw new RuntimeException("发送者不存在");
        }
        if(message.getToUser()==null){
            throw new RuntimeException("接收者不能为空");
        }
        User toUser = new UserService().getUserById(message.getToUser().getUserId());
        if(toUser==null){
            throw new RuntimeException("接收者不存在");
        }
        if(message.getContent()==null){
            throw new RuntimeException("消息内容不能为空");
        }
        boolean result = messageDao.insert(message);
        if(!result){
            throw new RuntimeException("发送失败");
        }
        return true;
    }
    /**
     * 删除消息
     *
     * @param id 消息id
     * @return 是否成功
     */
    public boolean deleteMessage(Integer id) {
        UserMessage message = messageDao.queryMessageById(id);
        if(message==null){
            throw new RuntimeException("消息不存在");
        }
        return messageDao.delete(id);
    }
}
