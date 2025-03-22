package com.cnu.teamProj.teamProj.security.repository;

import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByMail(String mail); //test
    Boolean existsByName(String name);

    Boolean existsByMail(String mail);

    @Query("select user from User user where user.id like :query")
    List<User> findUsersById(@Param("query") String query);

    @Query("select user from User user where user.name like :query")
    List<User> findUsersByName(@Param("query") String query);
}
