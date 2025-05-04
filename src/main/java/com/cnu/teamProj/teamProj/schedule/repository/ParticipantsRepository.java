package com.cnu.teamProj.teamProj.schedule.repository;

import com.cnu.teamProj.teamProj.schedule.entity.Participants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ParticipantsRepository extends JpaRepository<Participants, String> {
    boolean existsByIdAndScheId(String id, String scheId);
    boolean existsByIdAndProjId(String id, String projId);
    List<Participants> findParticipantsByScheId(String scheID);
    List<Participants> findAllById(String id);
    Participants findParticipantsByScheIdAndId(String scheId, String id);


}
