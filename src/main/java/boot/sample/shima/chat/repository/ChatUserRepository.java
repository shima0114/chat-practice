package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, String> {
    public ChatUser findByUserName(String username);
    public ChatUser findByUserId(String userId);
    public List<ChatUser> findAllByUserIdNot(String userId);
}
