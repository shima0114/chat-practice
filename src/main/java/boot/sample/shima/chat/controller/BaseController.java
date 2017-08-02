package boot.sample.shima.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boot.sample.shima.chat.history.HistoryService;
import boot.sample.shima.chat.room.ChatRoom;
import boot.sample.shima.chat.room.ChatRoomService;

@Controller
public class BaseController {

    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private ChatRoomService rooms;

    @Autowired
    private HistoryService histories;

    @RequestMapping("/")
    public String showMainPage(Model model) {
        model.addAttribute("prompt_msg", "ログイン情報を入力してください");
        return "index";
    }

    @RequestMapping("/talk")
    public String showTalkPage(Model model, @RequestParam String room_name, @RequestParam String room_id,
                                    @RequestParam String user_name, @RequestParam String user_id) {
        if (!rooms.existsRoom(room_id)) {
            model.addAttribute("roomList", rooms.getAllRoom());
            return "index";
        }
        rooms.joinRoom(room_id, user_id);
        ChatRoom room = rooms.getRoomFromId(room_id);
        model.addAttribute("room_id", room.getId());
        model.addAttribute("user_name", user_name);
        model.addAttribute("user_id", user_id);
        model.addAttribute("room_name", room.getRoomName());
        model.addAttribute("del_auth", user_id.equals(room.getCreateUserId()));
        model.addAttribute("histories", histories.getHistories(room_id));
        return "talk";
    }

    @RequestMapping("/admin")
    public String showAdminPage(Model model) {
        return "admin";
    }

    @MessageMapping("/room/{id}")
    public String sendMessage(@DestinationVariable String id, @RequestParam String message) {
        smt.convertAndSend("/topic/room/" + id, message);
        return message;
    }
}
