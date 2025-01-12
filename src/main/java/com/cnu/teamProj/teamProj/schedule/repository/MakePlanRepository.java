package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.schedule.entity.MakePlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MakePlanRepository extends JpaRepository<MakePlanEntity, Integer> {
}
