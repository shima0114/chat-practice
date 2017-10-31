package boot.sample.shima.chat.entity;

import boot.sample.shima.chat.entity.converter.LocalDateTimeConverter;
import boot.sample.shima.chat.entity.key.ChatGroupStateKey;
import boot.sample.shima.chat.service.ChatGroupService;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@IdClass(value= ChatGroupStateKey.class)
public class ChatGroupState {
    @Id
    private String groupId;
    @Id
    private String entryUserId;

    @Convert(converter= LocalDateTimeConverter.class)
    private LocalDateTime joiningDateTime;

    private ChatGroupState() {}

    public ChatGroupState(String groupId, String entryUserId) {
        this.groupId = groupId;
        this.entryUserId = entryUserId;
        this.joiningDateTime =  LocalDateTime.now();
    }
}
