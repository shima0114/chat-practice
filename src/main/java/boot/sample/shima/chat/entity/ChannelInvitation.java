package boot.sample.shima.chat.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class ChannelInvitation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;

    private String channelId;
    private String scope;
    private String targetId;

    public ChannelInvitation() {}

    public ChannelInvitation(String id, String channelId, String scope, String targetId) {
        this.id = id;
        this.channelId = channelId;
        this.scope = scope;
        this.targetId = targetId;
    }
}
