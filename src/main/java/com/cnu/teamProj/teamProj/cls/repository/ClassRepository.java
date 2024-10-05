package com.cnu.teamProj.teamProj.cls.repository;

import com.cnu.teamProj.teamProj.cls.entity.ClassInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassRepository extends JpaRepository<ClassInfo, String> {
}
