package boot.sample.shima.chat.channel;

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

    protected Channel() {}

    public Channel(String id, String channelName, String createUserId, boolean invalidateFlag) {
        this.id = id;
        this.channelName = channelName;
        this.createUserId = createUserId;
        this.invalidateFlag = invalidateFlag;
    }

    @Override
    public String toString() {
        return String.format("TalkChannel[id=%s, channelName=%s, createUserName=%s, invalidate=%s]",
                this.id, this.channelName, this.createUserId, this.invalidateFlag);
    }
}
