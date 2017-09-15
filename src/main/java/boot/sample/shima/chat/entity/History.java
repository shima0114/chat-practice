package boot.sample.shima.chat.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.*;

import boot.sample.shima.chat.entity.converter.LocalDateTimeConverter;
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
    private String type;
    @Convert(converter= LocalDateTimeConverter.class)
    private LocalDateTime sendTime;

    public History() {}

    public History(String id, String userId, String channelId, String userName, String message, String type) {
        this.id = id;
        this.channelId = channelId;
        this.userName = userName;
        this.message = message;
        this.type = type;
        this.sendTime = LocalDateTime.now();
    }

}
