package boot.sample.shima.chat.entity;

import boot.sample.shima.chat.entity.converter.LocalDateTimeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
public class AttachmentFile {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;
    private String channelId;
    private String userId;
    private String originalFileName;
    private String saveFileName;
    @Convert(converter= LocalDateTimeConverter.class)
    private LocalDateTime updateDataTime;

    public AttachmentFile() {}

    public AttachmentFile(String id, String channelId, String userId, String originalFileName, String saveFileName, LocalDateTime updateDataTime) {
        this.id = id;
        this.channelId = channelId;
        this.userId = userId;
        this.originalFileName = originalFileName;
        this.saveFileName = saveFileName;
        this.updateDataTime = updateDataTime;
    }

    public String id() {
        return id;
    }

    public String originalFileName() {
        return originalFileName;
    }
}
