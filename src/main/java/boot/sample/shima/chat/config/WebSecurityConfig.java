package boot.sample.shima.chat.config;

import boot.sample.shima.chat.repository.AuthorizedUserSecurityRepository;
import org.hibernate.service.spi.InjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import boot.sample.shima.chat.entity.ChatUser;
import boot.sample.shima.chat.service.ChatUserService;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private ChatUserService userService;

    @Autowired
    AuthorizedUserSecurityRepository authorizedUserSecurityRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        // セキュリティ設定を無視するリクエスト
        web.ignoring().antMatchers(
                            "/css/**",
                            "/js/**",
                            "/webjars/**",
                            "/h2-console/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //http.setSharedObject(SecurityContextRepository.class, authorizedUserSecurityRepository);
        http.authorizeRequests()
                .antMatchers("/", "/index", "/create", "/user/exists").permitAll()
                .anyRequest().authenticated()
                .and()
                .csrf()
                .ignoringAntMatchers("/login", "/logout");
        http.formLogin()
                .loginPage("/")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/enter")
                .failureUrl("/")
                .usernameParameter("userId")
                .passwordParameter("password")
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
                .permitAll()
                .and()
                .sessionManagement()
                .maximumSessions(1)
                //.maxSessionsPreventsLogin(true)
                .expiredUrl("/");
    }

    @Autowired
    public void configre(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
            .passwordEncoder(new BCryptPasswordEncoder());
        auth.inMemoryAuthentication()
            .withUser("admin")
            .password("admin")
            .authorities(ChatUser.Authority.ROLE_ADMIN.toString());
    }
}
