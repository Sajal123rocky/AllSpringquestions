package com.nw.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nw.model.Users;

public interface UserRepository extends JpaRepository<Users, String>{
	@Query(value="from Users where email=:email")
	Optional<Users> findByEmail(@Param("email") String email);

}
