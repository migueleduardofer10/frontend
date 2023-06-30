package com.youtube.ecommerce.dao;

import com.youtube.ecommerce.entity.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends CrudRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.userName = ?1")
    public User findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.verificationCode = ?1")
    public User findByVerificationCode(String code);
}
