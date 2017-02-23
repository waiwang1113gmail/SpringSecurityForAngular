package com.waiwang1113.application.security.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.waiwang1113.application.security.entity.User;

@Repository
public interface UserRepository extends CrudRepository<User,Integer> {
	User findByEmailAndPassword(String login,String password);
}
