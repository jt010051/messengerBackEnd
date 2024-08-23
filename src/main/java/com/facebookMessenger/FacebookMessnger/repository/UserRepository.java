package com.facebookMessenger.FacebookMessnger.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.facebookMessenger.FacebookMessnger.domain.Status;
import com.facebookMessenger.FacebookMessnger.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{
	User findByemail(String email);
	User findByphone(String phone);
	List<User> findAllByStatus(Status online);
}
