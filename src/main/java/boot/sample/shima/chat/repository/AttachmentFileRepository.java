package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.AttachmentFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttachmentFileRepository extends JpaRepository<AttachmentFile, String> {
    List<AttachmentFile> findAllByChannelId(String channelId);
}
