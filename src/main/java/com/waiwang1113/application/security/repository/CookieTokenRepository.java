package com.waiwang1113.application.security.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.waiwang1113.application.security.entity.CookieToken;

@Repository
public interface CookieTokenRepository extends CrudRepository<CookieToken, String>{

}
