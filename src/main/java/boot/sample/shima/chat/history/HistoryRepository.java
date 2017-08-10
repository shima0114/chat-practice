package boot.sample.shima.chat.history;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface HistoryRepository  extends JpaRepository<History, String> {
    public List<History> findByChannelIdOrderByYmdDateDescHmsTimeDescIdAsc(String channelId);

    @Query("select h from History h where channelId = :channelId order by ymdDate ASC, hmsTime ASC ")
    public List<History> findLatestHistory(@Param("channelId") String channelId);

    @Transactional
    @Modifying
    @Query("delete from History h where h.channelId = :channelId")
    public int deleteClosedChannelHistoryBefore3Days(@Param("channelId") String channelId);
}
