package boot.sample.shima.chat.history;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    private String channelId;
    private String userId;
    private String userName;
    private String message;
    private String ymdDate;
    private String hmsTime;

    protected History() {}

    public History(String id, String userId, String channelId, String userName, String message) {
        this.id = id;
        this.channelId = channelId;
        this.userName = userName;
        this.message = message;
        LocalDateTime ldt = LocalDateTime.now();
        this.ymdDate =ldt.format(DateTimeFormatter.ISO_LOCAL_DATE);
        this.hmsTime = ldt.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

}
