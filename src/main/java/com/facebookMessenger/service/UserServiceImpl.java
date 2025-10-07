package com.facebookMessenger.service;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import com.facebookMessenger.domain.Roles;
import com.facebookMessenger.domain.Status;
import com.facebookMessenger.domain.User;
import com.facebookMessenger.repository.RoleRepository;
import com.facebookMessenger.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service @RequiredArgsConstructor @Transactional @Slf4j


public class UserServiceImpl implements UserService, UserDetailsService 
{
	private final UserRepository repo;
	private final RoleRepository roleRepo;
	private final ChatRoomService chatService;
	private final PasswordEncoder passwordEncoder;

 


	
	public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
		User thisUser = null;
		String phoneCheck = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
	
		
	    Pattern pattern = Pattern.compile(phoneCheck);
	 
	    Matcher matcher = pattern.matcher(user);
	
	    if(matcher.matches()) thisUser = repo.findByphone(user.trim());
	    
		
		
	    else thisUser = repo.findByemail(user.trim());
	 
		
	    if(thisUser == null) {
	        log.error("Email or Phone Number not found in the database");
	        throw new UsernameNotFoundException("Email or Phone Number not found in the database");
	    } else {

	        repo.save(thisUser);
	        log.info("Email or Phone Number found in the database: {}", user);
	        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
	      
	        thisUser.getRole().forEach(role -> {
	            authorities.add(new SimpleGrantedAuthority(role.getName()));
	        });
	        org.springframework.security.core.userdetails.User u = new org.springframework.security.core.userdetails.User(thisUser.getEmail(), thisUser.getPassword(), authorities);
	     

	        return u;
	    }
	
	}

	@Override
	public User saveUser(User user) throws UsernameNotFoundException {
			if(repo.findByemail(user.getEmail()) != null) return null;
//		  List<User> list = repo.findAll();
		  String thisUser = (user.getEmail() != "" ? user.getEmail() : user.getPhone());	    	
		  
		  if(user.getEmail() == "") {
			 if(repo.findByphone(user.getPhone()) != null) return null;
		  }
		  else if(user.getPhone() == "") {
			  if(repo.findByemail(user.getEmail()) != null) return null;
		  }
		  
		  if(user.getPhone().equals("")) user.setPhone("tempPhone");
		  else if(user.getEmail().equals("")) user.setEmail("tempEmail");
		  
		  log.info("Saving  {} to the database", thisUser);
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        
	      

		 return repo.save(user);		    			
	}

	@Override
	public List<User> allUsers() {
		return 	repo.findAll();
	}

	@Override
	public User getUser(String user) throws UsernameNotFoundException 
	{
		log.info(user);
		
		if(repo.findByphone(user) != null) return repo.findByphone(user);
		  if(repo.findByemail(user) == null) {
		        log.error("Email or Phone Number not found in the database");
		        throw new UsernameNotFoundException("Email or Phone Number not found in the database");
		    }
		return repo.findByemail(user);
	}
	
	
	
	
	
	@Override
	public Roles saveRole(Roles role) {
	       log.info("Saving new role {} to the database", role.getName());        
	       return roleRepo.save(role);
	}
	
	
	
	
	@Override
	public void addRoleToUser(String user) {
		
	    User thisUser = getUser(user);
	    Collection<Roles> roles = thisUser.getRole();
	    	for(Map.Entry<String, Boolean> map: thisUser.getIsPending().entrySet()) {
	    	     Roles role = roleRepo.findByName(map.getKey());
	    	    
	    		    
	    	        if(role == null) {
	    	        	log.info("role {} not found", role);
	    	        	return;
	    	        }
	    	        if(map.getValue() == true) {
	    	        	log.info("role {} already added", role);
	    	        	continue;
	    	        }
	    	      
	    	        thisUser.getRole().add(role);
	    		    
	    			log.info("Adding role {} to  {}", role, thisUser);
	    			log.info("userDetails {}", user);
	    			roles.add(role);
	    			thisUser.getIsPending().put(map.getKey(), false);
	    	}
	    		repo.save(thisUser);
	}
	
	@Override
	public User updatePassword(String password, User updatedUser) {
		updatedUser.setPassword(passwordEncoder.encode(password));
		return repo.save(updatedUser);
	}
	
	@Override
	public User update(User updatedUser) {
		User thisUser= repo.findById(updatedUser.getID()).get();
		
		if(thisUser.getEmail() != updatedUser.getEmail() && updatedUser.getEmail() != "") thisUser.setEmail(updatedUser.getEmail());
		if(thisUser.getFirstName() != updatedUser.getFirstName()&& updatedUser.getFirstName() != "") thisUser.setFirstName(updatedUser.getFirstName());
		if(thisUser.getLastName() != updatedUser.getLastName() && updatedUser.getLastName() != "") thisUser.setLastName(updatedUser.getLastName());
		if(thisUser.getBirthDate() != updatedUser.getBirthDate() && updatedUser.getBirthDate() != "") thisUser.setBirthDate(updatedUser.getBirthDate());
		if(thisUser.getGender() != updatedUser.getGender() && updatedUser.getGender().toString() != "") thisUser.setGender(updatedUser.getGender());
		
		return repo.save(thisUser);
	}
	
	@Override
	public void delete(User user) {
		repo.deleteById(user.getID());
		
	}
	
	@Override
	public void removeRole(String username) {
		User user = getUser(username);
		user.setRole(null);		
	}
	


	public void disconnect(User user) {
		var storedUser = repo.findByemail(user.getEmail());				
		if(storedUser == null)storedUser = repo.findByphone(user.getEmail());
		else if(storedUser != null) {
			storedUser.setStatus(Status.OFFLINE);
			repo.save(storedUser);
			log.info("{} Successfully logged out", user.getEmail());
		}
	
	}
	
	public List<User> findConnectedUser(User user){
		List<User> online = repo.findAllByStatusAndEmailIn(Status.ONLINE, user.getFriends());
		System.out.println(online);
		return online;
	}

	@Override
	public void addFriend(String thisUser, String userToAdd) {
		User user = getUser(thisUser);
		if(user.getFriends().contains(userToAdd)) {
			log.info("Users are already friends");
			return;
		}
		
		User otherUser = getUser(userToAdd);
		List<String> myFriends = user.getFriends();
		List<String> theirFriends = otherUser.getFriends();

		myFriends.add(userToAdd);
		user.setFriends(myFriends);

		theirFriends.add(thisUser);
		otherUser.setFriends(theirFriends);
		chatService.getChatRoomId(thisUser, userToAdd, true);
		user.getRequests().remove(userToAdd);
		otherUser.getRequests().remove(thisUser);

		
		
		repo.save(user);
		repo.save(otherUser);
	}
	
	@Override
	public List<String> myFriends(String thisUser){
		User user = getUser(thisUser);
		return 	user.getFriends();
	}

	@Override
	public Map<String, String>myFriendRequests(User user) {
		
		return user.getRequests();
	}

	@Override
	public Map<String, String> newFriendRequest(User userSendingRequest, User userRecievingRequest) {
		userRecievingRequest.getRequests().put(userSendingRequest.getEmail(), "Request");
		repo.save(userRecievingRequest);
		repo.save(userSendingRequest);
		return userSendingRequest.getRequests();

	}

	@Override
	public void connect(User user) {
		user.setStatus(Status.ONLINE);
		repo.save(user);
	}
	
}
