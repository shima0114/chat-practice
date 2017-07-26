package boot.sample.shima.chat.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import boot.sample.shima.chat.user.ChatUser.Authority;

public class ChatUserService implements UserDetailsService {

    @Autowired
    ChatUserRepository repo;

    public ChatUser create(String userId, String userName, String password) {
        ChatUser user = new ChatUser();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setAuthority(Authority.ROLE_USER);
        repo.save(user);
        return user;
    }

    @Override
    public ChatUser loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isEmpty()) {
            throw new UsernameNotFoundException("Username is empty");
        }
        ChatUser user = Optional.ofNullable(repo.findByUserId(username))
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with userId=%s was not found", username)));
        return user;
    }

}
