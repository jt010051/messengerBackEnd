package com.facebookMessenger.FacebookMessnger.service;

import java.util.List;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

//import org.springframework.security.core.userdetails.UserDetails;

import com.facebookMessenger.FacebookMessnger.domain.Roles;
import com.facebookMessenger.FacebookMessnger.domain.User;

public interface UserService {
	 UserDetails loadUserByUsername(String user);
	User saveUser(User user);
	List<User> allUsers();
	User getUser(String user);
	Roles saveRole(Roles role);
	void addRoleToUser(String username, String roleName);
	User updatePassword (String password, User updatedUser);
	User update (User user);
	void delete (User user);
	void removeRole(String username);
	void disconnect(User user);
	List<User> findConnectedUser(User user);
	void addFriend(String thisUser, String userToAdd);
	 List<String> myFriends(String user);
}
