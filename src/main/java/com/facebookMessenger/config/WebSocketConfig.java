package com.facebookMessenger.config;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facebookMessenger.domain.Roles;
import com.facebookMessenger.domain.Status;
import com.facebookMessenger.domain.User;
import com.facebookMessenger.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSocketMessageBroker
@EnableWebSocketSecurity
@RequiredArgsConstructor
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 100)



public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{



@Bean(name = "csrfChannelInterceptor")
ChannelInterceptor csrfChannelInterceptor() {
    return new ChannelInterceptor() {
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {
            return message;
        }
    };
}
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {

	registry.enableSimpleBroker("/user");

	registry.setApplicationDestinationPrefixes("/app");
	registry.setUserDestinationPrefix("/user");
	
}



@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {

//	registry.addEndpoint("/ws")
//		.setAllowedOrigins("http://localhost:3000", "http://localhost:1993")
//		.setAllowedOriginPatterns("*")
//		.withSockJS();

	registry.addEndpoint("/ws")
	.setAllowedOrigins("http://localhost:3000", "http://localhost:1993")
	.setAllowedOriginPatterns("*");
}

@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();

resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
converter.setObjectMapper(new ObjectMapper());
converter.setContentTypeResolver(resolver);
messageConverters.add(converter);
return false;
}
@Bean
AuthorizationManager<Message<?>> messageAuthorizationManager(MessageMatcherDelegatingAuthorizationManager.Builder messages) {
  messages
  
//          .simpDestMatchers("/ws/**").permitAll()
//          .simpDestMatchers("/chat/**").permitAll() 
//          .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.CONNECT,
//        		  SimpMessageType.HEARTBEAT, SimpMessageType.OTHER  ).permitAll()
//          .simpDestMatchers("/user/**").permitAll()
//          .simpDestMatchers("/app/**").permitAll()
          .anyMessage().permitAll(); 
  	
  return messages.build();
}

//@Override
//public void configureClientInboundChannel(ChannelRegistration registration)  {
//    registration.interceptors(new ChannelInterceptor() {
//        @Override
//        public Message<?> preSend(Message<?> message, MessageChannel channel) {
//            StompHeaderAccessor accessor =
//                    MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
//            log.info("Headers: {}", accessor);
//
//            assert accessor != null;
//            if (StompCommand.CONNECT.equals(accessor.getCommand())) {
//
//                String authorizationHeader = accessor.getFirstNativeHeader("Authorization");
//                assert authorizationHeader != null;
//                String token = authorizationHeader.substring(7);
//
//  
//          
//	                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
//	                JWTVerifier verifier = JWT.require(algorithm).build();
//	                DecodedJWT decodedJWT = verifier.verify(token);
//	                String username = decodedJWT.getSubject();
//	               
//	                
//	           	     UserDetails userDetails = service.loadUserByUsername(username);
//	                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//	                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//
//	                accessor.setUser(usernamePasswordAuthenticationToken);
//	         
//	          
//            }
//
//            return message;
//        }
//
//    });
//
//}
protected boolean sameOriginDisabled() {
    return true;
}
}
