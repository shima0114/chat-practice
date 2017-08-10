package boot.sample.shima.chat.channel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {
    public Channel findByChannelNameAndInvalidateFlagFalse(String channelName);

    public Channel findByIdAndInvalidateFlagFalse(String id);

    public Channel findById(String id);

    public List<Channel> findByInvalidateFlagFalse();

    public List<Channel> findByInvalidateFlagFalseOrCreateUserIdOrderById(String createUserId);

    public List<Channel> findByInvalidateFlagTrue();

    public int countChannelNameByChannelNameAndInvalidateFlagFalse(String channelName);

}
