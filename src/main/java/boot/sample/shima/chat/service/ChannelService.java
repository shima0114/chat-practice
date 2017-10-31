package boot.sample.shima.chat.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import boot.sample.shima.chat.entity.*;
import boot.sample.shima.chat.entity.key.ChannelScopeKey;
import boot.sample.shima.chat.repository.*;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

public class ChannelService {

    @Autowired
    private ChannelRepository repo;

    @Autowired
    private ChannelStateRepository stateRepo;

    @Autowired
    private ChannelInvitationRepository invitationRepo;

    @Autowired
    private ChannelGroupRepository channelGroupRepository;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ChatUserService userService;

    @Autowired
    private ChatGroupService groupService;

    public Channel updateOrInsertChannel(String channelId, String channelName, String createUserId, String channelScope, String scopeTarget) {
        Channel channel = new Channel();
        if (StringUtils.isEmpty(channelId)) {
            channel.setChannelName(channelName);
            channel.setCreateUserId(createUserId);
            channel.setChannelScope(channelScope);
            channel = repo.save(channel);
        } else {
            channel = repo.findById(channelId);
        }
        final String targetChannelId = channel.getId();

        // 公開範囲に応じて処理
        if (ChannelScopeKey.GROUP.getId().equalsIgnoreCase(channelScope)) {
            // グループ情報を登録
            Arrays.asList(scopeTarget.split(",")).stream()
                    .forEach(target -> {
                        ChannelGroup cGroup = new ChannelGroup();
                        cGroup.setChannelId(targetChannelId);
                        cGroup.setGroupId(target);
                        channelGroupRepository.save(cGroup);
                    });
        } else if (ChannelScopeKey.USER.getId().equalsIgnoreCase(channelScope)) {
            // 個別ユーザー登録
            Arrays.asList(scopeTarget.split(",")).stream()
                    .forEach(userId -> {
                        addChannelInvitation(targetChannelId, userId);
                    });
        }
        // 作成者本人はその場で参加
        joinChannel(channel.getId(), createUserId);
        return channel;
    }

    private void addChannelInvitation(String channelId, String scopeTarget) {
        ChannelInvitation invitation = new ChannelInvitation();
        invitation.setChannelId(channelId);
        invitation.setTargetId(scopeTarget);
        invitationRepo.save(invitation);
    }

    public List<Channel> getAllChannel() {
        return repo.findByInvalidateFlagFalse();
    }

    public List<Channel> getAllScopeChannel() {
        return repo.findAllByChannelScope(ChannelScopeKey.ALL.getId());
    }

    public Channels getChannels(String channelId, String userId) {
        Channel channel = repo.findById(channelId);
        return new Channels(channel, getUnread(channelId, LocalDateTime.now()));
    }

    public List<Channels> getJoiningChannels(String userId) {
        List<Channels> channelsList = new ArrayList<>();
        List<ChannelState> states = stateRepo.findAllByEntryUserIdOrderByJoiningDateTime(userId);
        states.stream().forEach(state -> {
            Channel channel = repo.findById(state.getChannelId());
            Channels channels = new Channels(channel, getUnread(channel.getId(), state.lastLogin()));
            channelsList.add(channels);
        });
        return channelsList;
    }

    public List<Channels> getInvitationChannelsWithoutJoining(String userId, List<Channels> joiningList) {
        List<Channels> channelsList = new ArrayList<>();
        List<String> channelIdList = new ArrayList<>();
        List<ChannelInvitation> invList = invitationRepo.findAllByTargetId(userId);
        invList.stream().forEach(invitation -> {
            channelIdList.add(invitation.getChannelId());
        });
        ChatUser user = userService.loadUserByUserId(userId);

        groupService.getJoiningGroups(userId).stream()
                .map(ChatGroup::getId)
                .forEach(groupId -> {
                    channelGroupRepository.findAllByGroupId(groupId)
                            .stream()
                            .map(ChannelGroup::getChannelId)
                            .forEach(channelId -> {
                                channelIdList.add(channelId);
                            });
                });
        List<String> joiningIdList = joiningList.stream().map(Channels::getChannel).map(Channel::getId).collect(Collectors.toList());
        channelIdList.stream()
                .sorted()
                .distinct()
                .filter(channelId -> !joiningIdList.contains(channelId))
                .forEach(channelId -> {
                    channelsList.add(new Channels(repo.findById(channelId), 0));
                });
        return channelsList;
    }

    public List<Channels> getInvitationChannels(String userId) {
        List<Channels> channelsList = new ArrayList<>();
        List<String> channelIdList = new ArrayList<>();
        List<ChannelInvitation> invList = invitationRepo.findAllByTargetId(userId);
        invList.stream().forEach(invitation -> {
            channelIdList.add(invitation.getChannelId());
        });
        ChatUser user = userService.loadUserByUserId(userId);

        groupService.getJoiningGroups(userId).stream()
                .map(ChatGroup::getId)
                .forEach(groupId -> {
                    channelGroupRepository.findAllByGroupId(groupId)
                            .stream()
                            .map(ChannelGroup::getChannelId)
                            .forEach(channelId -> {
                                channelIdList.add(channelId);
                            });
            });
        channelIdList.stream()
                .sorted()
                .distinct()
                .forEach(channelId -> {
                    channelsList.add(new Channels(repo.findById(channelId), 0));
                });
        return channelsList;
    }

