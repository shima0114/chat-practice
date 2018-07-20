package boot.sample.shima.chat.repository;

import java.util.List;

import boot.sample.shima.chat.entity.ChannelState;
import boot.sample.shima.chat.entity.key.ChannelStateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelStateRepository extends JpaRepository<ChannelState, ChannelStateKey> {
    ChannelState findByChannelIdAndEntryUserId(String channelId, String entryUserId);

    List<ChannelState> findAllByChannelId(String channelId);

    int countByChannelId(String channelId);

    List<ChannelState> findAllByEntryUserIdOrderByJoiningDateTime(String entryUserId);

}
