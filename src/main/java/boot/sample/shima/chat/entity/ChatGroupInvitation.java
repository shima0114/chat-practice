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
public class ChatGroupInvitation {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;
    private String groupId;
    private String userId;

    public ChatGroupInvitation() {}

    public ChatGroupInvitation(String id, String groupId, String userId) {
        this.id = id;
        this.groupId = groupId;
        this.userId = userId;
    }
}
