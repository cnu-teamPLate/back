package com.cnu.teamProj.teamProj.file.repository;

import com.cnu.teamProj.teamProj.file.entity.Doc;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocRepository extends JpaRepository<Doc, Integer> {
    List<Doc> findDocsByProjId(Project projId);
    List<Doc> findDocsByCreatedBy(User createdBy);
    List<Doc> findDocsByProjIdAndCreatedBy(Project projId, User createdBy);
}
