package boot.sample.shima.chat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import boot.sample.shima.chat.service.ChannelService;
import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.service.ChatUserService;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
public class UserController {
    @Autowired
    private ChatUserService userService;

    @RequestMapping("/user/exists")
    public boolean isUserExists(@RequestParam String userId) {
        return userService.isUserExists(userId);
    }

    @RequestMapping(value = "/user/update/common", method = RequestMethod.POST)
    public String userUpdate(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        Map<String , String> retMap = new HashMap<>();
        String userId = request.getParameter("user_id");
        String userName = request.getParameter("upd_user_name");
        String oldPassword = request.getParameter("org_password");
        String newPassword = request.getParameter("new_password");
        ChatUser user = null;
        if (newPassword != null && !newPassword.isEmpty()) {
            user = userService.updateUser(userId, userName, oldPassword, newPassword);
        } else {
            user = userService.updateUser(userId, userName, oldPassword);
        }

        if (user != null) {
            retMap.put("result", "success");
            retMap.put("message", "ユーザー情報を更新しました。");
            retMap.put("username", user.userName());

//            HttpSession session = request.getSession(false);
//            SecurityContext context = SecurityContextHolder.createEmptyContext();
//            context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
//            session.setAttribute("SPRING_SECURITY_CONTEXT", context);
            updateSession(request, user);
        } else {
            redirectAttributes.addFlashAttribute("errMsg", "現在のパスワードが一致しなかったため更新できませんでした。");
//            request.setAttribute("errMsg", "現在のパスワードが一致しなかったため更新できませんでした。");
            //retMap.put("message", "現在のパスワードが一致しなかったため更新できませんでした。");
        }
        return "redirect:/enter";
    }

    @RequestMapping(value="/user/update/image", method = RequestMethod.POST)
    public String updateUserImage(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        try {
            String userId = request.getParameter("user_id");
            String imageSrc = request.getParameter("imageSrc");
            Map<String, String> result = new HashMap<>();
            ChatUser user = userService.loadUserByUserId(userId);
            user.setUserImageBase64(imageSrc);
            ChatUser updUser = userService.save(user);
            if (updUser == null) {
                request.setAttribute("errMsg", "画像の登録に失敗しました。");
            }
            updateSession(request, updUser);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errMsg", "画像の登録に失敗しました。");
        }
        return "redirect:/enter";
    }

    private void updateSession(HttpServletRequest request, ChatUser user) {
        HttpSession session = request.getSession(false);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        session.setAttribute("SPRING_SECURITY_CONTEXT", context);
    }
}
