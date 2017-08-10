package boot.sample.shima.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boot.sample.shima.chat.channel.ChannelService;
import boot.sample.shima.chat.user.ChatUser;
import boot.sample.shima.chat.user.ChatUserService;

@Controller
public class UserController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private ChatUserService userService;

    @RequestMapping("/create")
    public String createUser(Model model, @RequestParam String newUserId, @RequestParam String newUserName,
                                            @RequestParam String newPassword1, @RequestParam String newPassword2) {
        ChatUser chatUser = userService.create(newUserId, newUserName, newPassword1);
        model.addAttribute("userId", chatUser.getUserId());
        model.addAttribute("prompt_msg", "パスワードを入力し作成したユーザーでログインしてください。");
        return "index";
    }
}
