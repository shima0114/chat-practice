package boot.sample.shima.chat.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boot.sample.shima.chat.entity.ChannelInvitation;
import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.entity.key.ChannelScopeKey;
import boot.sample.shima.chat.repository.ChannelInvitationRepository;
import boot.sample.shima.chat.repository.ChannelRepository;
import boot.sample.shima.chat.repository.ChannelStateRepository;
import boot.sample.shima.chat.entity.Channel;
import boot.sample.shima.chat.entity.ChannelState;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

public class ChannelService {

    @Autowired
    private ChannelRepository repo;

    @Autowired
    private ChannelStateRepository stateRepo;

    @Autowired
    private ChannelInvitationRepository invitationRepository;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ChatUserService userService;

    public Channel addChannel(Channel channel) {
        return repo.save(channel);
    }

    public Channel addChannel(String channelName, String createUserId, String channelScope) {
        Channel channel = new Channel();
        channel.setChannelName(channelName);
        channel.setCreateUserId(createUserId);
        channel.setChannelScope(channelScope);
        return addChannel(channel);
    }

    public void addChannelInvitation(String channelId, String scope, String scopeTarget) {
        ChannelInvitation invitation = new ChannelInvitation();
        invitation.setChannelId(channelId);
        invitation.setScope(scope);
        invitation.setTargetId(scopeTarget);
        invitationRepository.save(invitation);
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
            LocalDateTime ldt = state.getLastLogin() == null ? state.getJoiningDateTime() : state.getLastLogin();
            Channels channels = new Channels(channel, getUnread(channel.getId(), ldt));
            channelsList.add(channels);
        });
        return channelsList;
    }

    public List<Channels> getInvitationChannels(String userId, String scope) {
        List<Channels> channelsList = new ArrayList<>();
        List<ChannelInvitation> invList = invitationRepository.findAllByScopeAndTargetId(scope, userId);
        invList.stream().forEach(invitation -> {
            Channel channel = repo.findById(invitation.getChannelId());
            channelsList.add(new Channels(channel, 0));
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

    public boolean existsChannelName(String channelName) {
        int cnt = repo.countChannelNameByChannelNameAndInvalidateFlagFalse(channelName);
        return cnt > 0;
    }

    public void joinChannel(String channelId, String userId) {
        ChannelState state = stateRepo.findByChannelIdAndEntryUserId(channelId, userId);
        if (state == null) {
            state = new ChannelState(userId, channelId);
        }
        stateRepo.save(state);
        invitationRepository.deleteByChannelIdAndTargetId(channelId, userId);
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

    public List<ChatUser> getJoiners(String channelId) {
        List<ChatUser> userList = new ArrayList<>();
        List<ChannelState> states = stateRepo.findAllByChannelId(channelId);
        states.stream().forEach(state -> {
            userList.add(userService.loadUserByUserId(state.getEntryUserId()));
        });
        return userList;
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
