package com.facebookMessenger.FacebookMessnger.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Configuration @EnableWebSecurity @RequiredArgsConstructor @EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true)

public class RestSecurityConfig  { 
	
	AuthenticationConfiguration authentication;
	

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(authentication));
        customAuthenticationFilter.setFilterProcessesUrl("/user/auth/login/**");

    http.cors(); 
    http.csrf().disable();
    http.sessionManagement().sessionCreationPolicy(STATELESS);


    http.authorizeHttpRequests().requestMatchers(GET, "/ws/**").authenticated();
    http.authorizeHttpRequests().requestMatchers(GET, "chatUsers/**").permitAll();
    http.authorizeHttpRequests().requestMatchers(POST, "chatUsers/**").permitAll();
    http.authorizeHttpRequests().requestMatchers(PUT, "chatUsers/**").permitAll();

    http.authorizeHttpRequests().requestMatchers(DELETE, "chatUsers/**").authenticated();

    http.authorizeHttpRequests().requestMatchers(GET, "/user/auth/token/refresh").authenticated();
    http.authorizeHttpRequests().requestMatchers(GET, "/messages/**").permitAll();

    http.authorizeHttpRequests().requestMatchers(GET, "forgot/**").permitAll();

    http.authorizeHttpRequests().requestMatchers(PUT, "user/updatePassword").authenticated();

    http.authorizeHttpRequests().requestMatchers(DELETE, "user/delete/{email}").authenticated();

    http.authorizeHttpRequests().requestMatchers(POST, "/role/addtouser").hasAnyAuthority("ROLE_ADMIN");
    http.authorizeHttpRequests().anyRequest().authenticated();
    http.addFilter(customAuthenticationFilter);
    http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
    	return authenticationConfiguration.getAuthenticationManager();   
    	}
  
    
    
    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
        messages
        
                .simpDestMatchers("/ws").permitAll().anyMessage().authenticated() //or permitAll
                .simpDestMatchers("/**").authenticated();

        return messages.build();
    }
}