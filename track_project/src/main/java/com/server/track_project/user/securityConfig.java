package com.server.track_project.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
public class securityConfig {

    @Autowired
    public userService userService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((auth) ->
                        auth
                                .requestMatchers("/user/signUp").permitAll()
                                .requestMatchers("/user/login").permitAll()
                                .requestMatchers("/user/user_access").hasRole("USER")
                )
                .formLogin((login) ->
                        login
                                .loginPage("/user/login")
                                .defaultSuccessUrl("/user/user_access")
                )
                .logout(logout ->
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/user/logout"))
                                .logoutSuccessUrl("/user/login")
                                .invalidateHttpSession(true)
                                .deleteCookies("JESSIONID", "remember-me")
                )
                .rememberMe((remember) ->
                        remember
                                .rememberMeParameter("remember-me")
                                .tokenValiditySeconds(31536000)
                                .userDetailsService(userService))
                .csrf(csrf ->
                        csrf.disable());

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(authProvider());
        return authenticationManagerBuilder.build();
    }


}

