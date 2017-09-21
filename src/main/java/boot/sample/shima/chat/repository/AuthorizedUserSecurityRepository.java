package boot.sample.shima.chat.repository;

import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.service.ChatUserService;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class AuthorizedUserSecurityRepository implements SecurityContextRepository {

    public static final String AUTHORIZED_USER_ID_KEY = "AUTHORIZED_USER_ID";

    @Autowired
    private ChatUserService userService;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        HttpServletRequest request = requestResponseHolder.getRequest();
        HttpSession httpSession = request.getSession(false);

        String authUserId = readAuthorizedUserIdFromSession(httpSession);
        if (authUserId != null) {
            ChatUser user = userService.loadUserByUserId(authUserId);
            if (user != null) {
                context.setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            }
        }
        return null;
    }

    public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            HttpSession httpSession = request.getSession(false);
            if (httpSession != null) {
                httpSession.removeAttribute(AUTHORIZED_USER_ID_KEY);
            }
        } else if (authentication.getPrincipal() != null && authentication.getPrincipal() instanceof ChatUser) {
            HttpSession httpSession = request.getSession(!response.isCommitted());
            if (httpSession != null) {
                ChatUser authUser = (ChatUser) authentication.getPrincipal();
                httpSession.setAttribute(AUTHORIZED_USER_ID_KEY, authUser != null ? authUser.userId() : null);
            }
        }
    }

    @Override
    public boolean containsContext(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        return session.getAttribute(AUTHORIZED_USER_ID_KEY) != null;
    }

    private String readAuthorizedUserIdFromSession(HttpSession httpSession) {
        if (httpSession == null) {
            return null;
        }

        Object authorizedUserIdFromSession = httpSession.getAttribute(AUTHORIZED_USER_ID_KEY);
        if (authorizedUserIdFromSession == null) {
            return null;
        }

        if (!(authorizedUserIdFromSession instanceof Long)) {
            return null;
        }
        return (String) authorizedUserIdFromSession;
    }
}
