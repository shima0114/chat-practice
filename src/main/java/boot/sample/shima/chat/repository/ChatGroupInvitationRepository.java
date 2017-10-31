package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChatGroupInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupInvitationRepository extends JpaRepository<ChatGroupInvitation, String> {

    void deleteByGroupIdAndUserId(String groupId, String userId);

    void deleteByGroupId(String groupId);

    List<ChatGroupInvitation> findAllByUserId(String userId);
}
