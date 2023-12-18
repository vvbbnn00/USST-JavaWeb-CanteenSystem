package cn.vvbbnn00.canteen.service;

import cn.vvbbnn00.canteen.dao.UserMessageDao;
import cn.vvbbnn00.canteen.dao.impl.UserMessageDaoImpl;
import cn.vvbbnn00.canteen.model.User;
import cn.vvbbnn00.canteen.model.UserMessage;

import java.util.List;

public class UserMessageService {
    private static final UserMessageDao messageDao = new UserMessageDaoImpl();

    /**
     * 获取消息列表
     *
     * @param UserId   接收者id
     * @return 消息列表
     */
    public List<User> getMessagedUserList(Integer UserId) {
        return messageDao.queryMessagedUsers(
                UserId
        );
    }
    /**
     * 获取消息列表数量
     *
     * @param toUserId   接收者id
     * @return 消息列表数量
     */
    public List<UserMessage> getMessageList(Integer toUserId, Integer fromUserId) {
        return messageDao.queryMessagesList(
                toUserId, fromUserId
        );
    }

    /**
     * 发送消息
     *
     * @param message 消息
     * @return 是否成功
     */
    public boolean sendMessage(UserMessage message) {
        if(message.getFromUserId()==null){
            throw new RuntimeException("发送者不能为空");
        }
        User fromUser = new UserService().getUserById(message.getFromUserId());
        if(fromUser==null){
            throw new RuntimeException("发送者不存在");
        }
        if(message.getToUserId()==null){
            throw new RuntimeException("接收者不能为空");
        }
        User toUser = new UserService().getUserById(message.getToUserId());
        if(toUser==null){
            throw new RuntimeException("接收者不存在");
        }
        if(message.getContent()==null){
            throw new RuntimeException("消息内容不能为空");
        }
        if(message.getContent().length()>200){
            throw new RuntimeException("消息内容长度不能超过200个字符");
        }
        boolean result = messageDao.insert(message);
        if(!result){
            throw new RuntimeException("发送失败");
        }
        return true;
    }
}
