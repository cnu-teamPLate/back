package com.cnu.teamProj.teamProj.proj.repository;

import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<ProjMem, String> {
    List<ProjMem> findProjMemsByProjId(String projId);
    Boolean existsByProjId(String projId);
    Boolean existsProjMemByIdAndProjId(String id, String projId);
    // 특정 userId와 projId를 기반으로 멤버 존재 여부 확인
    Boolean existsByIdAndProjId(String id, String projId);

    // 특정 userId와 projId를 기반으로 멤버 삭제
    void deleteByIdAndProjId(String id, String projId);
}
