package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChatUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, String> {
    ChatUser findByUserName(String username);
    ChatUser findByUserId(String userId);
    List<ChatUser> findAllByUserIdNot(String userId);
    ChatUser findByUserIdAndPassword(String userId, String password);
}
