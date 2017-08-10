package boot.sample.shima.chat.task.taskjob;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import boot.sample.shima.chat.channel.Channel;
import boot.sample.shima.chat.channel.ChannelService;
import boot.sample.shima.chat.history.HistoryService;

@Component
public class HistoryTaskService {

    @Autowired
    HistoryService historys;

    @Autowired
    ChannelService rooms;

    @Value("${batch.name.host}")
    String batchHostName;

    //@Scheduled(fixedDelay=60000)
    public void deleteNotExistsHistory() {
        List<Channel> roomList = rooms.getClosedChannelId();
        roomList.stream()
                .forEach(room -> {
                    historys.deleteClosedChannelHistoryBefore3Days(room.getId());
                    boolean keepRoom = historys.hasClosedChannelHistory(room.getId());
                    if (!keepRoom) {
                        rooms.deleteChannel(room.getId());
                    }
                });
    }
}
