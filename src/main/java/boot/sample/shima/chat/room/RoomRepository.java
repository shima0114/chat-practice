package jp.co.commerce.sample.shima.chat.room;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<ChatRoom, String> {
    public ChatRoom findByRoomNameAndInvalidateFlagFalse(String roomName);

    public ChatRoom findByIdAndInvalidateFlagFalse(String id);

    public List<ChatRoom> findByInvalidateFlagFalse();

    public List<ChatRoom> findByInvalidateFlagFalseOrCreateUserId(String createUserId);

    public List<ChatRoom> findByInvalidateFlagTrue();

    public int countRoomNameByRoomNameAndInvalidateFlagFalse(String roomName);

}
