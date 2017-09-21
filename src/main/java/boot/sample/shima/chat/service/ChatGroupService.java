package boot.sample.shima.chat.service;

import boot.sample.shima.chat.entity.ChatGroup;
import boot.sample.shima.chat.repository.ChatGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class ChatGroupService {

    @Autowired
    ChatGroupRepository repo;

    public ChatGroup create(String groupName, String joinPassword) {
        ChatGroup group = new ChatGroup();
        group.setGroupName(groupName);
        group.setJoinPassword(new BCryptPasswordEncoder().encode(joinPassword));
        return repo.save(group);
    }

    public boolean authorization(String id, String joinPassword) {
        ChatGroup group = repo.findOne(id);
        String cryptPassword = new BCryptPasswordEncoder().encode(joinPassword);
        if (group.getJoinPassword().equals(cryptPassword)) {
            return true;
        }
        return false;
    }
}
