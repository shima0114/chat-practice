package boot.sample.shima.chat.entity.key;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class ChatGroupStateKey implements Serializable {

    @Setter
    @Getter
    private String groupId;

    @Setter
    @Getter
    private String entryUserId;
}
