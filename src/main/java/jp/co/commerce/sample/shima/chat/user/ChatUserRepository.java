package jp.co.commerce.sample.shima.chat.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatUserRepository extends JpaRepository<ChatUser, String> {
    public ChatUser findByUserName(String username);
    public ChatUser findByUserId(String userId);
}
