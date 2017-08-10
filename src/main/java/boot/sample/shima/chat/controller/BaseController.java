package boot.sample.shima.chat.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boot.sample.shima.chat.channel.Channel;
import boot.sample.shima.chat.channel.ChannelService;
import boot.sample.shima.chat.history.HistoryService;
import boot.sample.shima.chat.user.ChatUser;

@Controller
public class BaseController {

    @Autowired
    private SimpMessagingTemplate smt;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private HistoryService historyService;

    @RequestMapping("/")
    public String showMainPage(Model model) {
        model.addAttribute("prompt_msg", "ログイン情報を入力してください");
        return "index";
    }

    @RequestMapping("/talk")
    public String showChannelListPage(Model model, @RequestParam String channel_name, @RequestParam String channel_id,
                                    @RequestParam String user_name, @RequestParam String user_id) {
        if (!channelService.existsChannel(channel_id)) {
            model.addAttribute("channelList", channelService.getAllChannel());
            return "index";
        }
        channelService.joinChannel(channel_id, user_id);
        Channel channel = channelService.getChannelFromId(channel_id);
        model.addAttribute("channel_id", channel.getId());
        model.addAttribute("user_name", user_name);
        model.addAttribute("user_id", user_id);
        model.addAttribute("channel_name", channel.getChannelName());
        model.addAttribute("del_auth", user_id.equals(channel.getCreateUserId()));
        model.addAttribute("histories", historyService.getHistories(channel_id));
        return "talk";
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
        model.addAttribute("channelList", channelService.getAvailableChannels(user.getUserId()));
        return "enter";
    }

    @RequestMapping("/admin")
    public String showAdminPage(Model model) {
        return "admin";
    }

    @MessageMapping("/channel/{id}")
    public String sendMessage(@DestinationVariable String id, @RequestParam String message) {
        smt.convertAndSend("/topic/channel/" + id, message);
        return message;
    }


    /* Without STOMP test */
    @RequestMapping("/talk2")
    public String showChannelListPageWithoutStomp(Model model, @RequestParam String channel_name, @RequestParam String channel_id,
                                    @RequestParam String user_name, @RequestParam String user_id) {
        if (!channelService.existsChannel(channel_id)) {
            model.addAttribute("channelList", channelService.getAllChannel());
            return "index";
        }
        channelService.joinChannel(channel_id, user_id);
        Channel channel = channelService.getChannelFromId(channel_id);
        model.addAttribute("channel_id", channel.getId());
        model.addAttribute("user_name", user_name);
        model.addAttribute("user_id", user_id);
        model.addAttribute("channel_name", channel.getChannelName());
        model.addAttribute("del_auth", user_id.equals(channel.getCreateUserId()));
        model.addAttribute("histories", historyService.getHistories(channel_id));
        return "talk2";
    }
}
