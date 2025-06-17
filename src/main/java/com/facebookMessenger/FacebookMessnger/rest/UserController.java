package com.facebookMessenger.FacebookMessnger.rest;

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
//import org.springframework.security.access.prepost.PreAuthorize;
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
import com.facebookMessenger.FacebookMessnger.domain.Roles;
import com.facebookMessenger.FacebookMessnger.domain.Status;
import com.facebookMessenger.FacebookMessnger.domain.User;
import com.facebookMessenger.FacebookMessnger.repository.UserRepository;
import com.facebookMessenger.FacebookMessnger.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private final UserRepository repo;
	private final UserService service;

	
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
	
    
    @PutMapping("/updatePassword")
    public User updatePassword( @RequestParam String user, @RequestParam String password) {
    	User thisUser = service.getUser(user);
    	return service.updatePassword(password, thisUser);
    }
    
	@GetMapping("/allUsers")
	public List<User> allUsers() {
		return service.allUsers();
	}
	
	@PutMapping("/addFriend/{thisUser}/{userToAdd}")
	public void addFriend(@PathVariable("thisUser") String user, 
						  @PathVariable("userToAdd") String userToAdd) {
		 service.addFriend(user, userToAdd);
	}
	@GetMapping("/currentUser/{user}")
	public User getUser(@PathVariable ("user") String user) {
		return service.getUser(user);
	}
	
	@PostMapping("/role/addtouser")
	public ResponseEntity<?>addRoleToUser(@RequestBody RoleToUserForm form) {
	    service.addRoleToUser(form.getEmail(), form.getRoleName());	  
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
	                if(user.getStatus() == Status.OFFLINE) {
	                	user.setStatus(Status.ONLINE);
	                	repo.save(user);
	                }
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
	 @SendTo("/user/public")
	 public User addUser(@Payload User user) {
	 	service.saveUser(user);
	 	return user;
	 }
	 @MessageMapping("/user.disconnectUser")
	 @SendTo("/user/public")
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
    private String roleName;
    
}
