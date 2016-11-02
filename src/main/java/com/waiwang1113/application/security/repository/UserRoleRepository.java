package com.waiwang1113.application.security.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.waiwang1113.application.security.entity.UserRole;
@Repository
public interface UserRoleRepository extends CrudRepository<UserRole, Integer> {
	List<UserRole> findByUserId(Integer id);
}