    public List<Channels> getAbstentionChannels(String userId) {
        List<Channels> channelsList = new ArrayList<>();
        List<ChannelState> states = stateRepo.findAllByEntryUserIdOrderByJoiningDateTime(userId);

        List<Channel> allChannel = getAllScopeChannel();
        List<String> joiningIdList = states.stream().map(ChannelState::getChannelId).collect(Collectors.toList());
        allChannel.stream()
                .filter(channel -> !joiningIdList.contains(channel.getId()))
                .forEach(channel -> {
            Channels channels = new Channels(channel, 0);
            channelsList.add(channels);
        });
        return channelsList;
    }

    private int getUnread(String channelId, LocalDateTime ldt) {
        return historyService.getUnreadCount(channelId, ldt);
    }

    public List<Channel> getClosedChannelId() {
        return repo.findByInvalidateFlagTrue();
    }

    public Channel getChannelFromId(String id) {
        Channel channel = repo.findByIdAndInvalidateFlagFalse(id);
        return channel;
    }

    public Channel getChannelFromName(String channelName) {
        Channel channel = repo.findByChannelNameAndInvalidateFlagFalse(channelName);
        if (channel != null) {
            return channel;
        } else {
            return new Channel();
        }
    }

    public boolean existsChannel(String channelId) {
        if (getChannelFromId(channelId) == null) {
            return false;
        }
        return true;
    }

    public void closeChannel(String id) {
        changeInvalidateFlag(id, true);
        leaveAll(id);
    }

    public void restoreChannel(String id) {
        changeInvalidateFlag(id, false);
    }

    private void changeInvalidateFlag(String id, boolean flag) {
        Channel room = repo.findById(id);
        room.setInvalidateFlag(flag);
         repo.save(room);
    }

    public void deleteChannel(String id) {
        repo.delete(id);
    }

    public boolean existsChannelName(String channelId, String channelName) {
        if (StringUtils.isEmpty(channelId)) {
            // 新規の場合は同名を検索し、存在する場合はtrueを返す
            return repo.countChannelNameByChannelNameAndInvalidateFlagFalse(channelName) > 0;
        } else {
            // 更新の場合、現在の名称から変更がある場合は同名を検索し、存在する場合はtrueを返す
            Channel channel = repo.findById(channelId);
            if (!channel.getChannelName().equals(channelName)) {
                return repo.countChannelNameByChannelNameAndInvalidateFlagFalse(channelName) > 0;
            }
        }
        return false;
    }

    public void updateChannel() {

    }

    public void joinChannel(String channelId, String userId) {
        ChannelState state = stateRepo.findByChannelIdAndEntryUserId(channelId, userId);
        if (state == null) {
            state = new ChannelState(userId, channelId);
        }
        stateRepo.save(state);
        invitationRepo.deleteByChannelIdAndTargetId(channelId, userId);
    }

    public void withdrawalChannel(String channelId, String userId) {
        ChannelState state = stateRepo.findByChannelIdAndEntryUserId(channelId, userId);
        if (state != null) {
            stateRepo.delete(state);
        }
    }

    public void lastLogin(String channelId, String userId) {
        ChannelState state = stateRepo.findByChannelIdAndEntryUserId(channelId, userId);
        state.setLastLogin(LocalDateTime.now());
        stateRepo.save(state);
    }

    private void leaveAll(String channelId) {
        List<ChannelState> states = stateRepo.findAllByChannelId(channelId);
        states.stream().forEach(state -> {
            stateRepo.delete(state);
        });
    }

    public List<Map<String, String>> getJoiners(String channelId) {
        List<Map<String, String>> userList = new ArrayList<>();
        List<ChannelState> states = stateRepo.findAllByChannelId(channelId);
        states.stream().forEach(state -> {
            ChatUser user = userService.loadUserByUserId(state.getEntryUserId());
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", user.userName());
            userMap.put("id", user.userId());
            userMap.put("image", user.userImageBase64());
            userMap.put("lastLogin", state.lastLogin().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));
            userList.add(userMap);
        });
        return userList;
    }

    public List<Map<String, String>> getScopeTarget(String channelId) {
        List<Map<String, String>> targetList = new ArrayList<>();
        Channel channel = repo.findById(channelId);
        if (channel.getChannelScope().equals(ChannelScopeKey.USER.getId())) {
            stateRepo.findAllByChannelId(channelId)
                    .stream()
                    .map(ChannelState::getEntryUserId)
                    .forEach(userId -> {
                        Map<String, String> targetMap = new HashMap<>();
                        targetMap.put("targetId", userId);
                        targetMap.put("disabled", "true");
                        targetList.add(targetMap);
                    });
            invitationRepo.findAllByChannelId(channelId)
                    .stream()
                    .map(ChannelInvitation::getTargetId)
                    .forEach(userId -> {
                        Map<String, String> targetMap = new HashMap<>();
                        targetMap.put("targetId", userId);
                        targetMap.put("disabled", "false");
                        targetList.add(targetMap);
                    });
        } else if (channel.getChannelScope().equals(ChannelScopeKey.GROUP.getId())) {
            channelGroupRepository.findAllByChannelId(channelId)
                    .stream()
                    .map(ChannelGroup::getGroupId)
                    .forEach(groupId -> {
                        Map<String, String> targetMap = new HashMap<>();
                        targetMap.put("targetId", groupId);
                        targetMap.put("disabled", "false");
                        targetList.add(targetMap);
                    });
        }
        return targetList;
    }

    @Setter
    @Getter
    public class Channels {
        private Channel channel;
        private int unread;

        public Channels(Channel channel, int unread) {
            this.channel = channel;
            this.unread = unread;
        }
    }
}
