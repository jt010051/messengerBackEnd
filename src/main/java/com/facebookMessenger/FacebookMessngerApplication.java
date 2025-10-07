package com.facebookMessenger;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.facebookMessenger.domain.BirthDate;
import com.facebookMessenger.domain.Gender;
import com.facebookMessenger.domain.Roles;
import com.facebookMessenger.domain.Status;
import com.facebookMessenger.domain.User;
import com.facebookMessenger.repository.ChatRoomRepository;
import com.facebookMessenger.service.UserService;


@SpringBootApplication
public class FacebookMessngerApplication  {
	
ChatRoomRepository repo;
	public static void main(String[] args) {
		
		SpringApplication.run(FacebookMessngerApplication.class, args);
	}
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
	CommandLineRunner run(UserService userService) {
		
		return args -> {
			
			BirthDate birthDate = new BirthDate("September", 9, 1993);
			BirthDate birthDate2 = new BirthDate("September", 9, 1993);
			BirthDate birthDate3 = new BirthDate("September", 9, 1993);

			String birth = birthDate.toString();
			String birth2 = birthDate2.toString();
			String birth3 = birthDate3.toString();
			
Map <String, Boolean> one = new HashMap<>();
Map <String, Boolean> two = new HashMap<>();
Map <String, Boolean> three = new HashMap<>();

one.put( "ROLE_ADMIN", false);
two.put( "ROLE_USER", true);
three.put( "ROLE_ADMIN", true);
		
			userService.saveRole(new Roles(null, "ROLE_ADMIN"));
			userService.saveRole(new Roles(null, "ROLE_USER"));
			
			
			
			userService.saveUser(new User(null , "jont26.smith@gmail.com", 
								 "admin", "Jon-Thomas", "Smith",
								 birth, Gender.MALE.toString(), 
								 new ArrayList<>(), "1111111111", 
								 one, Status.OFFLINE, new ArrayList<>(), new HashMap<>()));
			
		
			
			
			
			userService.saveUser(new User(null , "jt010051@gmail.com", 
								 		  "admin", "Jon-Thomas2", "Smith",birth2
								 		  , Gender.FEMALE.toString(), 
								 		  new ArrayList<>(), "1111111112", 
								 		 two, Status.OFFLINE, new ArrayList<>(), new HashMap<>()));
			
			
			
			
			
			userService.saveUser(new User(null , "test@test.com", "admin", 
										  "Jon-Thomas3", "Smith",birth3
										  , Gender.THEY.toString(), 
										  new ArrayList<>(), "1111111113", 
										  three, Status.OFFLINE, new ArrayList<>(), new HashMap<>()));
			

		};
		}
	
}
