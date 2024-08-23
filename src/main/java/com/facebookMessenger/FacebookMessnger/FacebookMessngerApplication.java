package com.facebookMessenger.FacebookMessnger;


import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
import com.facebookMessenger.FacebookMessnger.domain.BirthDate;
import com.facebookMessenger.FacebookMessnger.domain.Gender;
import com.facebookMessenger.FacebookMessnger.domain.Roles;
import com.facebookMessenger.FacebookMessnger.domain.Status;
import com.facebookMessenger.FacebookMessnger.domain.User;
import com.facebookMessenger.FacebookMessnger.repository.ChatRoomRepository;
import com.facebookMessenger.FacebookMessnger.service.UserService;
import lombok.AllArgsConstructor;

@SpringBootApplication
@AllArgsConstructor
public class FacebookMessngerApplication  {
	
ChatRoomRepository repo;
	public static void main(String[] args) {
		SpringApplication.run(FacebookMessngerApplication.class, args);
	}
//	@Bean
//	PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
	@Bean
	CommandLineRunner run(UserService userService) {
		
		return args -> {
			
			BirthDate birthDate = new BirthDate("September", 9, 1993);
			BirthDate birthDate2 = new BirthDate("September", 9, 1993);
			BirthDate birthDate3 = new BirthDate("September", 9, 1993);

			String birth = birthDate.toString();
			String birth2 = birthDate2.toString();
			String birth3 = birthDate3.toString();

			List<User> friends2 = new ArrayList<>();
			List<User> friends3 = new ArrayList<>();
			
			userService.saveRole(new Roles(null, "ROLE_ADMIN"));
			userService.saveRole(new Roles(null, "ROLE_USER"));
			userService.saveUser(new User(null , "jont26.smith@gmail.com", 
								 "admin", "Jon-Thomas", "Smith",
								 birth, Gender.MALE.toString(), 
								 new ArrayList<>(), "1111111111", 
								 false, Status.OFFLINE, new ArrayList<>()));
			userService.addRoleToUser("jont26.smith@gmail.com", "ROLE_ADMIN");
			
			friends2.add(userService.getUser("jt010051@gmail.com"));
			userService.saveUser(new User(null , "jt010051@gmail.com", 
								 		  "admin", "Jon-Thomas2", "Smith",birth2
								 		  , Gender.FEMALE.toString(), 
								 		  new ArrayList<>(), "1111111112", 
								 		  false, Status.OFFLINE, friends2));
			userService.addRoleToUser("jt010051@gmail.com", "ROLE_USER");
			friends2.add(userService.getUser("test@test.com"));
			
			userService.saveUser(new User(null , "test@test.com", "admin", 
										  "Jon-Thomas3", "Smith",birth3
										  , Gender.THEY.toString(), 
										  new ArrayList<>(), "1111111113", 
										  false, Status.OFFLINE, friends3));
			userService.addRoleToUser("test@test.com", "ROLE_USER");
			
			
			
			
//			ChatRoom defaultRoom = new ChatRoom("1", "1", "jt010051@gmail.com", "jont26.smith@gmail.com");
//			repo.save(defaultRoom);
		};
		}
	
}
