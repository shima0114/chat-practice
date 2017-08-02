package boot.sample.shima.chat.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boot.sample.shima.chat.room.ChatRoomService;
import boot.sample.shima.chat.user.ChatUser;
import boot.sample.shima.chat.user.ChatUserService;

@Controller
public class UserController {

    @Autowired
    private ChatRoomService rooms;

    @Autowired
    private ChatUserService user;

    @RequestMapping("/create")
    public String createUser(Model model, @RequestParam String newUserId, @RequestParam String newUserName,
                                            @RequestParam String newPassword1, @RequestParam String newPassword2) {
        ChatUser chatUser = user.create(newUserId, newUserName, newPassword1);
        model.addAttribute("userId", chatUser.getUserId());
        model.addAttribute("prompt_msg", "パスワードを入力し作成したユーザーでログインしてください。");
        return "index";
    }

    @RequestMapping("/enter")
    public String entry(Principal principal, Model model) {
        Authentication auth = (Authentication)principal;
        ChatUser user;
        try {
            user = (ChatUser)auth.getPrincipal();
        } catch (ClassCastException e) {
            user = new ChatUser("admin", "admin", "admin", ChatUser.Authority.ROLE_ADMIN, true);
        }
        model.addAttribute("user", user);
        model.addAttribute("roomList", rooms.getAvailableRooms(user.getUserId()));
        return "enter";
    }
}
