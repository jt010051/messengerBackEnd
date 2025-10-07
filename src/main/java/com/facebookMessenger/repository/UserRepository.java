package com.facebookMessenger.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.facebookMessenger.domain.Status;
import com.facebookMessenger.domain.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
	User findByemail(String email);
	User findByphone(String phone);
	List<User> findAllByStatusAndEmailIn(Status online, List<String> users);

List<String> findAllByFriends (User friends);

}
