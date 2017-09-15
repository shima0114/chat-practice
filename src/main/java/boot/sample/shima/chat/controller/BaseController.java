package boot.sample.shima.chat.controller;

import java.security.Principal;

import boot.sample.shima.chat.entity.key.ChannelScopeKey;
import boot.sample.shima.chat.service.AttachmentFileService;
import boot.sample.shima.chat.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import boot.sample.shima.chat.service.ChannelService;
import boot.sample.shima.chat.service.HistoryService;
import boot.sample.shima.chat.entity.ChatUser;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BaseController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ChatUserService userService;

    @Autowired
    private AttachmentFileService fileService;

    @RequestMapping("/")
    public String showMainPage(Model model) {
        model.addAttribute("prompt_msg", "ログイン情報を入力してください");
        return "index";
    }

    @RequestMapping("/create")
    public String createUser(Model model, @RequestParam String newUserId, @RequestParam String newUserName,
                             @RequestParam String newPassword1, @RequestParam String newPassword2) {
        ChatUser chatUser = userService.create(newUserId, newUserName, newPassword1);
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
        model.addAttribute("channelList", channelService.getJoiningChannels(user.getUserId()));
        model.addAttribute("invitationList", channelService.getInvitationChannels(user.getUserId(),ChannelScopeKey.USER.getId()));
        model.addAttribute("abstentionList", channelService.getAbstentionChannels(user.getUserId()));
        model.addAttribute("userList", userService.loadAllOther(user.getUserId()));
        model.addAttribute("channelScope", ChannelScopeKey.values());
        return "enter";
    }

    @RequestMapping("/admin")
    public String showAdminPage(Model model) {
        return "admin";
    }
}
