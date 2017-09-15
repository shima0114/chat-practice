package boot.sample.shima.chat.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class Channel {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;

    private String channelName;

    private String createUserId;

    @Column(columnDefinition="boolean default false")
    private boolean invalidateFlag;

    private String channelScope;

    public Channel() {}

    public Channel(String id, String channelName, String createUserId, boolean invalidateFlag, String channelScope) {
        this.id = id;
        this.channelName = channelName;
        this.createUserId = createUserId;
        this.invalidateFlag = invalidateFlag;
        this.channelScope = channelScope;
    }

    @Override
    public String toString() {
        return String.format("TalkChannel[id=%s, channelName=%s, createUserName=%s, invalidate=%s, channelScope=%s]",
                this.id, this.channelName, this.createUserId, this.invalidateFlag, this.channelScope);
    }
}
