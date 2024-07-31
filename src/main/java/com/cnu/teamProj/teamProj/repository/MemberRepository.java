package com.cnu.teamProj.teamProj.repository;

import com.cnu.teamProj.teamProj.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    MemberEntity findByUsername(String username);
    MemberEntity findByMemberEmail(String email);
}
