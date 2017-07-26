package jp.co.commerce.sample.shima.chat.room;

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
public class ChatRoom {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;

    private String roomName;

    private String createUserId;

    @Column(columnDefinition="boolean default false")
    private boolean invalidateFlag;

    protected ChatRoom() {}

    public ChatRoom(String id, String roomName, String createUserId, boolean invalidateFlag) {
        this.id = id;
        this.roomName = roomName;
        this.createUserId = createUserId;
        this.invalidateFlag = invalidateFlag;
    }

    @Override
    public String toString() {
        return String.format("ChatRoom[id=%s, roomName=%s, createUserName=%s, invalidate=%s]",
                this.id, this.roomName, this.createUserId, this.invalidateFlag);
    }
}
