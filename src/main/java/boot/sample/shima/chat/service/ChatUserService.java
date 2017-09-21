package boot.sample.shima.chat.service;

import java.util.List;
import java.util.Optional;

import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.repository.ChatUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import boot.sample.shima.chat.entity.ChatUser.Authority;

public class ChatUserService implements UserDetailsService {

    @Autowired
    ChatUserRepository repo;

    public ChatUser create(String userId, String userName, String password) {
        ChatUser user = new ChatUser();
        user.setUserId(userId);
        user.setUserName(userName);
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        user.setAuthority(Authority.ROLE_USER);
        user.setEnabled(true);
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

    public boolean isUserExists(String userId) {
        ChatUser user = repo.findByUserId(userId);
        if (user != null && user.isEnabled()) {
            return true;
        }
        return false;
    }

    public List<ChatUser> loadAllUser() {
        return repo.findAll();
    }

    public List<ChatUser> loadAllOther(String userId) {
        return repo.findAllByUserIdNot(userId);
    }

    public ChatUser loadUserByUserId(String userId) {
        return repo.findByUserId(userId);
    }

    public ChatUser updateUser(String userId, String userName, String password) {
        // id, passwordが一致したら更新
        ChatUser user = repo.findByUserId(userId);
        if (new BCryptPasswordEncoder().matches(password, user.getPassword())) {
            user.setUserName(userName);
            return repo.save(user);
        }
        return null;
    }

    public ChatUser updateUser(String userId, String userName, String oldPassword, String newPassword) {
        // id, passwordが一致したら更新
        ChatUser user = repo.findByUserId(userId);
        if (new BCryptPasswordEncoder().matches(oldPassword, user.getPassword())) {
            user.setUserName(userName);
            user.setPassword(new BCryptPasswordEncoder().encode(newPassword));
            return repo.save(user);
        }
        return null;
    }

    public ChatUser save(ChatUser user) {
        return repo.save(user);
    }

}
