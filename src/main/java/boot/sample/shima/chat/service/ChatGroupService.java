package boot.sample.shima.chat.service;

import boot.sample.shima.chat.entity.ChatGroup;
import boot.sample.shima.chat.entity.ChatGroupInvitation;
import boot.sample.shima.chat.entity.ChatGroupState;
import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.entity.key.ChatGroupScopeKey;
import boot.sample.shima.chat.repository.ChatGroupInvitationRepository;
import boot.sample.shima.chat.repository.ChatGroupRepository;
import boot.sample.shima.chat.repository.ChatGroupStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

public class ChatGroupService {

    @Autowired
    ChatGroupRepository repo;

    @Autowired
    ChatGroupStateRepository stateRepository;

    @Autowired
    ChatGroupInvitationRepository invitationRepository;

    @Autowired
    ChatUserService userService;

    @Transactional
    public ChatGroup create(ChatUser user, String groupId, String groupName, String joinPassword, String groupScope, String scopeTarget) {

        ChatGroup group = new ChatGroup();
        if (!StringUtils.isEmpty(groupId)) {
            group = repo.findOne(groupId);
        }
        group.setGroupName(groupName);
        if (!StringUtils.isEmpty(joinPassword)) {
            group.setJoinPassword(new BCryptPasswordEncoder().encode(joinPassword));
        } else {
            group.setJoinPassword("");
        }
        group.setScope(groupScope);
        group.setCreateUserId(user.userId());
        group = repo.save(group);
        final String targetGroupId = group.getId();

        // 公開範囲に応じて処理
        if (ChatGroupScopeKey.CLOSED.name().equalsIgnoreCase(groupScope)) {
            List<String> targetList = Arrays.asList(scopeTarget.split(","));
            // 既存の招待情報を削除
            invitationRepository.deleteByGroupId(targetGroupId);
            // 既存の参加情報から対象外となったユーザーを削除
            stateRepository.deleteByGroupIdAndEntryUserIdNotIn(targetGroupId, targetList);
            // 招待情報を再生成
            targetList.stream()
                    .forEach(target -> {
                        addGroupInvitation(targetGroupId, target);
                    });
        }
        join(targetGroupId, user.userId());
        return group;
    }

    public boolean needAuthorized(String groupId) {
        return repo.getOne(groupId).needAuthorized();
    }

    @Transactional
    public boolean joinGroup(String groupId, String userId, String password) {
        if (needAuthorized(groupId)) {
            if (!authorization(groupId, password)) {
                return false;
            }
        }
        join(groupId, userId);
        return true;
    }

    private void join(String groupId, String userId) {
            ChatGroupState state = new ChatGroupState(groupId, userId);
            stateRepository.save(state);
            invitationRepository.deleteByGroupIdAndUserId(groupId, userId);
    }

    public void withdrawalGroup(String groupId, String userId, String scope) {
        ChatGroupState state = stateRepository.findOneByGroupIdAndEntryUserId(groupId, userId);
        stateRepository.delete(state);
        if (scope.equals(ChatGroupScopeKey.CLOSED.name())) {
            addGroupInvitation(groupId, userId);
        }
    }

    private void addGroupInvitation(String groupId, String userId) {
        if (stateRepository.findOneByGroupIdAndEntryUserId(groupId, userId) != null) {
            // 参加済みなのでスキップ
            return;
        }
        ChatGroupInvitation invitation = new ChatGroupInvitation();
        invitation.setGroupId(groupId);
        invitation.setUserId(userId);
        invitationRepository.save(invitation);
    }

    public boolean authorization(String id, String joinPassword) {
        ChatGroup group = repo.getOne(id);
        if (new BCryptPasswordEncoder().matches(joinPassword, group.getJoinPassword())) {
            return true;
        }
        return false;
    }

    public List<ChatUser> getJoiningUsers(String channelId) {
        List<ChatGroupState> stateList = stateRepository.findAllByGroupId(channelId);
        return stateList
                .stream()
                .map(state -> userService.loadUserByUserId(state.getEntryUserId()))
                .collect(Collectors.toList());
    }

    public List<ChatGroup> getJoiningGroups(String userId) {
        List<ChatGroupState> stateList = stateRepository.findAllByEntryUserId(userId);
        return stateList
                .stream()
                .map(state -> repo.getOne(state.getGroupId()))
                .collect(Collectors.toList());
    }

    public List<ChatGroup> getInvitationGroupsWithoutJoinings(String userId, List<ChatGroup> joiningGroups) {
        List<ChatGroupInvitation> invList = invitationRepository.findAllByUserId(userId);
        return invList
                .stream()
                .map(inv -> repo.getOne(inv.getGroupId()))
                .collect(Collectors.toList());
    }

    public List<ChatGroup> getInvitationGroups(String userId) {
        List<ChatGroupInvitation> invList = invitationRepository.findAllByUserId(userId);
        return invList
                .stream()
                .map(inv -> repo.getOne(inv.getGroupId()))
                .collect(Collectors.toList());
    }

    public List<ChatGroup> getAbstentionGroups(String userId) {
        return repo.getOpenGroupsWithoutJoiningGroups(userId);
    }

    public Map<String, List<Map<String, String>>> userGroups(String userId) {
        Map<String, List<Map<String, String>>> result = new HashMap<>();
        List<Map<String, String>> joiningList = new ArrayList<>();
        getJoiningGroups(userId).stream()
                .forEach(group -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", group.getId());
                    map.put("groupName", group.getGroupName());
                    map.put("scope", group.getScope());
                    map.put("createUserId", group.getCreateUserId());
                    map.put("needAuthorized", Boolean.toString(group.needAuthorized()));
                    joiningList.add(map);
                });
        List<Map<String, String>> invitationList = new ArrayList<>();
        getInvitationGroups(userId).stream()
                .forEach(group -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", group.getId());
                    map.put("groupName", group.getGroupName());
                    map.put("scope", group.getScope());
                    map.put("createUserId", group.getCreateUserId());
                    map.put("needAuthorized", Boolean.toString(group.needAuthorized()));
                    invitationList.add(map);
                });
        List<Map<String, String>> abstentionList = new ArrayList<>();
        getAbstentionGroups(userId).stream()
                .forEach(group -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", group.getId());
                    map.put("groupName", group.getGroupName());
                    map.put("scope", group.getScope());
                    map.put("createUserId", group.getCreateUserId());
                    map.put("needAuthorized", Boolean.toString(group.needAuthorized()));
                    abstentionList.add(map);
                });
        result.put("joiningList", joiningList);
        result.put("invitationList", invitationList);
        result.put("abstentionList", abstentionList);
        return result;
    }


    public List<ChatGroup> getOpenGroups() {
        return null;
    }
}
