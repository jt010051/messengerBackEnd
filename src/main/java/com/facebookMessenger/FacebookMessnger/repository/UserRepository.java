package com.facebookMessenger.FacebookMessnger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.facebookMessenger.FacebookMessnger.domain.Status;
import com.facebookMessenger.FacebookMessnger.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	User findByemail(String email);
	User findByphone(String phone);
	List<User> findAllByStatusAndEmailIn(Status online, List<String> users);

List<String> findAllByFriends (User friends);
}
