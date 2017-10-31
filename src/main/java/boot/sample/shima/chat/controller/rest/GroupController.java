package boot.sample.shima.chat.controller.rest;

import boot.sample.shima.chat.entity.ChatGroup;
import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.service.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class GroupController {

    @Autowired
    ChatGroupService groupService;

    @RequestMapping(value="/group/create")
    public ChatGroup create(Principal principal, @RequestParam String groupName, @RequestParam String password,
                            @RequestParam String groupScope, @RequestParam String scopeTarget, @RequestParam(required=false) String groupId) {
        Authentication auth = (Authentication)principal;
        ChatUser user;
        try {
            user = (ChatUser)auth.getPrincipal();
        } catch (ClassCastException e) {
            user = new ChatUser("admin", "admin", "admin", ChatUser.Authority.ROLE_ADMIN, true, null);
        }
        return groupService.create(user, groupId, groupName, password, groupScope, scopeTarget);
    }

    @RequestMapping("/group/join")
    public Map<String, String> join(@RequestParam String groupId, @RequestParam String userId, @RequestParam(required = false) String password) {
        Map<String, String> result = new HashMap<>();

        if (groupService.joinGroup(groupId, userId, password)) {
            result.put("result", "authorize");
        } else {
            result.put("result", "reject");
        }
        return result;
    }

    @RequestMapping("/group/withdrawal")
    public Map<String, String> withdrawal(@RequestParam String groupId, @RequestParam String userId, @RequestParam String scope) {
        Map<String, String> result = new HashMap<>();
        groupService.withdrawalGroup(groupId, userId, scope);
        return result;
    }

    @RequestMapping("/group/needAuth")
    public Map<String, Boolean> needAuthorized(@RequestParam String groupId) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("needAuthorized", groupService.needAuthorized(groupId));
        return result;
    }

    @RequestMapping(value="/group/listUpdate")
    public Map<String, List<Map<String, String>>> listUpdate(@RequestParam String userId) {
        return groupService.userGroups(userId);
    }
}
