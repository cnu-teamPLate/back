package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLog;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLogId;
import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeetingLogRepository extends JpaRepository<MeetingLog, MeetingLogId> {
    List<MeetingLog> findAllByProjId(Project project);
    MeetingLog findByScheId(Schedule schedule);
}
