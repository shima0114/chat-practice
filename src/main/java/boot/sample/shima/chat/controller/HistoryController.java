package boot.sample.shima.chat.controller;

import boot.sample.shima.chat.entity.History;
import boot.sample.shima.chat.service.ChannelService;
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

    @RequestMapping("/history/load")
    public List<History> load(@RequestParam String channelId) {
        return historyService.getHistories(channelId);
    }

}
