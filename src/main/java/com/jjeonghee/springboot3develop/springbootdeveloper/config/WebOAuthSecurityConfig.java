package com.jjeonghee.springboot3develop.springbootdeveloper.config;

import com.jjeonghee.springboot3develop.springbootdeveloper.config.jwt.TokenProvider;
import com.jjeonghee.springboot3develop.springbootdeveloper.config.oauth.OAuth2SuccessHandler;
import com.jjeonghee.springboot3develop.springbootdeveloper.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.jjeonghee.springboot3develop.springbootdeveloper.repository.RefreshTokenRepository;
import com.jjeonghee.springboot3develop.springbootdeveloper.service.Oauth2UserCustomService;
import com.jjeonghee.springboot3develop.springbootdeveloper.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final Oauth2UserCustomService oauth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        toH2Console(),
                        AntPathRequestMatcher.antMatcher("/img/**"),
                        AntPathRequestMatcher.antMatcher("/css/**"),
                        AntPathRequestMatcher.antMatcher("/js/**")
                );
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
        http.csrf(csrf->csrf.disable())
                .httpBasic(httpBasic-> httpBasic.disable())
                .formLogin(login->
                        login.disable())
                .logout(logout -> logout.disable());

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(
                requests ->
                        requests.requestMatchers(
                                mvcMatcherBuilder.pattern("/api/token")
                        ).permitAll()
                        .requestMatchers(
                                mvcMatcherBuilder.pattern("/api/**")
                        ).authenticated().anyRequest().permitAll()

        );

        http.oauth2Login(
          oauth2 ->
          oauth2.loginPage("/login")
                  .authorizationEndpoint()
                  //boot 3.1.0 버전 이상부터는 authorizationEndpoint() 가 deprecated 되어 실행시 문제 있음
//                  .authorizationEndpoint(
//                          authorization ->
//                                  authorization.baseUri("https://accounts.google.com/o/oauth2/v2/auth")
//                  ).redirectionEndpoint(
//                          redirectionEndpointConfig -> redirectionEndpointConfig.baseUri(OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI)
//                  )
                  .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())
                  .and()
                  .successHandler(oAuth2SuccessHandler())
                  .userInfoEndpoint()
                  .userService(oauth2UserCustomService)
        );

        http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer.logoutSuccessUrl("/login"));

        http.exceptionHandling(
                httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.defaultAuthenticationEntryPointFor(
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), new AntPathRequestMatcher("/api/**"))
        );

        return http.build();
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(
                tokenProvider,
                refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                userService
                );
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }


    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
