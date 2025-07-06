package com.properException.handle.reposetory;

import org.springframework.data.jpa.repository.JpaRepository;

import com.properException.handle.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	boolean existsByEmail(String email);
    boolean existsByContact(String contact);
}

