package com.facebookMessenger.FacebookMessnger.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.facebookMessenger.FacebookMessnger.domain.Roles;

@Repository
public interface RoleRepository extends JpaRepository<Roles, Long> {
	Roles findByName(String roleName);
}
