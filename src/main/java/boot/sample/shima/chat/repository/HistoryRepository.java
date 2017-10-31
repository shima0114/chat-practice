package boot.sample.shima.chat.repository;

import java.time.LocalDateTime;
import java.util.List;

import boot.sample.shima.chat.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HistoryRepository  extends JpaRepository<History, String> {
    List<History> findByChannelIdOrderBySendTimeDescIdAsc(String channelId);

    @Query("select h from History h where channelId = :channelId order by sendTime ASC ")
    List<History> findLatestHistory(@Param("channelId") String channelId);

    @Transactional
    @Modifying
    @Query("delete from History h where h.channelId = :channelId")
    int deleteClosedChannelHistoryBefore3Days(@Param("channelId") String channelId);

    int countIntByChannelIdAndSendTimeAfter(String channelId, LocalDateTime dateTime);

    int countIntByChannelId(String channelId);

    @Query("select h.userId from History h where h.channelId = :channelId group by h.userId")
    List<String> getSendingUserIds(@Param("channelId") String channelId);
}
