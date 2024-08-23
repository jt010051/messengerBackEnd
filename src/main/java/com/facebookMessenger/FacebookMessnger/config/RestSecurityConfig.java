//package com.facebookMessenger.FacebookMessnger.config;
//
//
//
//import static org.springframework.http.HttpMethod.GET;
//import static org.springframework.http.HttpMethod.POST;
//import static org.springframework.http.HttpMethod.DELETE;
//import static org.springframework.http.HttpMethod.PUT;
//
//
//import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
//
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.messaging.Message;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authorization.AuthorizationManager;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//
//
//import lombok.RequiredArgsConstructor;
//
//@Configuration @EnableWebSecurity @RequiredArgsConstructor @EnableMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
//public class RestSecurityConfig  { 
//	AuthenticationConfiguration authentication;
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManager(authentication));
//        customAuthenticationFilter.setFilterProcessesUrl("/user/auth/login/**");
//
//    http.cors(); 
//    http.csrf().disable();
//    http.sessionManagement().sessionCreationPolicy(STATELESS);
//  
//    http.authorizeHttpRequests().requestMatchers(GET, "user/**", "/ws/**").permitAll();
//    http.authorizeHttpRequests().requestMatchers(GET, "/user/auth/token/refresh").hasAnyAuthority("ROLE_ADMIN");
//    http.authorizeHttpRequests().requestMatchers(GET, "/user/auth/token/refresh").hasAnyAuthority("ROLE_USER");
//
//    http.authorizeHttpRequests().requestMatchers(GET, "forgot/**").permitAll();
//
//    http.authorizeHttpRequests().requestMatchers(POST, "user/**").permitAll();
//    http.authorizeHttpRequests().requestMatchers(PUT, "user/updatePassword").permitAll();
//
//    http.authorizeHttpRequests().requestMatchers(POST, "/user/auth/**").permitAll();
//    http.authorizeHttpRequests().requestMatchers(DELETE, "user/delete/{email}").hasAnyAuthority("ROLE_ADMIN");
//
//    http.authorizeHttpRequests().requestMatchers(POST, "/role/addtouser").hasAnyAuthority("ROLE_ADMIN");
//    http.authorizeHttpRequests().anyRequest().authenticated();
//    http.addFilter(customAuthenticationFilter);
//
//    http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//	@Bean
//	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)  throws Exception{
//		return authenticationConfiguration.getAuthenticationManager();
//	}
//	
//    AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
//        messages
//        
//                .simpDestMatchers("/ws").permitAll().anyMessage().authenticated() //or permitAll
//                .simpDestMatchers("/**").authenticated();
//
//        return messages.build();
//    }
//}