package com.facebookMessenger.rest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facebookMessenger.HashMaptoJson;
import com.facebookMessenger.domain.ChatMessage;
import com.facebookMessenger.domain.ChatNotification;
import com.facebookMessenger.domain.Roles;
import com.facebookMessenger.domain.Status;
import com.facebookMessenger.domain.User;
import com.facebookMessenger.repository.UserRepository;
import com.facebookMessenger.service.UserService;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("chatUsers")
@Slf4j 
@RequiredArgsConstructor
public class UserController {
	private long expirationTime = 1800000;
	private final UserService service;
    private final SimpMessagingTemplate messagingTemplate;

@JsonProperty()
	@PostMapping("/save")
	public User saveUser(@RequestBody User user) {
		return service.saveUser(user);
	}
	
	@DeleteMapping("delete/{user}")
	public void delete( @PathVariable("user") String user) {
		log.info("Delete user {}", user);
		User thisUser = service.getUser(user);
		service.removeRole(user);
		service.delete(thisUser);
	}
	
	@GetMapping("forgot/{user}")
	public User forgotPassword( @PathVariable("user") String user) {
		log.info("User {} forgot password", user);
		User thisUser = service.getUser(user);
		if (user == null) return null;
		return thisUser;	
	}
	@GetMapping("{user}/myRequests")
	public Map<String, String> getMyRequests(@PathVariable("user") String user){
		User thisUser = getUser(user);
		
		return service.myFriendRequests(thisUser);
		
	}
    
    @PutMapping("/updatePassword")
    public User updatePassword( @RequestParam String user, @RequestParam String password) {
    	User thisUser = service.getUser(user);
    	return service.updatePassword(password, thisUser);
    }
    
	@GetMapping("/allUsers/")
	public List<User> allUsers() {
		return service.allUsers();
	}
//	@PutMapping("/friendRequests/{user}/{other}")
//	public void friendRequests(@PathVariable String user,
//			@PathVariable String other) {
//		User thisUser = getUser(user);
//		User otherUser = getUser(other);
//
//	    service.newFriendRequest(thisUser, otherUser);
//	     
//
//	}
	@MessageMapping("/request/")
	public void processMessage(@Payload User user) {
        String requested = "";
        for (Map.Entry<String, String> entry : user.getRequests().entrySet()) {
        	requested = entry.getKey();
        }
        
        
        
		User other = getUser(requested);
		user.setRequests(service.newFriendRequest(user, other)); 
	         
	        messagingTemplate.convertAndSendToUser(
	        		other.getEmail(), "/queue/request", user.getRequests()
	            
	        );
	}

	@PutMapping("/addFriend/{thisUser}/{userToAdd}") 
	public void addFriend(@PathVariable("thisUser") String user, 
						  @PathVariable("userToAdd") String userToAdd) {
		
		 service.addFriend(user, userToAdd);
		 
	}
	@GetMapping("/currentUser/{user}")
	public User getThisUser(@PathVariable ("user") String user) {
		return service.getUser(user);
	}
	@GetMapping("/{user}")
	public User getUser(@PathVariable ("user") String user) {
		return service.getUser(user);
	}
	
	
	@PostMapping("/role/addtouser")
	public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form) {
	    service.addRoleToUser(form.getEmail());	  
	    return ResponseEntity.ok().build();
	}
	
	@GetMapping("auth/token/refresh")
	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
	    	String authorizationHeader = request.getHeader(AUTHORIZATION);
	        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	            try {
	                String refresh_token = authorizationHeader.substring("Bearer ".length());
	                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
	                JWTVerifier verifier = JWT.require(algorithm).build();
	                DecodedJWT decodedJWT = verifier.verify(refresh_token);
	                String username = decodedJWT.getSubject();
	                User user = service.getUser(username);
	          
	                String access_token = JWT.create()
	                        .withSubject(user.getEmail())
	                        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
	                        .withIssuer(request.getRequestURL().toString())
	                        .withClaim("roles", user.getRole().stream().map(Roles::getName).collect(Collectors.toList()))

	                        .sign(algorithm);
	                Map<String, String> tokens = new HashMap<>();	               
	                tokens.put("access_token", access_token);
	                tokens.put("refresh_token", refresh_token);
	                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
	                new ObjectMapper().writeValue(response.getOutputStream(), tokens);					
	            }
	            catch (Exception exception) {
	                response.setHeader("error", exception.getMessage());
	                response.setStatus(FORBIDDEN.value());
	                //response.sendError(FORBIDDEN.value());
	                Map<String, String> error = new HashMap<>();
	                error.put("error_message", exception.getMessage());
	                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
	                new ObjectMapper().writeValue(response.getOutputStream(), error);
	            }
	        } 
	        else {
	            throw new RuntimeException("Refresh token is missing");
	        }
	    }
	 
	 @MessageMapping("/user.addUser")
	 @SendTo("/user")
	 public User connect(@Payload User user) {
		 	user = getUser(user.getEmail());
	 	service.connect(user);
	 	return user;
	 }
	 @MessageMapping("/user.disconnectUser")
	 @SendTo("/user")
	 public User disconnect(@Payload User user) {
	 	service.disconnect(user);
	 	return user;
	 }
	 @GetMapping("/usersOnline/{currentUser}")
	 public List<User> findConnectedUsers(@PathVariable("currentUser") String user){
		 
	 return service.findConnectedUser(service.getUser(user));
	 }
	 @GetMapping("/myFriends/{user}")
	 public List<String>  findMyFriends(@PathVariable("user") String user){
		 
		 
	 return service.myFriends(user);
	 } 
	 
	 
	 
	 
}
@Data
class RoleToUserForm {
    private String email;
    private String phone;
    
}
