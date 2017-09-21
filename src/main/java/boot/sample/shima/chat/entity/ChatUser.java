package boot.sample.shima.chat.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
@Entity
public class ChatUser implements UserDetails {

    public enum Authority {ROLE_USER, ROLE_ADMIN};

    @Id
    @Column(name="user_id")
    private String userId;

    @Column(nullable=false, unique=true)
    private String userName;

    @Column(nullable=false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Authority authority;

    @Column(columnDefinition="boolean default true")
    private boolean isEnabled;

    @Column(length=50000)
    private String userImageBase64;

    public ChatUser() {}

    public ChatUser(String userId, String userName, String password, Authority auth, boolean isEnabled, String userImageBase64) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.authority = auth;
        this.isEnabled = isEnabled;
        this.userImageBase64 = userImageBase64;
    }

    public String userName() {
        return userName;
    }

    public String userId() {
        return userId;
    }

    public Authority authority() {
        return authority;
    }

    public String userImageBase64() {
        if (userImageBase64 != null || !StringUtils.isEmpty(userImageBase64)) {
            return userImageBase64;
        }
        // return default
        return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABQCAYAAADvCdDvAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAvZJREFUeNrsm79q21AUh6/iDoEEagKFbBX0AewxWzR2i8Zs8RvUazf3Ddw3cN5A2TKmY6c6D1BQtkKHqBAC3XpPewQmyLWwJZ3flX8fXDwEfB19Oufcv84RQgghhBBCCCEEhyjEH/3u9M3IfyS+jX2LX/w515Z9//HznkLak/Daf0x8m1ZIWIeIWfg293J+UUhzMq7kofo23PIrCpXyiUJ2jwp5w9OGvnIpUYacyiJwGXdaJ5pEoiVBlRLtmQx4KQegAZK1KMNpLcpUPIVsiI4POqRtm1jrE1PWhlSV7zCa2gZJXV8YIdXMOpZR9smUtYaJQZ+Jzvwp5EW6ujCIDssXAT5Ckj3tG1bIeE/7ZoSsSZkjCsFiSCGEQiikPrlx/0sKwRFSoOwoIgm529O+YYVkFAIkRDeLrNLWgkKqmVnIQDqRAreF62fM31x3SxmylTv2Qh4YIeuZdhmRSDKEAZqNx6fnh5PjI3lz33eQqj6i/f8DwAgRKV+9lLjF1CWTwEvfz28KqS/lxkuRBb+zFobXKerRUvijpLqTKMPSJlZjZ+jHSUM52yunUeRs72SHid8ErYAHK2RFzFv375zvpEZ9yTU9zUMQEaSQiqgp74fEK/MKKdh5SBIIIb1NWXoYoUxVyX/mGVJDlkjHRPs0yjrXQp5uMfwtdJQldw6vKWQ3EXKVbebq3ymsI2fugO8col7YudAHF7fURaGTxM8Usnkou3DN3SnchNSaFGmIHAHJGOlELu6460Jn8TcIz2EAIkOK9q1vpwbdH/p2eXJ8lD8+Pd/vvRCtF7f6YCxJEaQMjGWUaerQYWAuJTKUYXGfsC5jqyvTlnvqGaiMv7/N6sr0gVF0XDmgW0sVxM7oMmhkIAM5VZmnLosImQYiw+lqQX8jJLDoMImSriMkDUxGGdG9TVlTFx5pL4Ws7IGHxlBXE3oXIYkLl4RCwAp7H4WMGSFYQmJHKKTBQcl5X2fqhELC4RVHWbVZ8nUhhBBCCCGEENI6fwQYAA8F2YeDNP+QAAAAAElFTkSuQmCC";
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      List<GrantedAuthority> authorities = new ArrayList<>();
      authorities.add(new SimpleGrantedAuthority(authority.toString()));
      return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
      return true;
    }

    @Override
    public boolean isAccountNonLocked() {
      return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return true;
    }

    @Override
    public boolean isEnabled() {
      return isEnabled;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }
}
