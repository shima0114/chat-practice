package boot.sample.shima.chat.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class ChatGroup {

    @Id
    private String id;
    private String groupName;
    private String joinPassword;

    public ChatGroup() {}

    public ChatGroup(String id, String groupName, String joinPassword) {
        this.id = id;
        this.groupName = groupName;
        this.joinPassword = joinPassword;
    }
}
