package jp.co.commerce.sample.shima.chat.user;

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

    protected ChatUser() {}

    public ChatUser(String userId, String userName, String password, Authority auth) {
        this.userId = userId;
        this.userName = userName;
        this.password = password;
        this.authority = auth;
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
      return true;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }
}
