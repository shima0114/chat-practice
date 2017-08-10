package boot.sample.shima.chat.channel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelStateRepository extends JpaRepository<ChannelState, ChannelStateKey> {
    public ChannelState findByChannelIdAndEntryUserId(String channelId, String userId);

    public List<ChannelState> findAllByChannelId(String channelId);

    public int countByChannelId(String channelId);

}
