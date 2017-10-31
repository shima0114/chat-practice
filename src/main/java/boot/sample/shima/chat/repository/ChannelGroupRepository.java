package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChannelGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChannelGroupRepository extends JpaRepository<ChannelGroup, String> {
    List<ChannelGroup> findAllByGroupId(String groupId);
    List<ChannelGroup> findAllByChannelId(String channelId);
}
