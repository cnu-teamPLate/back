package com.cnu.teamProj.teamProj.schedule.service;

import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.common.UserNotFoundException;
import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.file.entity.File;
import com.cnu.teamProj.teamProj.file.repository.FileRepository;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import com.cnu.teamProj.teamProj.file.service.S3Service;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.schedule.dto.MeetingListDto;
import com.cnu.teamProj.teamProj.schedule.dto.MeetingLogDto;
import com.cnu.teamProj.teamProj.schedule.dto.ScheduleCreateReqDto;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLog;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLogId;
import com.cnu.teamProj.teamProj.schedule.entity.Participants;
import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import com.cnu.teamProj.teamProj.schedule.repository.MeetingLogRepository;
import com.cnu.teamProj.teamProj.schedule.repository.ParticipantsRepository;
import com.cnu.teamProj.teamProj.schedule.repository.ScheduleRepository;
import com.cnu.teamProj.teamProj.security.dto.SimpleUserInfoDto;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.util.STTUtil;
import com.cnu.teamProj.teamProj.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingService {
    private final MeetingLogRepository meetingLogRepository;
    private final ScheduleRepository scheduleRepository;
    private final FileRepository fileRepository;
    private final ScheduleService scheduleService;
    private final ProjRepository projRepository;
    private final ParticipantsRepository participantsRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final STTUtil sttUtil;
    private final DocsService docsService;

    /**
     * 회의록 업로드
     */
    @Transactional
    public ResponseEntity<?> updateMeetingLog(MeetingLogDto dto, MultipartFile file) {
        String scheId = dto.getScheId();
        Schedule schedule;
        if(scheId == null) {
            ResponseEntity<?> createRet = scheduleService.createSchedule(new ScheduleCreateReqDto(dto));
            if(createRet.getBody() == null) {
                return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "스케줄을 생성하는 중 에러가 발생했습니다");
            }
            schedule = (Schedule) createRet.getBody();
        } else {
            schedule = scheduleRepository.findByScheId(scheId);
            if(schedule == null) {
                return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "올바른 스케줄 아이디가 아닙니다");
            }
        }
        if(!schedule.getCategory().equalsIgnoreCase("meeting")) {
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 회의가 없습니다");
        }
        String projId = dto.getProjId();
        Project project = projRepository.findProjectByProjId(projId);
        if(project == null) {
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 프로젝트가 없습니다");
        }
        //회의록 엔티티에 레코드 추가
        meetingLogRepository.save(new MeetingLog(schedule, project, dto.getContents(), dto.getFix(), dto.getSttContents()));
        //제목, 날짜 스케줄 엔티티에 반영
        schedule.setDate(dto.getDate());
        schedule.setScheName(dto.getTitle());
        schedule.setCategory("meeting");
        //참여자 리스트 수정
        //기존의 참여자 정보 삭제
        List<Participants> allParticipants = participantsRepository.findParticipantsByScheId(schedule);
        participantsRepository.deleteAll(allParticipants);
        //새로운 참여자 정보 업데이트
        for(SimpleUserInfoDto info : dto.getParticipants()) {
            User user = userRepository.findById(info.getId()).orElse(null);
            if(user == null) {
                throw new UserNotFoundException("존재하지 않는 사용자입니다");
            }
            participantsRepository.save(new Participants(schedule, user, project));
        }
        //파일 데이터 저장
        try{
            //TODO DocsService에서 saveFileToS3nDB 함수 로직 마무리
            //TODO 해당 함수 호출
            if(file != null) {
                List<MultipartFile> files = new ArrayList<>();
                files.add(file);
                ResponseDto fileRetDto = docsService.saveFileToS3nDB(files, "meeting", schedule.getScheId(), dto.getProjId());
                if(fileRetDto.getCode() != 200) {
                    throw new Exception(fileRetDto.getMessage());
                }
            }
        } catch(Exception e) {
            log.error(e.getMessage());
            return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "녹음본 저장 중 에러가 발생했습니다");
        }

        return ResultConstant.returnResult(ResultConstant.OK);
    }

    /**
     * 회의록 정보 가져오기
     */
    public ResponseEntity<?> getMeetingLog(Map<String, String> dto) {

        //유효한 유저가 보내는 요청인지 확인
        String userId = SecurityUtil.getCurrentUser();
        User client = userRepository.findById(userId).orElse(null);
//        if(client == null) {
//            result.put("유효하지 않은 유저입니다", null);
//            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
//        }

//        String projId = dto.get("projId").toString();
//        Project project = projRepository.findProjectByProjId(projId);
        log.info("요청을 보낸 유저{}", userId);
//        if(!memberRepository.existsByIdAndProjId(client, project)) {
//            result.put("회의록을 요청할 자격이 없는 유저입니다", null);
//            return new ResponseEntity<>(result, HttpStatus.NOT_ACCEPTABLE);
//        }

        if(dto.get("scheId") == null && dto.get("projId") == null) {
            return ResultConstant.returnResult(ResultConstant.REQUIRED_PARAM_NON);
        }

        //스케쥴 아이디가 null이 아닌 경우 구체적인 회의록 정보 불러오기
        if(dto.get("scheId") != null) {
            String scheId = dto.get("scheId").toString();
            Schedule schedule = scheduleRepository.findByScheId(scheId);
            if(schedule == null || !schedule.getCategory().equalsIgnoreCase("meeting")) {
                return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "스케줄 아이디가 존재하지 않습니다");
            }

            //참가자 목록 불러오기
            List<SimpleUserInfoDto> participantsForMeeting = new ArrayList<>();
            for(Participants participants : participantsRepository.findParticipantsByScheId(schedule)) {
                User user = userRepository.findById(participants.getId().getId()).orElseThrow();
                participantsForMeeting.add(new SimpleUserInfoDto(user.getUsername(), user.getId()));
            }
            //회의록에 대한 정보 가져오기 -> meetingLog
//            MeetingLogId meetingLogId = new MeetingLogId(scheId, projId);
            MeetingLog meetingLog = meetingLogRepository.findByScheId(schedule);
            //정보를 담을 dto
            MeetingLogDto ret = new MeetingLogDto(meetingLog);
            ret.setParticipants(participantsForMeeting);
            ret.setTitle(schedule.getScheName());
            ret.setDate(schedule.getDate());

            String fileType = String.format("meeting:%s", meetingLog.getScheId().getScheId());
            System.out.println("fileType: "+fileType);
            File file = fileRepository.findByFileType(fileType);
            if(file != null) ret.setFileUrl(file.getUrl());

            return new ResponseEntity<>(ret, HttpStatus.OK);
        }

        //프로젝트 아이디가 null이 아닌 경우 프로젝트에 해당하는 모든 회의록 정보 가져오기
        String projId = dto.get("projId").toString();
        Project project = projRepository.findProjectByProjId(projId);
        if(project == null) return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "프로젝트 아이디가 존재하지 않습니다");
        List<MeetingLog> allMeetings = meetingLogRepository.findAllByProjId(project);
        List<MeetingListDto> ret = new ArrayList<>(); //반환해줄 값
        for(MeetingLog log : allMeetings) {
            String fileType = String.format("meeting:%s", log.getScheId().getScheId());
            File file = fileRepository.findByFileType(fileType);
            ret.add(new MeetingListDto(log.getScheId().getScheId(), log.getScheId().getScheName(), log.getScheId().getDate(), file.getUrl()));
        }

        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /**
     * 회의 녹음본 문자로 변환
     */
    public ResponseEntity<?> convertSpeechToText(MultipartFile audioFile) {
        try{
            if(audioFile.isEmpty()) {
                return ResultConstant.returnResultCustom(ResultConstant.REQUIRED_PARAM_NON, "파일 데이터가 없습니다");
            }
            String fileType = audioFile.getContentType();
            assert fileType != null;
            if(!fileType.equalsIgnoreCase("audio/wave") && !fileType.equalsIgnoreCase("audio/x-flac") && !fileType.equalsIgnoreCase("audio/flac")) {
                log.info("파일형식: {}", fileType);
                return ResultConstant.returnResultCustom(ResultConstant.INVALID_PARAM, "지원되지 않는 오디오 형식입니다 (지원되는 오디오 형식: wave, x-flac)");
            }
            String text = sttUtil.asyncRecognizeGcs(audioFile);
            System.out.println(text);
            Map<String, String> ret = new HashMap<>();
            ret.put("result", text);
            return new ResponseEntity<>(ret, HttpStatus.OK);
        } catch(Exception e) {
            log.error("인코딩 중 문제가 발생했습니다: {}", e.getMessage());
            return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "인코딩 중 문제가 발생했습니다");
        }
    }
}
