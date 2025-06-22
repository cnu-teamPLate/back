package com.cnu.teamProj.teamProj.proj.repository;

import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.entity.ProjMemId;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<ProjMem, ProjMemId> {
    List<ProjMem> findProjMemsByProjId(Project projId);
    Boolean existsByProjId(Project projId);
    Boolean existsProjMemByIdAndProjId(User user, Project projId);

    // 특정 userId와 projId를 기반으로 멤버 존재 여부 확인
    Boolean existsByIdAndProjId(User id, Project project);

    // 특정 userId와 projId를 기반으로 멤버 삭제
    void deleteByIdAndProjId(User user, Project projId);

    //유저가 참여중인 프로젝트 반환
    List<ProjMem> findProjMemsById(User user);


}
