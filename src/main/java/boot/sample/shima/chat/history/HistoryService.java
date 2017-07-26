package boot.sample.shima.chat.history;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class HistoryService {

    @Autowired
    private HistoryRepository repo;

    private List<History> historyList = new ArrayList<>();

    public void registMessageHistory(String roomId, String userName, String message) {
        History history = new History();
        history.setRoomId(roomId);
        history.setUserName(userName);
        history.setMessage(message);
        history.setRegistDate(new Timestamp(System.currentTimeMillis()));
        repo.save(history);
    }

    public List<History> getHistories(String roomId) {
        historyList = repo.findLatestHistory(roomId);
        if (historyList == null) {
            return Collections.emptyList();
        }
        return historyList;
    }

    public void deleteClosedRoomHistoryBefore3Days(String roomId) {
        repo.deleteClosedRoomHistoryBefore3Days(roomId);
    }

    public boolean hasClosedRoomHistory(String roomId) {
        List<History> hList = repo.findByRoomIdOrderByRegistDateDescIdAsc(roomId);
        return hList == null ? false : hList.isEmpty() ? false : true;
    }
}
