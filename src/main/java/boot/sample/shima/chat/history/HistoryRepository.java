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
    public List<History> findByRoomIdOrderByRegistDateDescIdAsc(String roomId);

    @Query("select h from History h where roomId = :roomId and day(registDate) > (day(current_timestamp()) - 2) order by registDate ASC ")
    public List<History> findLatestHistory(@Param("roomId") String roomId);

    @Transactional
    @Modifying
    @Query("delete from History h where h.roomId = :roomId and day(h.registDate) < (day(current_timestamp()) -2)")
    public int deleteClosedRoomHistoryBefore3Days(@Param("roomId") String roomId);
}
