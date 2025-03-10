package com.cnu.teamProj.teamProj.schedule.service;

import com.cnu.teamProj.teamProj.comment.Comment;
import com.cnu.teamProj.teamProj.comment.CommentRepository;
import com.cnu.teamProj.teamProj.common.DateToStringMapping;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.ProjMemRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.schedule.dto.*;
import com.cnu.teamProj.teamProj.schedule.entity.Participants;
import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import com.cnu.teamProj.teamProj.schedule.entity.Task;
import com.cnu.teamProj.teamProj.schedule.repository.ParticipantsRepository;
import com.cnu.teamProj.teamProj.schedule.repository.ScheduleRepository;
import com.cnu.teamProj.teamProj.schedule.repository.TaskRepository;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);
    private final ScheduleRepository scheduleRepository;
    private final TaskRepository taskRepository;
    private final ProjMemRepository projMemRepository;

    private final ProjRepository projRepository;
    private final ParticipantsRepository participantsRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    /**
     * @param scheduleDto - 업로드용 디티오
     * @return
     *  - 입력값이 null 이면 0 반환
     *  - 프로젝트 아이디가 존재하지 않으면 -1 반환
     *  - 존재하지 않는 유저가 참여자로 들어가 있으면 -2 반환
     *  - 정상 저장되었으면 1 반환
     * */
    public int createSchedule(ScheduleDto scheduleDto) {
        if(scheduleDto == null) return 0;
        if(projRepository.findById(scheduleDto.getProjId()).isEmpty()) return -1;
        Project project = projRepository.findById(scheduleDto.getProjId()).get();
        //schedule 아이디 생성
        int scheCnt = project.getScheCnt()+1;
        project.setScheCnt(scheCnt);
        projRepository.save(project);
        String scheId = String.format("%s_%d", project.getProjId(), scheCnt);
        //schedule 테이블에 값 저장
        Schedule schedule = new Schedule(scheId, scheduleDto.getDate(), scheduleDto.getScheName(), scheduleDto.getPlace(), scheduleDto.getCategory(), scheduleDto.getDetail(), project);
        scheduleRepository.save(schedule);
        //participants 테이블에 값 저장
        List<String> people = scheduleDto.getParticipants();
        for(String teamone : people) {
            if(userRepository.findById(teamone).isEmpty()) return -2;
            Participants participants = new Participants(scheId, teamone, project.getProjId());
            participantsRepository.save(participants);
        }
        return 1;
    }

    //스케줄 조회
    /**
     * @param param
     *  - id : 유저 아이디
     *  - projId : 프로젝트 아이디
     *  - date : 날짜 정보 (필수)
     *  - term : "w" or "m"
     * */

    public ResponseEntity<CalendarScheduleDto> getSchedule(Map<String, Object> param) {
        boolean isIdExistInParam = param.containsKey("id");
        if(isIdExistInParam) {
            if(!userRepository.existsById(param.get("userId").toString()))
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        boolean isProjIdExistInParam = param.containsKey("projId");
        if(isProjIdExistInParam) {
            if(!projRepository.existsById(param.get("projId").toString()))
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }

        if(!isIdExistInParam && !isProjIdExistInParam)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        DateToStringMapping dateToStringMapping = new DateToStringMapping();
        ZonedDateTime standardDate = dateToStringMapping.stringToDateMapper(param.get("date").toString());
        if(standardDate == null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        ZonedDateTime endDate;
        if(param.get("term").toString().equalsIgnoreCase("w")) { //주간 일정 불러오기
            endDate = standardDate.plusWeeks(1);
        } else {
            endDate = standardDate.plusMonths(1);
        }

        Map<String, List<TeamScheduleDto>> teamSchedules = new HashMap<>();
        Map<String, List<TaskScheduleDto>> taskSchedules = new HashMap<>();

        //해당 프로젝트의 유저와 관련된 일정 불러오기
        if(isIdExistInParam && isProjIdExistInParam) {
            String projId = param.get("projId").toString();
            String userId = param.get("userId").toString();
            //이 경우 한 개의 프로젝트 아이디만 키 값으로 가짐
            teamSchedules.put(projId, new ArrayList<>());
            taskSchedules.put(projId, new ArrayList<>());

            Project project = projRepository.findById(projId).stream().toList().get(0);
            String projName = project.getProjName();
            //해당 프로젝트 아이디로 등록된 스케쥴 모두 불러오기
            List<Schedule> schedules = scheduleRepository.findSchedulesByProjId(project);
            //팀 스케줄 불러오기
            for(Schedule schedule : schedules) {
                String scheduleId = schedule.getScheId();
                boolean isInPeriod = schedule.getDate().isAfter(standardDate) && schedule.getDate().isBefore(endDate);
                if(participantsRepository.existsByIdAndScheId(userId, scheduleId) && isInPeriod) {
                    TeamScheduleDto teamScheduleDto = new TeamScheduleDto(schedule);
//                    List<Participants> participants = participantsRepository.findParticipantsByScheId(scheduleId);
//                    List<String> participantIds = new ArrayList<>();
//                    for(Participants participant : participants) {
//                        participantIds.add(participant.getId());
//                    }
                    teamScheduleDto.setProjName(projName);
                    teamSchedules.get(projId).add(teamScheduleDto);
                }
            }
            //개인 과제 불러오기
            List<Task> tasks = taskRepository.findTasksByProjIdAndId(projId, userId);
            for(Task task : tasks) {
                boolean isInPeriod = task.getDate().isAfter(standardDate) && task.getDate().isBefore(endDate);
                if(isInPeriod) {
                    TaskScheduleDto taskScheduleDto = new TaskScheduleDto(task);
                    taskSchedules.get(projId).add(taskScheduleDto);
                }
            }
        }
        else if (isIdExistInParam) { //아이디 값만 들어온 경우
            String userId = param.get("userId").toString();

            //팀 스케쥴 불러오기
            //participants 테이블에서 아이디로 record값을 조회해서 scheId를 가지고 오고 scheId로 schedule 테이블에서 값 가져오기
            List<Participants> participants = participantsRepository.findAllById(userId);
            for(Participants participant : participants) {
                String scheId = participant.getScheId();
                String projId = participant.getProjId();
                Schedule schedule = scheduleRepository.findByScheId(scheId);
                boolean isInPeriod = schedule.getDate().isAfter(standardDate) && schedule.getDate().isBefore(endDate);
                if(isInPeriod) {
                    if(!teamSchedules.containsKey(projId)) teamSchedules.put(projId, new ArrayList<>());

                    TeamScheduleDto teamScheduleDto = new TeamScheduleDto(schedule);
                    String projName = schedule.getProjId().getProjName();
                    teamScheduleDto.setProjName(projName);

                    teamSchedules.get(projId).add(teamScheduleDto);
                }
            }
            //과제 불러오기
            List<Task> tasks = taskRepository.findTasksById(userId);
            for(Task task : tasks) {
                boolean isInPeriod = task.getDate().isAfter(standardDate) && task.getDate().isBefore(endDate);
                if(isInPeriod) {
                    TaskScheduleDto taskScheduleDto = new TaskScheduleDto(task);
                    String projName = projRepository.findById(task.getProjId()).stream().toList().get(0).getProjName();
                    taskScheduleDto.setProjName(projName);

                    String projId = task.getProjId();
                    if(!taskSchedules.containsKey(projId)) taskSchedules.put(projId, new ArrayList<>());

                    taskSchedules.get(projId).add(taskScheduleDto);
                }
            }
        }
        else { //프로젝트 아이디를 기준으로 스케줄 불러오기
            String projId = param.get("projId").toString();
            teamSchedules.put(projId, new ArrayList<>());
            taskSchedules.put(projId, new ArrayList<>());

            Project project = projRepository.findById(projId).get();

            //팀 스케줄 가져오기
            List<Schedule> schedules = scheduleRepository.findSchedulesByProjId(project);

            for(Schedule schedule : schedules) {
                boolean isInPeriod = schedule.getDate().isAfter(standardDate) && schedule.getDate().isBefore(endDate);
                if(isInPeriod) {
                    TeamScheduleDto teamScheduleDto = new TeamScheduleDto(schedule);
                    String projName = schedule.getProjId().getProjName();
                    teamScheduleDto.setProjName(projName);

                    teamSchedules.get(projId).add(teamScheduleDto);
                }
            }

            //과제 가져오기
            List<Task> tasks = taskRepository.findTasksByProjId(projId);

            for(Task task : tasks) {
                boolean isInPeriod = task.getDate().isAfter(standardDate) && task.getDate().isBefore(endDate);
                if(isInPeriod) {
                    TaskScheduleDto taskScheduleDto = new TaskScheduleDto(task);
                    String projName = projRepository.findById(task.getProjId()).stream().toList().get(0).getProjName();
                    taskScheduleDto.setProjName(projName);

                    taskSchedules.get(projId).add(taskScheduleDto);
                }
            }
        }
        if(teamSchedules.isEmpty() && taskSchedules.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.OK);
        return new ResponseEntity<>(new CalendarScheduleDto(teamSchedules, taskSchedules), HttpStatus.OK);
    }

    public ResponseEntity<Object> getObjectResponseEntity(@RequestBody Map<String, Object> param) {
        ResponseEntity<CalendarScheduleDto> ret = getSchedule(param);
        if(ret.getStatusCode() == HttpStatus.BAD_REQUEST)
            return new ResponseEntity<>("프로젝트 아이디 혹은 유저 아이디를 파라미터로 넣어줘야 함니다", HttpStatus.NOT_FOUND);
        if(ret.getStatusCode() == HttpStatus.NOT_FOUND)
            return new ResponseEntity<>("프로젝트 아이디 혹은 유저 아이디가 존재하지 않습니다", HttpStatus.NOT_FOUND);
        if(ret.getBody() == null)
            return new ResponseEntity<>("조회된 레코드가 없습니다", HttpStatus.OK);
        else return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    /**
     * @param scheId - 삭제하려는 스케쥴 아이디
     * @return
     * -1 = 존재하는 스케줄이 없을 때
     * 1 = 성공적으로 삭제됐을 때
     * */
    @Transactional
    public int deleteSchedule(String scheId) {
        if(!scheduleRepository.existsById(scheId)) return -1;
        //참가자 먼저 지우기
        List<Participants> participants = participantsRepository.findParticipantsByScheId(scheId);
        participantsRepository.deleteAll(participants);
        //스케줄 지우기
        Schedule schedule = scheduleRepository.findByScheId(scheId);
        scheduleRepository.delete(schedule);
        return 1;
    }

    @Transactional
    public int deleteTask(int taskId) {
        if(!taskRepository.existsByTaskId(taskId)) return -1;
        //comment 지우기
        List<Comment> comments = commentRepository.findCommentsByTaskId(taskId);
        commentRepository.deleteAll(comments);
        //task 지우기
        taskRepository.delete(taskRepository.findTaskByTaskId(taskId));
        return 1;
    }

    public int updateTask(TaskUpdateDto taskDto) {
        if(!taskRepository.existsByTaskId(taskDto.getTaskId())) return -1;
        Task task = new Task(taskDto);
        taskRepository.save(task);
        return 1;
    }

    @Transactional
    public int updateSchedule(ScheduleUpdateDto scheduleDto) {
        if(!scheduleRepository.existsById(scheduleDto.getScheId())) return -1;
        String scheId = scheduleDto.getScheId();

        //참가자 목록 업데이트
        for(String userId : scheduleDto.getParticipants().keySet()) {
            int status = scheduleDto.getParticipants().get(userId);
            Participants participant = participantsRepository.findParticipantsByScheIdAndId(scheId, userId);
            if(participant == null) return -1;
            if(status == -1) participantsRepository.delete(participant);
            else participantsRepository.save(participant);
        }

        Schedule schedule = new Schedule(scheduleDto);
        schedule.setProjId(projRepository.findProjectByProjId(scheduleDto.getProjId()));
        scheduleRepository.save(schedule);
        return 1;
    }
}
