package boot.sample.shima.chat.controller;

import java.security.Principal;
import java.util.List;

import boot.sample.shima.chat.entity.key.ChannelScopeKey;
import boot.sample.shima.chat.entity.key.ChatGroupScopeKey;
import boot.sample.shima.chat.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import boot.sample.shima.chat.entity.ChatUser;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class BaseController {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ChatUserService userService;

    @Autowired
    private AttachmentFileService fileService;

    @Autowired
    private ChatGroupService groupService;

    @RequestMapping("/")
    public String showMainPage(Model model) {
        model.addAttribute("prompt_msg", "ログイン情報を入力してください");
        return "index";
    }

    @RequestMapping("/create")
    public String createUser(Model model, HttpServletRequest request, @RequestParam String newUserId, @RequestParam String newUserName,
                             @RequestParam String newPassword1, @RequestParam String newPassword2) {
        ChatUser chatUser = userService.create(newUserId, newUserName, newPassword1);
        model.addAttribute("userId", chatUser.getUserId());
        model.addAttribute("prompt_msg", "パスワードを入力し作成したユーザーでログインしてください。");
        request.getSession().removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        return "index";
    }

    @RequestMapping("/enter")
    public String entry(Principal principal, Model model, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Authentication auth = (Authentication)principal;
        ChatUser user;
        try {
            user = (ChatUser)auth.getPrincipal();
        } catch (ClassCastException e) {
            user = new ChatUser("admin", "admin", "admin", ChatUser.Authority.ROLE_ADMIN, true, null);
        }
        // 全体
        model.addAttribute("user", user);
        // channel
        List<ChannelService.Channels> joiningChannels = channelService.getJoiningChannels(user.getUserId());
        model.addAttribute("channelList", joiningChannels);
        model.addAttribute("invitationList", channelService.getInvitationChannelsWithoutJoining(user.getUserId(), joiningChannels));
        model.addAttribute("abstentionList", channelService.getAbstentionChannels(user.getUserId()));
        // config
        model.addAttribute("groupList", groupService.getJoiningGroups(user.getUserId()));
        model.addAttribute("groupInvitationList", groupService.getInvitationGroups(user.getUserId()));
        model.addAttribute("groupAbstentionList", groupService.getAbstentionGroups(user.getUserId()));
        // modal
        model.addAttribute("userList", userService.loadAllOther(user.getUserId()));
        model.addAttribute("channelScope", ChannelScopeKey.values());
        model.addAttribute("groupScope", ChatGroupScopeKey.values());

        // リダイレクト時のエラー
        if (redirectAttributes.containsAttribute("errMsg")) {
            model.addAttribute("errMsg", redirectAttributes.asMap().get("errMsg").toString());
        }
        return "enter";
    }

    @RequestMapping(value = "/user/update/common", method = RequestMethod.POST)
    public String userUpdate(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            String userId = request.getParameter("user_id");
            String userName = request.getParameter("upd_user_name");
            String oldPassword = request.getParameter("org_password");
            String newPassword = request.getParameter("new_password");
            ChatUser user = userService.updateUser(userId, userName, oldPassword, newPassword);
            if (user != null) {
                updateSession(request, user);
            } else {
                throw new RuntimeException("現在のパスワードが一致しなかったため更新できませんでした。");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/enter";
    }

    @RequestMapping(value="/user/update/image", method = RequestMethod.POST)
    public String updateUserImage(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            String userId = request.getParameter("user_id");
            String imageSrc = request.getParameter("imageSrc");
            ChatUser user = userService.updateUserImage(userId, imageSrc);
            if (user == null) {
                throw new RuntimeException("画像の登録に失敗しました。");
            }
            updateSession(request, user);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/enter";
    }

    private void updateSession(HttpServletRequest request, ChatUser user) {
        HttpSession session = request.getSession(false);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
    }

    @RequestMapping("/admin")
    public String showAdminPage(Model model) {
        return "admin";
    }
}
