package com.projeto.projeto_final.spring.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/").permitAll() // nothing
                                .requestMatchers("/index").permitAll() // index.html
                                .requestMatchers("/home").hasRole("USER") // home.html
                                .requestMatchers("/register/**").hasRole("ADMIN") // register.html
                                .requestMatchers("/calendar").hasRole("USER") // events_calendar.html
                                .requestMatchers("/event/{eventid}").hasRole("USER") // register.html
                                .requestMatchers("/chat").hasRole("USER") // chat.html
                                .requestMatchers("/logs").hasRole("ADMIN") // log.html
                                .requestMatchers("/ws/**").hasRole("USER")  // Permitir conexões websocket
                                .requestMatchers("/api/events/all").hasRole("USER") // GET ALL EVENTS
                                .requestMatchers("/api/events/personal").hasRole("USER") // GET PERSONAL EVENTS
                                .requestMatchers("/api/tasks/{taskid}").hasRole("USER") // GET TASK
                                .requestMatchers("/api/get-event-auditlogs/{eventid}").hasRole("USER") // GET CERTAIN EVENT'S LOGS
                                .requestMatchers("/api/users").hasRole("USER") // GET ALL USERS
                                .requestMatchers("/api/messages/{senderId}/{recipientId}").hasRole("USER") //
                                .requestMatchers("/events/add").hasRole("USER") // POST CREATE EVENT
                                .requestMatchers("/events/delete/{eventid}").hasRole("USER") // DELETE EVENT
                                .requestMatchers("/events/update/{eventid}").hasRole("USER") // UPDATE EVENT
                                .requestMatchers("/event/update/{eventid}/editors").hasRole("USER") // ADD EVENT EDITORS
                                .requestMatchers("/event/update/{eventid}/status").hasRole("USER") // UPDATE EVENT STATUS
                                .requestMatchers("/tasks/add").hasRole("USER") // POST CREATE TASK
                                .requestMatchers("/tasks/delete/{taskid}").hasRole("USER") // DELETE TASK
                                .requestMatchers("/tasks/update/{taskid}").hasRole("USER") // UPDATE TASK
                                .requestMatchers("/tasks/up/{taskid}").hasRole("USER") // MOVE TASK UP
                                .requestMatchers("/tasks/down/{taskid}").hasRole("USER") // MOVE TASK DOWN
                                .requestMatchers("/activities/add/{eventid}/{boardid}").hasRole("USER") // POST CREATE ACTIVITY
                                .requestMatchers("/activities/delete/{activityid}").hasRole("USER") // DELETE ACTIVITY
                                .requestMatchers("/activities/update/{activityid}").hasRole("USER") // UPDATE ACTIVITY
                                .requestMatchers("/activities/move/{boardid}/{activityid}").hasRole("USER") // MOVE ACTIVITY
                                .requestMatchers("/subactivities/add/{activityid}").hasRole("USER") // ADD SUBACTIVITY
                                .requestMatchers("/subactivities/delete/{subactivityid}").hasRole("USER") // DELETE SUBACTIVITY
                                .requestMatchers("/subactivities/check/{subactivityid}").hasRole("USER") // UPDATE SUBACTIVITY
                )
                .formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/home") // home.html
                                .permitAll()
                )
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                )
                .csrf(
                        csrf -> csrf
                                .csrfTokenRepository(csrfTokenRepository())
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/ws/**")
                );

        return http.build();
    }

    // Configuração do repositório de tokens CSRF
    private CsrfTokenRepository csrfTokenRepository() {
        HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
        repository.setSessionAttributeName("_csrf");
        return repository;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        String hierarchy = "ROLE_ADMIN > ROLE_USER";
        roleHierarchy.setHierarchy(hierarchy);
        return roleHierarchy;
    }

    @Bean
    static MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setRoleHierarchy(roleHierarchy);
        return expressionHandler;
    }
}