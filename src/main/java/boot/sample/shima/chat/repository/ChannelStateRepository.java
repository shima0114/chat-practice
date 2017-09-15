package boot.sample.shima.chat.repository;

import java.util.List;

import boot.sample.shima.chat.entity.ChannelState;
import boot.sample.shima.chat.entity.key.ChannelStateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelStateRepository extends JpaRepository<ChannelState, ChannelStateKey> {
    public ChannelState findByChannelIdAndEntryUserId(String channelId, String userId);

    public List<ChannelState> findAllByChannelId(String channelId);

    public int countByChannelId(String channelId);

    public List<ChannelState> findAllByEntryUserIdOrderByJoiningDateTime(String entryUserId);

}
