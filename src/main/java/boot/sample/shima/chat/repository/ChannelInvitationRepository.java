package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChannelInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface ChannelInvitationRepository extends JpaRepository<ChannelInvitation, String> {
    List<ChannelInvitation> findAllByTargetId(String id);

    @Transactional
    void deleteByChannelIdAndTargetId(String channelId, String targetId);

    List<ChannelInvitation> findAllByChannelId(String channelId);
}
