package com.cnu.teamProj.teamProj.member.repository;

import com.cnu.teamProj.teamProj.member.entity.ProjMem;
import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<ProjMem, String> {
//    Optional<String> findBy
}
