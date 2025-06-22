package com.cnu.teamProj.teamProj.schedule.service;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.schedule.dto.MeetingLogDto;
import com.cnu.teamProj.teamProj.schedule.dto.ScheduleCreateReqDto;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLog;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLogId;
import com.cnu.teamProj.teamProj.schedule.entity.Participants;
import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import com.cnu.teamProj.teamProj.schedule.repository.MeetingLogRepository;
import com.cnu.teamProj.teamProj.schedule.repository.ParticipantsRepository;
import com.cnu.teamProj.teamProj.schedule.repository.ScheduleRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.util.STTUtil;
import com.cnu.teamProj.teamProj.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {
    private final MeetingLogRepository meetingLogRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProjRepository projRepository;
    private final ParticipantsRepository participantsRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final STTUtil sttUtil;

    /**
     * 회의록 업로드
     */
    public ResponseEntity<String> updateMeetingLog(MeetingLogDto dto) {
        String scheId = dto.getScheId();
        Schedule schedule = scheduleRepository.findByScheId(scheId);
        if(schedule == null) {
            return new ResponseEntity<>("올바른 스케줄 아이디가 아닙니다", HttpStatus.NOT_FOUND);
        }
        if(!schedule.getCategory().equalsIgnoreCase("meeting")) {
            return new ResponseEntity<>("존재하는 회의가 없습니다", HttpStatus.BAD_REQUEST);
        }
        String projId = dto.getProjId();
        Project project = projRepository.findProjectByProjId(projId);
        if(project == null) {
            return new ResponseEntity<>("존재하는 프로젝트가 없습니다", HttpStatus.NOT_FOUND);
        }
        meetingLogRepository.save(new MeetingLog(schedule, project, dto.getContents(), dto.getFix()));
        return new ResponseEntity<>("성공적으로 회의록이 등록되었습니다", HttpStatus.OK);
    }

    /**
     * 회의록 정보 가져오기
     */
    public ResponseEntity<Map<String, Object>> getMeetingLog(Map dto) {

        Map<String, Object> result = new HashMap();
        String userId = SecurityUtil.getCurrentUser();
        User client = userRepository.findById(userId).orElse(null);
        if(client == null) {
            result.put("유효하지 않은 유저입니다", null);
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }
        String scheId = dto.get("scheId").toString();
        String projId = dto.get("projId").toString();
        Project project = projRepository.findProjectByProjId(projId);
        log.info("요청을 보낸 유저{}", userId);
        if(!memberRepository.existsByIdAndProjId(client, project)) {
            result.put("회의록을 요청할 자격이 없는 유저입니다", null);
            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
        }


        //회의 참가자, 회의에 대한 정보, 프로젝트에 대한 정보, 콘텐츠, 확정사항
        Schedule schedule = scheduleRepository.findByScheId(scheId);
        if(schedule == null) {
            result.put("올바른 스케줄 아이디가 아닙니다", null);
            return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }
        if(!schedule.getCategory().equalsIgnoreCase("meeting")) {
            result.put("존재하는 회의가 없습니다", null);
            return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        //회의에 대한 정보 가져오기 -> scheduleDto
        ScheduleCreateReqDto scheduleDto = new ScheduleCreateReqDto(schedule);
        List<String> participantsss = new ArrayList<>();
        for(Participants participants : participantsRepository.findParticipantsByScheId(schedule)) {
            User user = userRepository.findById(participants.getId().getId()).orElseThrow();
            participantsss.add(user.getUsername());
        }
        scheduleDto.setParticipants(participantsss);

        //회의록에 대한 정보 가져오기 -> meetingLog
        MeetingLogId meetingLogId = new MeetingLogId(scheId, projId);
        MeetingLog meetingLog = meetingLogRepository.findById(meetingLogId).orElseThrow();

        result.put("schedule", scheduleDto);
        result.put("회의내용", meetingLog.getContents());
        result.put("확정사항", meetingLog.getFix());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 회의 녹음본 문자로 변환
     */
    public ResponseEntity<String> convertSpeechToText(MultipartFile audioFile) {
        try{
            if(audioFile.isEmpty()) {
                return new ResponseEntity<>("파일 데이터가 없습니다", HttpStatus.BAD_REQUEST);
            }
            String fileType = audioFile.getContentType();
            assert fileType != null;
            if(!fileType.equalsIgnoreCase("audio/wave") && !fileType.equalsIgnoreCase("audio/x-flac")) {
                log.info("파일형식: {}", fileType);
                return new ResponseEntity<>("지원되지 않는 오디오 형식입니다", HttpStatus.BAD_REQUEST);
            }
            String text = sttUtil.asyncRecognizeGcs(audioFile);
            return new ResponseEntity<>(text, HttpStatus.OK);
        } catch(Exception e) {
            log.error("인코딩 중 문제가 발생했습니다: {}", e.getMessage());
            return new ResponseEntity<>("인코딩 중 문제가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
