package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.schedule.entity.MakePlan;
import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MakePlanRepository extends JpaRepository<MakePlan, Integer> {
    List<MakePlan> findMakePlansByUserId(User userId);
}
