package jp.co.commerce.sample.shima.chat.task.taskjob;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import jp.co.commerce.sample.shima.chat.history.HistoryService;
import jp.co.commerce.sample.shima.chat.room.ChatRoom;
import jp.co.commerce.sample.shima.chat.room.ChatRoomService;

@Component
public class HistoryTaskService {

    @Autowired
    HistoryService historys;

    @Autowired
    ChatRoomService rooms;

    @Value("${batch.name.host}")
    String batchHostName;

    @Scheduled(fixedDelay=30000)
    public void deleteNotExistsHistory() {
        List<ChatRoom> roomList = rooms.getClosedRoomId();
        roomList.stream()
                .forEach(room -> {
                    historys.deleteClosedRoomHistoryBefore3Days(room.getId());
                    boolean keepRoom = historys.hasClosedRoomHistory(room.getId());
                    if (!keepRoom) {
                        rooms.deleteRoom(room.getId());
                    }
                });
    }
}
