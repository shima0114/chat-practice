package boot.sample.shima.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import boot.sample.shima.chat.service.ChannelService;
import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.service.ChatUserService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @Autowired
    private ChatUserService userService;

    @RequestMapping("/user/exists")
    public boolean isUserExists(@RequestParam String userId) {
        return userService.isUserExists(userId);
    }
}
