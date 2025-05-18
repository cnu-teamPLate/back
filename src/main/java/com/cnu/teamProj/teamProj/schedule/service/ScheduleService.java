package com.cnu.teamProj.teamProj.schedule.service;

import com.cnu.teamProj.teamProj.comment.Comment;
import com.cnu.teamProj.teamProj.comment.CommentRepository;
import com.cnu.teamProj.teamProj.common.DateToStringMapping;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.schedule.dto.*;
import com.cnu.teamProj.teamProj.schedule.entity.Participants;
import com.cnu.teamProj.teamProj.schedule.entity.Schedule;
import com.cnu.teamProj.teamProj.schedule.repository.ParticipantsRepository;
import com.cnu.teamProj.teamProj.schedule.repository.ScheduleRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.task.entity.Task;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
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
    private final MemberRepository projMemRepository;

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
    /**
     * 스케쥴을 조회하는 메소드
     * @param param
     *  - id : 유저 아이디
     *  - projId : 프로젝트 아이디
     *  - date : 날짜 정보 (필수)
     *  - term(기한설정) : w(=주) or m(=달) - 프론트에서 전달해주지 않아도 됨
     *  - category : 분류 - 회의(meeting)/과제(task)/일정(plan)/
     *      - category 값이 없으면 전체 스케줄을 반환
     *      - 일정과 회의는 같은 테이블로 묶임
     * */
    public ResponseEntity<CalendarScheduleDto> getSchedule(Map<String, Object> param) {
        boolean isIdExistInParam = param.containsKey("userId");
        if(isIdExistInParam) {
            if(!userRepository.existsById(param.get("userId").toString()))
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        boolean isProjIdExistInParam = param.containsKey("projId");
        if(isProjIdExistInParam) {
            if(!projRepository.existsById(param.get("projId").toString()))
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        String category = null;
        if(param.containsKey("cate")) {
            category = param.get("cate").toString();
            logger.info("요청으로 들어온 category 값: {}", category);
        }
        if(!isIdExistInParam && !isProjIdExistInParam)
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);

        DateToStringMapping dateToStringMapping = new DateToStringMapping();
        ZonedDateTime standardDate = dateToStringMapping.stringToDateMapper(param.get("date").toString());
        if(standardDate == null) return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        ZonedDateTime endDate;
        if(param.get("term").toString().equalsIgnoreCase("w")) { //주간 일정 불러오기
            endDate = standardDate.plusWeeks(1);
        } else { //월간 일정 불러오기
            endDate = standardDate.plusMonths(1);
        }

        Map<String, List<TeamScheduleDto>> teamSchedules = new HashMap<>();
        Map<String, List<TaskScheduleDto>> taskSchedules = new HashMap<>();

        //✅특정 프로젝트의 유저와 관련된 일정 불러오기
        if(isIdExistInParam && isProjIdExistInParam) {
            logger.info("isIdExistInParam && isProjIdExistInParam");
            String projId = param.get("projId").toString();
            String userId = param.get("userId").toString();

            //프로젝트 일정 불러오기
            if(category==null || !category.equalsIgnoreCase("task")) { //범주가 '과제'라면 팀스케줄 정보까지 가져올 필요가 없음
                teamSchedules.put(projId, new ArrayList<>());
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
                        teamScheduleDto.setProjName(projName);
                        
                        if(category != null) { //일정의 종류가 요청으로 들어왔으면 해당 카테고리에 해당되는 일정만 포함
                            if(schedule.getCategory().equalsIgnoreCase(category)) {
                                teamSchedules.get(projId).add(teamScheduleDto);
                            }
                        } else { //카테고리가 없으면 모두 포함
                            teamSchedules.get(projId).add(teamScheduleDto);
                        }
                    }
                }
            }
            logger.info("조건문 확인: {}", category.equalsIgnoreCase("task"));
            //개인 과제 불러오기
            if(category == null || category.equalsIgnoreCase("task")) {
                logger.info("안으로 들어옴");
                taskSchedules.put(projId, new ArrayList<>());
                List<Task> tasks = taskRepository.findTasksByProjIdAndId(projId, userId);
                logger.info("tasks의 개수: {}", tasks.size());
                for(Task task : tasks) {
                    boolean isInPeriod = task.getDate().isAfter(standardDate) && task.getDate().isBefore(endDate);
                    if(isInPeriod) {
                        TaskScheduleDto taskScheduleDto = new TaskScheduleDto(task);
                        String projName = projRepository.findById(task.getProjId()).orElseThrow().getProjName();
                        taskScheduleDto.setProjName(projName);
                        taskSchedules.get(projId).add(taskScheduleDto);
                    }
                }
            }
        }
        //✅유저가 참여중인 스케줄 불러오기
        else if (isIdExistInParam) {
            logger.info("isIdExistInParam");
            String userId = param.get("userId").toString();

            //프로젝트 일정 불러오기
            if(category==null || !category.equalsIgnoreCase("task")) {
                List<Participants> participants = participantsRepository.findAllById(userId); //유저가 참여중인 프로젝트 및 일정 정보 가져오기
                for(Participants participant : participants) {
                    String scheId = participant.getScheId();
                    String projId = participant.getProjId();
                    Schedule schedule = scheduleRepository.findByScheId(scheId);
                    boolean isInPeriod = schedule.getDate().isAfter(standardDate) && schedule.getDate().isBefore(endDate);
                    if(isInPeriod) {
                        if(!teamSchedules.containsKey(projId)) teamSchedules.put(projId, new ArrayList<>());

                        getTeamSchedule(category, teamSchedules, projId, schedule);
                    }
                }
            }

            //과제 불러오기
            if(category == null || category.equalsIgnoreCase("task")) {
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
        }
        //✅프로젝트 아이디를 기준으로 스케줄 불러오기
        else {
            logger.info("프로젝트 아이디만");
            String projId = param.get("projId").toString();

            Project project = projRepository.findById(projId).get();

            if(category==null || !category.equalsIgnoreCase("task")) {
                //팀 스케줄 가져오기
                teamSchedules.put(projId, new ArrayList<>());
                List<Schedule> schedules = scheduleRepository.findSchedulesByProjId(project);

                for(Schedule schedule : schedules) {
                    boolean isInPeriod = schedule.getDate().isAfter(standardDate) && schedule.getDate().isBefore(endDate);
                    if(isInPeriod) {
                        getTeamSchedule(category, teamSchedules, projId, schedule);
                    }
                }

            }

            //과제 가져오기 - 과제는 정해진 카테고리가 없을 때만 불러오기
            if(category == null || category.equalsIgnoreCase("task")) {
                taskSchedules.put(projId, new ArrayList<>());
                if(category==null) {
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
            }

        }
        if(teamSchedules.isEmpty() && taskSchedules.isEmpty())
            return new ResponseEntity<>(null, HttpStatus.OK);
        return new ResponseEntity<>(new CalendarScheduleDto(teamSchedules, taskSchedules), HttpStatus.OK);
    }

    private void getTeamSchedule(String category, Map<String, List<TeamScheduleDto>> teamSchedules, String projId, Schedule schedule) {
        TeamScheduleDto teamScheduleDto = new TeamScheduleDto(schedule);
        String projName = schedule.getProjId().getProjName();
        teamScheduleDto.setProjName(projName);
        if(category != null) { //일정의 종류가 요청으로 들어왔으면 해당 카테고리에 해당되는 일정만 포함
            if(schedule.getCategory().equalsIgnoreCase(category)) {
                teamSchedules.get(projId).add(teamScheduleDto);
            }
        } else { //카테고리가 없으면 모두 포함
            teamSchedules.get(projId).add(teamScheduleDto);
        }
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
    public ResponseEntity<?> updateSchedule(ScheduleUpdateDto scheduleDto) {
        if(!scheduleRepository.existsById(scheduleDto.getScheId()))
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "해당 아이디의 스케줄 레코드가 존재하지 않습니다");
        Project project = projRepository.findById(scheduleDto.getProjId()).orElse(null);
        if(project==null)
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 프로젝트가 아닙니다");

        String scheId = scheduleDto.getScheId();
        logger.info("scheId: {}", scheId);

        //참가자 목록 업데이트
        for(String userId : scheduleDto.getParticipants().keySet()) {
            User user = userRepository.findById(userId).orElse(null);
            if(user==null)
                return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 유저가 아닙니다");

            int status = scheduleDto.getParticipants().get(userId);
            Participants participant = participantsRepository.findParticipantsByScheIdAndId(scheId, userId);
            if(!projMemRepository.existsByIdAndProjId(user, project))
                return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "프로젝트 구성원이 아닌 유저는 참여자로 추가할 수 없습니다");
            if(status == -1 && participant != null) participantsRepository.delete(participant);
            else {
                Participants newParticipant = new Participants(scheId, userId, scheduleDto.getProjId());
                participantsRepository.save(newParticipant);
            }
        }

        Schedule schedule = new Schedule(scheduleDto);
        schedule.setProjId(projRepository.findProjectByProjId(scheduleDto.getProjId()));
        scheduleRepository.save(schedule);
        return ResultConstant.returnResult(1);
    }
}
