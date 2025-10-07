package com.facebookMessenger.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.security.messaging.context.AuthenticationPrincipalArgumentResolver;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import org.springframework.security.authorization.SpringAuthorizationEventPublisher;

import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration @EnableWebSecurity @RequiredArgsConstructor @EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true)


public class RestSecurityConfig implements WebSocketMessageBrokerConfigurer  { 
	
	AuthenticationConfiguration authentication;
	@Autowired 
	private ApplicationEventPublisher context;
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(authentication));
        customAuthenticationFilter.setFilterProcessesUrl("/user/auth/login/**");

  

    http
    .csrf().disable()
    .cors().and().sessionManagement().sessionCreationPolicy(STATELESS).and()
    .authorizeHttpRequests(
    		registry -> registry
//            .requestMatchers("/ws/**").permitAll()
//            .requestMatchers(GET, "chatUsers/**").permitAll()
//            .requestMatchers(POST, "chatUsers/**").permitAll()
//            .requestMatchers(PUT, "chatUsers/**").permitAll()
//            .requestMatchers(DELETE, "chatUsers/**").authenticated()
//            .requestMatchers("/stomp").permitAll()
//            .requestMatchers(GET, "/user/auth/token/refresh").authenticated()
//            .requestMatchers(GET, "/messages/**").permitAll()
//            .requestMatchers(GET, "forgot/**").permitAll()
//            .requestMatchers(PUT, "user/updatePassword").authenticated()
//            .requestMatchers(DELETE, "user/delete/{email}").authenticated()
//    		.requestMatchers(POST, "/role/addtouser").hasAnyAuthority("ROLE_ADMIN")
    		.anyRequest().permitAll()
    		)
    .headers().frameOptions().disable()
    .httpStrictTransportSecurity().disable().and()
            .addFilter(customAuthenticationFilter)
    .addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
    .exceptionHandling(ex -> {
        ex.authenticationEntryPoint((request, response, authException) -> response.sendError(401, "Unauthorized"));
        ex.accessDeniedHandler((request, response, authException) -> response.sendError(403, "Forbidden"));
});


    
  




        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    	return authenticationConfiguration.getAuthenticationManager();   
    	}



}