package com.facebookMessenger.FacebookMessnger.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import com.facebookMessenger.FacebookMessnger.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

//private final CustomAuthorizationFilter jwtTokenUtil;
//private final UserService service;
@Override
public void configureMessageBroker(MessageBrokerRegistry registry) {
	registry.enableSimpleBroker("/user");
	registry.setApplicationDestinationPrefixes("/app");
	registry.setUserDestinationPrefix("/user");
}



@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {

	registry.addEndpoint("/ws")
		.setAllowedOrigins("http://localhost:3000", "http://localhost:1993")
		.setAllowedOriginPatterns("*")
		.withSockJS();


}
@Override
public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
	DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
	MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

	resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
	converter.setObjectMapper(new ObjectMapper());
	converter.setContentTypeResolver(resolver);
	messageConverters.add(converter);
	
	return false;
}
//@Override
//	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
//DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
//
//resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
//MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
//converter.setObjectMapper(new ObjectMapper());
//converter.setContentTypeResolver(resolver);
//messageConverters.add(converter);
//return false;
//}

//public void configureClientInboundChannel(ChannelRegistration registration) {
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
//                String authorizationHeader = accessor.getFirstNativeHeader(AUTHORIZATION);
//                assert authorizationHeader != null;
//                String token = authorizationHeader.substring(7);
//
//                String username = jwtTokenUtil.getUsername(token);
//                UserDetails userDetails = service.loadUserByUsername(username);
//                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//
//                accessor.setUser(usernamePasswordAuthenticationToken);
//            }
//
//            return message;
//        }
//
//    });
//
//
//}

}
