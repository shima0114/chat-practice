package boot.sample.shima.chat.room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomStateRepository extends JpaRepository<ChatRoomState, String> {
    public ChatRoomState findByRoomIdAndEntryUserId(String roomId, String userId);

    public List<ChatRoomState> findAllByRoomId(String roomId);

    public int countByRoomId(String roomId);

}
