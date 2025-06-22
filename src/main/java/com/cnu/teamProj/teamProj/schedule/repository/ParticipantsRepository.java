package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.schedule.entity.Participants;
import com.cnu.teamProj.teamProj.schedule.entity.ParticipantsId;
import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import com.cnu.teamProj.teamProj.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants, ParticipantsId> {
    boolean existsByIdAndScheId(User userId, Schedule scheId);
    List<Participants> findParticipantsByScheId(Schedule scheID);
    List<Participants> findAllById(User id);
    Participants findParticipantsByScheIdAndId(Schedule scheId, User id);

    @Query(value = "SELECT p.id FROM Participants p WHERE p.scheId = :scheId")
    ArrayList<User> findParticipantsUserIdByScheId(@Param("scheId") Schedule scheId);
}
