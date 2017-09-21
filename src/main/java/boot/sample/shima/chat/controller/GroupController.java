package boot.sample.shima.chat.controller;

import boot.sample.shima.chat.entity.ChatGroup;
import boot.sample.shima.chat.service.ChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class GroupController {

    @Autowired
    ChatGroupService groupService;

    @RequestMapping("/group/create")
    public ChatGroup create(@RequestParam String name, @RequestParam String password) {
        return groupService.create(name, password);
    }

    @RequestMapping("/group/join")
    public Map<String, String> join(@RequestParam String id, @RequestParam String password) {
        Map<String, String> result = new HashMap<>();
        if (groupService.authorization(id, password)) {
            result.put("result", "authorize");
        } else {
            result.put("result", "reject");
        }
        return result;
    }
}
