package boot.sample.shima.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boot.sample.shima.chat.room.ChatRoom;
import boot.sample.shima.chat.room.ChatRoomService;
import boot.sample.shima.chat.room.ChatRoomService.Rooms;

@RestController
public class RoomController {

    @Autowired
    ChatRoomService rooms;

    @RequestMapping(value="/room/make",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createRoom(@RequestParam String roomName, @RequestParam String userId) {
        Map<String, String> jMap = new HashMap<>();
        if (rooms.existsRoomName(roomName)) {
            jMap.put("result", "exists");
        } else {
            ChatRoom room = rooms.addRoom(roomName, userId);
            Rooms createRooms = rooms.getRooms(room.getId());
            jMap.put("result","success");
            jMap.put("id", room.getId());
            jMap.put("name", room.getRoomName());
            jMap.put("createUserId", room.getCreateUserId());
            jMap.put("invalidateFlag", Boolean.toString(room.isInvalidateFlag()));
            jMap.put("state", createRooms.getState());
        }
        return jMap;
    }

    @RequestMapping(value="/room/delete",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> deleteRoom(@RequestParam String roomId) {
        Map<String, String> jMap = new HashMap<>();
        try {
            rooms.closeRoom(roomId);
            jMap.put("result","success");
        } catch (Exception e) {
            jMap.put("result", "failure");
        }
        return jMap;
    }

    @RequestMapping(value="/room/restore",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> restoreRoom(@RequestParam String roomId) {
        Map<String, String> jMap = new HashMap<>();
        try {
            rooms.restoreRoom(roomId);
            jMap.put("result","success");
        } catch (Exception e) {
            jMap.put("result", "failure");
        }
        return jMap;
    }


    @RequestMapping(value="/room/leave",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> leaveRoom(@RequestParam String roomId, @RequestParam String userId) {
        Map<String, String> jMap = new HashMap<>();
        try {
            rooms.leaveRoom(roomId, userId);
            jMap.put("result","success");
        } catch (Exception e) {
            jMap.put("result", "failure");
        }
        return jMap;
    }

    @RequestMapping(value="/room/listupdate",produces=MediaType.APPLICATION_JSON_VALUE)
    public List<Rooms> roomListUpdate(@RequestParam String userId) {
//        Map<String, String> jMap = new HashMap<>();
//        try {
            List<Rooms> roomList = rooms.getAvailableRooms(userId);
            // TODO roomListから部屋選択リスト作成用にJSONを作成する
//            jMap.put("result","success");
//        } catch (Exception e) {
//            jMap.put("result", "failure");
//        }
        return roomList;
    }
}
