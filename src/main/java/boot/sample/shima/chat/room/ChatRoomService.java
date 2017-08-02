package boot.sample.shima.chat.room;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

public class ChatRoomService {

    @Autowired
    private RoomRepository repo;

    @Autowired
    private RoomStateRepository stateRepo;

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

    public Rooms getRooms(String roomId) {
        ChatRoom chatRoom = repo.findById(roomId);
        return new Rooms(chatRoom, getState(chatRoom));
    }

    public List<Rooms> getAvailableRooms(String createUserId) {
        List<ChatRoom> roomList = repo.findByInvalidateFlagFalseOrCreateUserId(createUserId);
        List<Rooms> rooms = new ArrayList<>();
        roomList.stream()
            .forEach(chatRoom -> {
                String state = getState(chatRoom);
                rooms.add(new Rooms(chatRoom, state));
            });

        return rooms;
    }

    private String getState(ChatRoom chatRoom) {
        String state = "";
        if (chatRoom.isInvalidateFlag()) {
            state = "閉鎖中";
        } else {
            int entryNum = stateRepo.countByRoomId(chatRoom.getId());
            state = entryNum + "人が入室中";
        }
        return state;
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
        leaveAll(id);
    }

    public void restoreRoom(String id) {
        changeInvalidateFlag(id, false);
    }

    private void changeInvalidateFlag(String id, boolean flag) {
        ChatRoom room = repo.findById(id);
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

    public void joinRoom(String roomId, String userId) {
        ChatRoomState state = new ChatRoomState(roomId, userId);
        stateRepo.save(state);
    }

    public void leaveRoom(String roomId, String userId) {
        ChatRoomState state = stateRepo.findByRoomIdAndEntryUserId(roomId, userId);
        if (state != null) {
            stateRepo.delete(state);
        }
    }

    private void leaveAll(String roomId) {
        List<ChatRoomState> states = stateRepo.findAllByRoomId(roomId);
        states.stream().forEach(state -> {
            stateRepo.delete(state);
        });
    }

    @Setter
    @Getter
    public class Rooms {
        private ChatRoom room;
        private String state;

        public Rooms(ChatRoom room, String state) {
            this.room = room;
            this.state = state;
        }
    }
}
