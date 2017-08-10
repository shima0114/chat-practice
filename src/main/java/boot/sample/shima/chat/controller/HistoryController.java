package boot.sample.shima.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boot.sample.shima.chat.history.HistoryService;

@RestController
public class HistoryController {

    @Autowired
    private HistoryService histories;

    @RequestMapping("/history/write")
    public void write(@RequestParam String  channelId,@RequestParam String userId, @RequestParam String userName, @RequestParam String message) {
        histories.registMessageHistory(channelId, userId, userName, message);
    }

}
