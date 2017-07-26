package jp.co.commerce.sample.shima.chat.room;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

public class ChatRoomService {

    @Autowired
    private RoomRepository repo;

    public ChatRoom addRoom(ChatRoom room) {
        return repo.save(room);
    }

    public ChatRoom addRoom(String roomName, String createUserId) {
        ChatRoom room = new ChatRoom();
        room.setRoomName(roomName);
        room.setCreateUserId(createUserId);
        return addRoom(room);
    }

    public List<ChatRoom> getAllRoom() {
        return repo.findByInvalidateFlagFalse();
    }

    public List<ChatRoom> getAvailableRooms(String createUserId) {
        return repo.findByInvalidateFlagFalseOrCreateUserId(createUserId);
    }

    public List<ChatRoom> getClosedRoomId() {
        return repo.findByInvalidateFlagTrue();
    }

    public ChatRoom getRoomFromId(String id) {
        ChatRoom room = repo.findByIdAndInvalidateFlagFalse(id);
        return room;
    }

    public ChatRoom getRoomFromName(String roomName) {
        ChatRoom room = repo.findByRoomNameAndInvalidateFlagFalse(roomName);
        if (room != null) {
            return room;
        } else {
            return new ChatRoom();
        }
    }

    public boolean existsRoom(String roomId) {
        if (getRoomFromId(roomId) == null) {
            return false;
        }
        return true;
    }

    public void closeRoom(String id) {
        changeInvalidateFlag(id, true);
    }

    public void restoreRoom(String id) {
        changeInvalidateFlag(id, false);
    }

    private void changeInvalidateFlag(String id, boolean flag) {
        ChatRoom room = repo.findByIdAndInvalidateFlagFalse(id);
        room.setInvalidateFlag(flag);
         repo.save(room);
    }

    public void deleteRoom(String id) {
        repo.delete(id);
    }

    public boolean existsRoomName(String roomName) {
        int cnt = repo.countRoomNameByRoomNameAndInvalidateFlagFalse(roomName);
        return cnt > 0;
    }
}
