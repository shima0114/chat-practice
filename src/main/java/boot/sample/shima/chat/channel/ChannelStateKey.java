package boot.sample.shima.chat.channel;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

public class ChannelStateKey implements Serializable {

    @Setter
    @Getter
    private String entryUserId;

    @Setter
    @Getter
    private String channelId;
}
