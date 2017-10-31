package boot.sample.shima.chat.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@Setter
public class ChannelGroup {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;

    private String channelId;
    private String groupId;

    public ChannelGroup() {}

    public ChannelGroup(String id, String channelId, String groupId) {
        this.id = id;
        this.channelId = channelId;
        this.groupId = groupId;
    }
}
