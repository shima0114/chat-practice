package boot.sample.shima.chat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boot.sample.shima.chat.channel.Channel;
import boot.sample.shima.chat.channel.ChannelService;
import boot.sample.shima.chat.channel.ChannelService.Channels;

@RestController
public class ChannelController {

    @Autowired
    ChannelService channelService;

    @RequestMapping(value="/channel/make",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createChannel(@RequestParam String channelName, @RequestParam String userId) {
        Map<String, String> jMap = new HashMap<>();
        if (channelService.existsChannelName(channelName)) {
            jMap.put("result", "exists");
        } else {
            Channel channel = channelService.addChannel(channelName, userId);
            // IDが自動生成のためチャンネル情報を取り直す
            Channels channels = channelService.getChannels(channel.getId(), userId);
            // 参加する
            channelService.joinChannel(channel.getId(), userId);
            jMap.put("result","success");
            jMap.put("id", channel.getId());
            jMap.put("name", channel.getChannelName());
            jMap.put("createUserId", channel.getCreateUserId());
            jMap.put("invalidateFlag", Boolean.toString(channel.isInvalidateFlag()));
            jMap.put("state", channels.getState());
        }
        return jMap;
    }

    @RequestMapping(value="/channel/delete",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> deleteChannel(@RequestParam String channelId) {
        Map<String, String> jMap = new HashMap<>();
        try {
            channelService.closeChannel(channelId);
            jMap.put("result","success");
        } catch (Exception e) {
            jMap.put("result", "failure");
        }
        return jMap;
    }

    @RequestMapping(value="/channel/restore",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> restoreChannel(@RequestParam String channelId) {
        Map<String, String> jMap = new HashMap<>();
        try {
            channelService.restoreChannel(channelId);
            jMap.put("result","success");
        } catch (Exception e) {
            jMap.put("result", "failure");
        }
        return jMap;
    }

    @RequestMapping(value="/channel/withdrawal",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> leaveChannel(@RequestParam String channelId, @RequestParam String userId) {
        Map<String, String> jMap = new HashMap<>();
        try {
            channelService.withdrawalChannel(channelId, userId);
            jMap.put("result","success");
        } catch (Exception e) {
            jMap.put("result", "failure");
        }
        return jMap;
    }

    @RequestMapping(value="/channel/listupdate",produces=MediaType.APPLICATION_JSON_VALUE)
    public List<Channels> channelListUpdate(@RequestParam String userId) {
        List<Channels> channelList = channelService.getAvailableChannels(userId);
        return channelList;
    }
}
