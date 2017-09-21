package boot.sample.shima.chat.controller;

import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.entity.History;
import boot.sample.shima.chat.service.ChannelService;
import boot.sample.shima.chat.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boot.sample.shima.chat.service.HistoryService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ChatUserService userService;

    @RequestMapping("/history/load")
    public Map<String, Object> load(@RequestParam String channelId) {
        Map<String, Object> retMap = new HashMap<>();
        List<String> userIdList = historyService.getSenderUserIdList(channelId);
        Map<String, String> imageMap = new HashMap<>();
        userIdList.stream()
                .forEach(userId -> {
                    ChatUser user = userService.loadUserByUserId(userId);
                    imageMap.put(userId, user.userImageBase64());
                });
        retMap.put("user_images", imageMap);
        retMap.put("history", historyService.getHistories(channelId));
        return retMap;
    }

}
