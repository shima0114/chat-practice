package boot.sample.shima.chat.repository;

import java.util.List;

import boot.sample.shima.chat.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, String> {
    Channel findByChannelNameAndInvalidateFlagFalse(String channelName);

    Channel findByIdAndInvalidateFlagFalse(String id);

    Channel findById(String id);

    List<Channel> findByInvalidateFlagFalse();

    List<Channel> findByInvalidateFlagTrue();

    Integer countChannelNameByChannelNameAndInvalidateFlagFalse(String channelName);

    List<Channel> findAllByChannelScope(String channelScope);

}
