package boot.sample.shima.chat.controller;

import java.util.*;

import boot.sample.shima.chat.entity.AttachmentFile;
import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.entity.key.ChannelScopeKey;
import boot.sample.shima.chat.service.AttachmentFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import boot.sample.shima.chat.entity.Channel;
import boot.sample.shima.chat.service.ChannelService;
import boot.sample.shima.chat.service.ChannelService.Channels;

@RestController
public class ChannelController {

    @Autowired
    ChannelService channelService;

    @Autowired
    AttachmentFileService fileService;

    @RequestMapping(value="/channel/make",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> createChannel(@RequestParam String channelName, @RequestParam String userId,
                                             @RequestParam String channelScope, @RequestParam String scopeTarget) {
        Map<String, String> jMap = new HashMap<>();
        if (channelService.existsChannelName(channelName)) {
            jMap.put("result", "exists");
        } else {
            Channel channel = channelService.addChannel(channelName, userId, channelScope);
            // IDが自動生成のためチャンネル情報を取り直す
            Channels channels = channelService.getChannels(channel.getId(), userId);
            // 公開範囲に応じて処理
            if (ChannelScopeKey.USER.getId().equalsIgnoreCase(channelScope)) {
                Arrays.asList(scopeTarget.split(",")).stream()
                        .forEach(target -> {
                            channelService.addChannelInvitation(channel.getId(), channelScope, target);
                        });
            }
            // 参加する
            channelService.joinChannel(channel.getId(), userId);
            jMap.put("result","success");
            jMap.put("id", channel.getId());
            jMap.put("name", channel.getChannelName());
            jMap.put("createUserId", channel.getCreateUserId());
            jMap.put("invalidateFlag", Boolean.toString(channel.isInvalidateFlag()));
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
    public Map<String, List<Channels>> channelListUpdate(@RequestParam String userId) {
        Map<String, List<Channels>> channelListMap = new HashMap<>();
        List<Channels> joiningList = channelService.getJoiningChannels(userId);
        List<Channels> invitationList = channelService.getInvitationChannels(userId, ChannelScopeKey.USER.getId());
        List<Channels> abstentionList = channelService.getAbstentionChannels(userId);
        channelListMap.put("joiningList", joiningList);
        channelListMap.put("invitationList", invitationList);
        channelListMap.put("abstentionList", abstentionList);
        return channelListMap;
    }

    @RequestMapping("/channel/lastlogin")
    public void channelLastLogin(@RequestParam String channelId, @RequestParam String userId) {
        channelService.lastLogin(channelId, userId);
    }

    @RequestMapping("/channel/join")
    public void joinChannel(@RequestParam String channelId, @RequestParam String userId) {
        channelService.joinChannel(channelId, userId);
    }

    @RequestMapping(value="/channel/info",produces=MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> channelInformation(@RequestParam String channelId) {
        Map<String, Object> infoMap = new HashMap<>();
        // create joiner list
        List<ChatUser> joinersList = channelService.getJoiners(channelId);
        List<Map<String, String>> joiners = new ArrayList<Map<String, String>>();
        // ChatUserはパスワードなども持っているため、必要な情報のみ取得する
        joinersList.stream()
                .forEach(joiner -> {
                    Map<String, String> joinersMap = new HashMap<>();
                    joinersMap.put("name", joiner.userName());
                    joinersMap.put("id", joiner.userId());
                    joiners.add(joinersMap);
                });
        infoMap.put("joiners", joiners);

        // create attached file list
        List<AttachmentFile> fileList = fileService.getAttachmentFiles(channelId);
        infoMap.put("attachments", fileList);
        return infoMap;
    }
}
