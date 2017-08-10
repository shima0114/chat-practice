package boot.sample.shima.chat.history;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class HistoryService {

    @Autowired
    private HistoryRepository repo;

    private List<History> historyList = new ArrayList<>();

    public void registMessageHistory(String channelId, String userId, String userName, String message) {
        History history = new History();
        history.setChannelId(channelId);
        history.setUserId(userId);
        history.setUserName(userName);
        history.setMessage(message);
        LocalDateTime ldt = LocalDateTime.now();
        history.setYmdDate(ldt.format(DateTimeFormatter.ISO_LOCAL_DATE));
        history.setHmsTime(ldt.format(DateTimeFormatter.ISO_LOCAL_TIME));
        repo.save(history);
    }

    public List<History> getHistories(String channelId) {
        historyList = repo.findLatestHistory(channelId);
        if (historyList == null) {
            return Collections.emptyList();
        }
        return historyList;
    }

    public void deleteClosedChannelHistoryBefore3Days(String channelId) {
        repo.deleteClosedChannelHistoryBefore3Days(channelId);
    }

    public boolean hasClosedChannelHistory(String channelId) {
        List<History> hList = repo.findByChannelIdOrderByYmdDateDescHmsTimeDescIdAsc(channelId);
        return hList == null ? false : hList.isEmpty() ? false : true;
    }
}
