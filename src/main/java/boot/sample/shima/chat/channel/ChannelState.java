package boot.sample.shima.chat.channel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;

import lombok.Getter;
import lombok.Setter;

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

    protected ChannelState() {}

    public ChannelState(String entryUserId, String channelId) {
        this.entryUserId = entryUserId;
        this.channelId = channelId;
    }
}
