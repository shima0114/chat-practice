package boot.sample.shima.chat.entity;

import boot.sample.shima.chat.entity.key.ChatGroupScopeKey;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Setter
@Getter
public class ChatGroup {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private String id;
    private String groupName;
    private String scope;
    private String joinPassword;
    private String createUserId;

    public ChatGroup() {}

    public ChatGroup(String id, String groupName, String scope, String joinPassword, String createUserId) {
        this.id = id;
        this.groupName = groupName;
        this.scope = scope;
        this.joinPassword = joinPassword;
        this.createUserId = createUserId;
    }

    public boolean needAuthorized() {
        return !StringUtils.isEmpty(joinPassword);
    }
}
