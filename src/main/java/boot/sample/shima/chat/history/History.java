package jp.co.commerce.sample.shima.chat.history;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class History {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private String id;
    private String roomId;
    private String userName;
    private String message;
    private Timestamp registDate;

    protected History() {}

    public History(String id, String roomId, String userName, String message) {
        this.id = id;
        this.roomId = roomId;
        this.userName = userName;
        this.message = message;
        this.registDate = new Timestamp(System.currentTimeMillis());
    }

}
