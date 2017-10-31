package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChatGroupState;
import boot.sample.shima.chat.entity.key.ChatGroupStateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatGroupStateRepository extends JpaRepository<ChatGroupState, ChatGroupStateKey> {
    List<ChatGroupState> findAllByEntryUserId(String entryUserId);
    List<ChatGroupState> findAllByGroupId(String groupId);
    ChatGroupState findOneByGroupIdAndEntryUserId(String groupId, String entryUserId);
    void deleteByGroupIdAndEntryUserIdNotIn(String groupId, List<String> userIds);
}
