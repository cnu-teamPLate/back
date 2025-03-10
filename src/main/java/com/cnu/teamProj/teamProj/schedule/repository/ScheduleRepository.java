package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    List<Schedule> findSchedulesByProjId(Project projId);
    Schedule findByScheId(String scheId);
}
