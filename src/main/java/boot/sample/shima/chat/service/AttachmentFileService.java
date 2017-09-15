package boot.sample.shima.chat.service;

import boot.sample.shima.chat.entity.AttachmentFile;
import boot.sample.shima.chat.repository.AttachmentFileRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AttachmentFileService {

    @Autowired
    AttachmentFileRepository repo;

    public List<AttachmentFile> getAttachmentFiles(String channelId) {
        return repo.findAllByChannelId(channelId);
    }

    public AttachmentFile getAttachmentFile(String id) {
        return repo.findOne(id);
    }

    public AttachmentFile save(AttachmentFile file) {
        return repo.save(file);
    }

}
