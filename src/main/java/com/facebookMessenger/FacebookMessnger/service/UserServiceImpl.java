package com.facebookMessenger.FacebookMessnger.service;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.facebookMessenger.FacebookMessnger.domain.Roles;
import com.facebookMessenger.FacebookMessnger.domain.Status;
import com.facebookMessenger.FacebookMessnger.domain.User;
import com.facebookMessenger.FacebookMessnger.repository.RoleRepository;
import com.facebookMessenger.FacebookMessnger.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service @RequiredArgsConstructor @Transactional @Slf4j

//, UserDetailsService
public class UserServiceImpl implements UserService {
	private final UserRepository repo;
	private final RoleRepository roleRepo;
	ChatRoomService chatService;
	//private final PasswordEncoder passwordEncoder;
	//public UserDetails loadUserByUsername(String user) throws UsernameNotFoundException {
	//	User thisUser = null;
	//	String phoneCheck = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
	//
	//	
	//    Pattern pattern = Pattern.compile(phoneCheck);
	// 
	//    Matcher matcher = pattern.matcher(user);
	//
	//    if(matcher.matches()) {
	// 
	//    
	//thisUser = repo.findByphone(user);
	//    }
	//	
	//	
	//    else thisUser = repo.findByemail(user);
	//	
	//	
	//    if(thisUser == null) {
	//        log.error("Email or Phone Number not found in the database");
	//        throw new UsernameNotFoundException("Email or Phone Number not found in the database");
	//    } else {
	//        thisUser.setStatus(Status.ONLINE);
	//
	//        log.info("Email or Phone Number found in the database: {}", user);
	//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
	//      
	//        thisUser.getRoles().forEach(role -> {
	//            authorities.add(new SimpleGrantedAuthority(role.getName()));
	//        });
	//        
	//
	//    
	//        return new org.springframework.security.core.userdetails.User(thisUser.getEmail(), thisUser.getPassword(), authorities);
	//    }
	//
	//}
	
	@Override
	public User saveUser(User user) {
	
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
//		        user.setPassword(passwordEncoder.encode(user.getPassword()));
		 return repo.save(user);		    			
	}

	@Override
	public List<User> allUsers() {
		return 	repo.findAll();
	}

	@Override
	public User getUser(String user) {
		log.info(user);
		
		if(repo.findByphone(user) != null) return repo.findByphone(user);
		
		return repo.findByemail(user);
	}
	
	
	
	
	
	@Override
	public Roles saveRole(Roles role) {
	       log.info("Saving new role {} to the database", role.getName());
	        
	       return roleRepo.save(role);
	}
	
	
	
	
	@Override
	public void addRoleToUser(String email, String roleName) {
		String phoneCheck = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
	    Pattern pattern = Pattern.compile(phoneCheck); 
	    Matcher matcher = pattern.matcher(email);
	    User user = null;
        Roles role = roleRepo.findByName(roleName);
        
	    if(matcher.matches()) {	    
	    	user = repo.findByphone(email);
	    }		
	    else user = repo.findByemail(email);
	    
	    user.getRoles().add(role);
	    
		log.info("Adding role {} to  {}", roleName, email);
		log.info("userDetails {}", user);
	}
	
	@Override
	public User updatePassword(String password, User updatedUser) {
		User updatedPassword= repo.findById(updatedUser.getID()).get();
//		if(updatedPassword.getPassword() != passwordEncoder.encode(password)) {
//			updatedPassword.setPassword(passwordEncoder.encode(password));
//
//		}
//		else return null;
		
		return repo.save(updatedPassword);
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
		User user = repo.findByemail(username);
		user.setRoles(null);		
	}
	
	@Override
	public void pending(String user, String pendingRole) {
		User thisUser = null;
		String phoneCheck = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";		
	    Pattern pattern = Pattern.compile(phoneCheck);	 
	    Matcher matcher = pattern.matcher(user);
	    
	    if(matcher.matches()) thisUser = repo.findByphone(user);
	    else thisUser = repo.findByemail(user);
	    
		System.out.println(pendingRole);
		  	    	
		if(pendingRole.equals("admin")) thisUser.setPending(true);
		else {
		  	 if(!thisUser.getEmail().equals("tempEmail"))
		  	    addRoleToUser(thisUser.getEmail(), "ROLE_USER");
		  	 else addRoleToUser(thisUser.getPhone(), "ROLE_USER");
		}			
	}

	public void disconnect(User user) {
		var storedUser = repo.findByemail(user.getEmail());
				
		if(storedUser == null)storedUser = repo.findByphone(user.getEmail());
		else if(storedUser != null) {
			storedUser.setStatus(Status.OFFLINE);
			repo.save(storedUser);
		}
	}
	
	public List<User> findConnectedUser(){
		return repo.findAllByStatus(Status.ONLINE);
	}

	@Override
	public void addFriend(String thisUser, String userToAdd) {
//		User user = getUser(thisUser);
//		User otherUser = getUser(userToAdd);
//
//		List<User> listOfFriends = user.getFriends();
//		List<User> otherFriends = user.getFriends();
//
//		listOfFriends.add(otherUser);
//		otherFriends.add(user);
//		user.setFriends(listOfFriends);
//		otherUser.setFriends(otherFriends);
//		
//		repo.save(user);
//		repo.save(otherUser);
//
//		chatService.getChatRoomId(user.getFirstName(), otherUser.getFirstName(), false);
//		// TODO Auto-generated method stub
		
	}
	
}
