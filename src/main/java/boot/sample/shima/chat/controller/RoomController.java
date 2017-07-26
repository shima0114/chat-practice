package jp.co.commerce.sample.shima.chat.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jp.co.commerce.sample.shima.chat.room.ChatRoom;
import jp.co.commerce.sample.shima.chat.room.ChatRoomService;

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
            jMap.put("result","success");
            jMap.put("id", room.getId());
            jMap.put("name", room.getRoomName());
            jMap.put("createId", room.getCreateUserId());
        }
        return jMap;
    }

    @RequestMapping(value="/room/delete",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> deleteRoom(@RequestParam String roomId) {
        Map<String, String> jMap = new HashMap<>();
        rooms.closeRoom(roomId);
        jMap.put("result","success");
        return jMap;
    }

    @RequestMapping(value="/room/restore",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> restoreRoom(@RequestParam String roomId) {
        Map<String, String> jMap = new HashMap<>();
        rooms.restoreRoom(roomId);
        jMap.put("result","success");
        return jMap;
    }
}
