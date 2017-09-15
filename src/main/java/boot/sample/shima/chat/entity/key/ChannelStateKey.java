package boot.sample.shima.chat.entity.key;

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
