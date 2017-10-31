package boot.sample.shima.chat.entity;

import javax.persistence.*;

import boot.sample.shima.chat.entity.converter.LocalDateTimeConverter;
import boot.sample.shima.chat.entity.key.ChannelStateKey;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Setter
@Getter
@Entity
@IdClass(value=ChannelStateKey.class)
public class ChannelState {
    @Id
    @Column(name="entry_user_id")
    private String entryUserId;
    @Id
    @Column(name="channel_id")
    private String channelId;

    @Convert(converter= LocalDateTimeConverter.class)
    private LocalDateTime joiningDateTime;

    @Convert(converter= LocalDateTimeConverter.class)
    private LocalDateTime lastLogin;

    public ChannelState() {}

    public ChannelState(String entryUserId, String channelId) {
        this.entryUserId = entryUserId;
        this.channelId = channelId;
        this.joiningDateTime = LocalDateTime.now();
//        this.lastLogin =LocalDateTime.now();
    }

    public LocalDateTime lastLogin() {
        return lastLogin != null ? lastLogin : joiningDateTime;
    }
}
