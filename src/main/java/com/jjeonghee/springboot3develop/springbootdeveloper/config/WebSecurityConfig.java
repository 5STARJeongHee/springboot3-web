package com.jjeonghee.springboot3develop.springbootdeveloper.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
//@Configuration
public class WebSecurityConfig {
    private final UserDetailsService userDetailsService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        toH2Console(),
                        AntPathRequestMatcher.antMatcher("/static/**")
                )
                ;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        return http
                .cors(
                    cors -> cors.disable())
                .csrf(
                    csrf -> csrf.disable()
                )
                .authorizeHttpRequests(
                        requests -> {
                            requests
                                    .requestMatchers(
                                            mvcMatcherBuilder.pattern("/login"),
                                            mvcMatcherBuilder.pattern("/signup"),
                                            mvcMatcherBuilder.pattern("/user")
                                            ).permitAll().anyRequest().authenticated();
                        }
                )
                .formLogin(
                        login -> login.defaultSuccessUrl("/articles")
                                .loginPage("/login")
                                .permitAll()
                )
                .logout(logout -> logout.logoutSuccessUrl("/login")
                        .invalidateHttpSession(true).permitAll()
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, BCryptPasswordEncoder bCryptPasswordEncoder, UserDetailsService userDetailsService) throws Exception{
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder
                .userDetailsService(userDetailsService)
                .passwordEncoder(bCryptPasswordEncoder);
        return builder.build();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
