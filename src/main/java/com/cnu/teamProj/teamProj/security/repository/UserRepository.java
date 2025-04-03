package com.cnu.teamProj.teamProj.security.repository;

import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByMail(String mail); //test test test test test test test test test test test test test test test test test
    Boolean existsByName(String name);

    Boolean existsByMail(String mail);

    List<User> findUsersById(String query);

    List<User> findUsersByName(String query);
}
