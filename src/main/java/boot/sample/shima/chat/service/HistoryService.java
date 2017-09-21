package boot.sample.shima.chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import boot.sample.shima.chat.entity.History;
import boot.sample.shima.chat.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class HistoryService {

    @Autowired
    private HistoryRepository repo;

    private List<History> historyList = new ArrayList<>();

    public void resistMessageHistory(String channelId, String userId, String userName, String message, String type) {
        History history = new History();
        history.setChannelId(channelId);
        history.setUserId(userId);
        history.setUserName(userName);
        history.setMessage(message);
        history.setType(type);
        history.setSendTime(LocalDateTime.now());
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
        List<History> hList = repo.findByChannelIdOrderBySendTimeDescIdAsc(channelId);
        return hList == null ? false : hList.isEmpty() ? false : true;
    }

    public int getUnreadCount(String channelId, LocalDateTime dateTime) {
        if (dateTime == null) {
            dateTime = LocalDateTime.MIN;
        }
        return repo.countIntByChannelIdAndSendTimeAfter(channelId, dateTime);
    }

    public int getAllCount(String channelId) {
        return repo.countIntByChannelId(channelId);
    }

    public List<String> getSenderUserIdList(String channelId) {
        return repo.getSendingUserIds(channelId);
    }
}
