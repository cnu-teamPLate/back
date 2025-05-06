package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.schedule.entity.MeetingLog;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLogId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MeetingLogRepository extends JpaRepository<MeetingLog, MeetingLogId> {
}
