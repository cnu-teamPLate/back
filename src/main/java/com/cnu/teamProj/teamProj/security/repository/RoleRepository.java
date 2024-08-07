package com.cnu.teamProj.teamProj.security.repository;

import com.cnu.teamProj.teamProj.security.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByName(String name);
}
