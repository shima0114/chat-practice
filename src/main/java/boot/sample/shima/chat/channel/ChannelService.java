package boot.sample.shima.chat.channel;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import lombok.Getter;
import lombok.Setter;

public class ChannelService {

    @Autowired
    private ChannelRepository repo;

    @Autowired
    private ChannelStateRepository stateRepo;

    public Channel addChannel(Channel channel) {
        return repo.save(channel);
    }

    public Channel addChannel(String channelName, String createUserId) {
        Channel channel = new Channel();
        channel.setChannelName(channelName);
        channel.setCreateUserId(createUserId);
        return addChannel(channel);
    }

    public List<Channel> getAllChannel() {
        return repo.findByInvalidateFlagFalse();
    }

    public Channels getChannels(String channelId, String userId) {
        Channel channel = repo.findById(channelId);
        return new Channels(channel, getState(channelId, userId), getCondition(channel));
    }

    public List<Channels> getAvailableChannels(String createUserId) {
        List<Channel> channelList = repo.findByInvalidateFlagFalseOrCreateUserIdOrderById(createUserId);
        List<Channels> channels = new ArrayList<>();
        channelList.stream()
            .forEach(channel -> {
                String state = getCondition(channel);
                channels.add(new Channels(channel, getState(channel.getId(), createUserId), state));
            });

        return channels;
    }

    private String getCondition(Channel channel) {
        String state = "";
        if (channel.isInvalidateFlag()) {
            state = "閉鎖中";
        } else {
            int entryNum = stateRepo.countByChannelId(channel.getId());
            state = "参加人数 :: " + entryNum + "人";
        }
        return state;
    }

    private String getState(String channelId, String userId) {
        ChannelState state = stateRepo.findByChannelIdAndEntryUserId(channelId, userId);
        if (state != null) {
            return "JOINING";
        }
        return "NOT_YET";
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
    }

    public void withdrawalChannel(String channelId, String userId) {
        ChannelState state = stateRepo.findByChannelIdAndEntryUserId(channelId, userId);
        if (state != null) {
            stateRepo.delete(state);
        }
    }

    private void leaveAll(String channelId) {
        List<ChannelState> states = stateRepo.findAllByChannelId(channelId);
        states.stream().forEach(state -> {
            stateRepo.delete(state);
        });
    }

    @Setter
    @Getter
    public class Channels {
        private Channel channel;
        private String state;
        private String condition;

        public Channels(Channel channel, String state, String condition) {
            this.channel = channel;
            this.state = state;
            this.condition = condition;
        }
    }
}
