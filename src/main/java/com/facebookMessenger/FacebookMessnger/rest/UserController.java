package com.facebookMessenger.FacebookMessnger.rest;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
	
	@GetMapping("/userDetails/{user}")
	public User getUserDetails(@PathVariable String user) {
		return service.getUser(user);
	}
	
	@PostMapping("/save")
	public User saveUser(@RequestBody User user) {
		return service.saveUser(user);
	}
	
	@DeleteMapping("delete/{email}")
	public void delete( @PathVariable("email") String email) {
		log.info("Delete user {}", email);
		User user = service.getUser(email);
		service.removeRole(email);
		service.delete(user);
	}
	
	@GetMapping("forgot/{email}")
	public User forgotPassword( @PathVariable("email") String email) {
		log.info("User {} forgot password", email);
		User user = service.getUser(email);
		if (user == null) return null;
		return user;	
	}
	
	
    @PostMapping("/pendingRole")
    public void pendingRole(@RequestParam String pendingRole, @RequestParam String user) {
    	 System.out.println(pendingRole);
      	 service.pending(user, pendingRole );
    }
    
    @PutMapping("/updatePassword")
    public User updatePassword( @RequestParam String email, @RequestParam String password) {
    	User user = repo.findByemail(email);
    	return service.updatePassword(password, user);
    }
    
	@GetMapping("/allUsers")
	public List<User> allUsers() {
		return service.allUsers();
	}
	
	@PostMapping("/addFriend/{thisUser}/{userToAdd}")
	public void addFriend(@PathVariable("thisUser") String user, 
						  @PathVariable("userToAdd") String userToAdd) {
		service.addFriend(user, userToAdd);
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
	                String access_token = JWT.create()
	                        .withSubject(user.getEmail())
	                        .withExpiresAt(new Date(System.currentTimeMillis() + expirationTime))
	                        .withIssuer(request.getRequestURL().toString())
	                        .withClaim("roles", user.getRoles().stream().map(Roles::getName).collect(Collectors.toList()))

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
	 @GetMapping("/users")
	 public ResponseEntity<List<User>> findConnectedUsers(){
	 return ResponseEntity.ok(service.findConnectedUser());
	 }
	 
	 
	 
	 
	 
}
@Data
class RoleToUserForm {
    private String email;
    private String phone;
    private String roleName;
    
}
