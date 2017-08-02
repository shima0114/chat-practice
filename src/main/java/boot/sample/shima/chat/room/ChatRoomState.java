package boot.sample.shima.chat.room;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class ChatRoomState {
    @Id
    @Column(name="entry_user_id")
    private String entryUserId;
    private String roomId;

    protected ChatRoomState() {}

    public ChatRoomState(String roomId, String entryUserId) {
        this.roomId = roomId;
        this.entryUserId = entryUserId;
    }
}
