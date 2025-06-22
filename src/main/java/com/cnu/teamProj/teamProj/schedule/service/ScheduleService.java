package com.cnu.teamProj.teamProj.schedule.service;

import com.cnu.teamProj.teamProj.comment.Comment;
import com.cnu.teamProj.teamProj.comment.CommentRepository;
import com.cnu.teamProj.teamProj.common.BadRequestException;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.common.UserNotFoundException;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
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

import java.time.LocalDateTime;
import java.util.*;

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
    @Transactional
    public ResponseEntity<?> createSchedule(ScheduleCreateReqDto scheduleDto) {
        //필수 요청 값 유효성 검사
        if(scheduleDto == null) return ResultConstant.returnResult(ResultConstant.REQUIRED_PARAM_NON);
        if(scheduleDto.getProjId() == null || scheduleDto.getProjId().isEmpty()) return ResultConstant.returnResult(ResultConstant.REQUIRED_PARAM_NON);
        Project project = projRepository.findById(scheduleDto.getProjId()).orElse(null);
        if(project == null) return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 프로젝트 아이디가 아닙니다");
        //schedule 아이디 생성
        int scheCnt = project.getScheCnt()+1;
        project.setScheCnt(scheCnt);
        String scheId = String.format("%s_%d", project.getProjId(), scheCnt);
        //schedule 테이블에 값 저장
        Schedule schedule = new Schedule(scheId, scheduleDto.getDate(), scheduleDto.getScheName(), scheduleDto.getPlace(), scheduleDto.getCategory(), scheduleDto.getDetail(), project);
        scheduleRepository.save(schedule);
        //participants 테이블에 값 저장
        List<String> people = scheduleDto.getParticipants();
        for(String teamone : people) {
            User user = userRepository.findById(teamone).orElse(null);
            if(user == null) throw new UserNotFoundException(String.format("%s : 존재하지 않는 유저 아이디입니다", teamone));
            Participants participants = new Participants(schedule, user, project);
            participantsRepository.save(participants);
        }
        if(people.isEmpty()) {
            List<ProjMem> participants = projMemRepository.findProjMemsByProjId(project);
            for(ProjMem member : participants) {
                Participants participants1 = new Participants(schedule, member.getId(), project);
                participantsRepository.save(participants1);
            }
        }
        return ResultConstant.returnResult(ResultConstant.OK);
    }
    /**
     * 스케쥴을 조회하는 메소드
     * @param dto
     *  - id : 유저 아이디
     *  - projId : 프로젝트 아이디
     *  - date : 날짜 정보 (필수)
     *  - term(기한설정) : w(=주) or m(=달) - 프론트에서 전달해주지 않아도 됨
     *  - category : 분류 - 회의(meeting)/과제(task)/일정(plan)/
     *      - category 값이 없으면 전체 스케줄을 반환
     *      - 일정과 회의는 같은 테이블로 묶임
     * @param term 주간이면 w, 월간이면 m
     * */
    public ResponseEntity<?> getSchedule(ScheduleViewReqDto dto, String term) {
        boolean isIdExistInParam = dto.getUserId() != null && !dto.getUserId().isEmpty();
        User user = null;
        if(isIdExistInParam) {
            user = userRepository.findById(dto.getUserId()).orElse(null);
            if(user == null)
                throw new UserNotFoundException("존재하는 유저가 아닙니다");
        }
        boolean isProjIdExistInParam = dto.getProjId() != null && !dto.getProjId().isEmpty();
        if(isProjIdExistInParam) {
            if(!projRepository.existsById(dto.getProjId()))
                throw new UserNotFoundException("존재하는 프로젝트가 아닙니다");
        }

        if(!isIdExistInParam && !isProjIdExistInParam || dto.getStandardDate() == null) {
            return ResultConstant.returnResult(ResultConstant.REQUIRED_PARAM_NON);
        }

        List<String> category = null;
        if(dto.getCate() != null) category = new ArrayList<>(Arrays.asList(dto.getCate().split(",")));
        boolean isTaskInfoRequired = false; //과제 정보도 요구하는지 여부를 체크
        if(category != null && category.isEmpty()) {
            category = null;
        } else if (category != null && category.contains("task")) {
            isTaskInfoRequired = true;
            category.remove("task");
            if(category.isEmpty()) category = null;
        }

        LocalDateTime standardDate = dto.getStandardDate();
        if(standardDate == null) return ResultConstant.returnResult(ResultConstant.REQUIRED_PARAM_NON);
        LocalDateTime endDate;
        if(term.equalsIgnoreCase("w")) { //주간 일정 불러오기
            endDate = standardDate.plusWeeks(1);
        } else { //월간 일정 불러오기
            endDate = standardDate.plusMonths(1);
        }

        Map<String, List<TeamScheduleDto>> teamSchedules = new HashMap<>();
        Map<String, List<TaskScheduleDto>> taskSchedules = new HashMap<>();

        //✅특정 프로젝트의 유저와 관련된 일정 불러오기
        if(isIdExistInParam && isProjIdExistInParam) {
            String projId = dto.getProjId().trim();
            String userId = dto.getUserId().trim();


            teamSchedules.put(projId, new ArrayList<>());
            Project project = projRepository.findById(projId).orElse(null);
            if(project == null) throw new UserNotFoundException("존재하는 프로젝트가 아닙니다");
            String projName = project.getProjName();

            System.out.println("projName: "+projName);

            //해당 프로젝트 아이디로 등록된 스케쥴 모두 불러오기
            List<Schedule> schedules = scheduleRepository.findSchedulesByProjId(project);

            //팀 스케줄 불러오기
            for(Schedule schedule : schedules) {
                String scheduleId = schedule.getScheId();
                boolean isInPeriod = schedule.getDate().isAfter(standardDate) && schedule.getDate().isBefore(endDate);
                //주어진 기간에 포함되는 스케줄인지 확인
                if(participantsRepository.existsByIdAndScheId(user, schedule) && isInPeriod) {
                    TeamScheduleDto teamScheduleDto = new TeamScheduleDto(schedule);
                    teamScheduleDto.setProjName(projName);
                    //지정된 카테고리가 있다면 해당 카테고리의 값만 불러옴
                    if(category != null) {
                        boolean isTrue = false;
                        for(String cate : category) {
                            if (schedule.getCategory().equalsIgnoreCase(cate)) {
                                isTrue = true;
                                break;
                            }
                        }
                        if(isTrue) teamSchedules.get(projId).add(teamScheduleDto);
                    }
                    //지정된 카테고리가 없다면 모든 데이터를 불러옴
                    else {
                        teamSchedules.get(projId).add(teamScheduleDto);
                    }
                }
            }

            if(isTaskInfoRequired) {
                taskSchedules.put(projId, new ArrayList<>());
                List<Task> tasks = taskRepository.findTasksByProjIdAndId(projId, userId);
                for(Task task : tasks) {
                    LocalDateTime taskTime = task.getDate().toLocalDateTime();
                    boolean isInPeriod = taskTime.isAfter(standardDate) && taskTime.isBefore(endDate);
                    if(isInPeriod) {
                        TaskScheduleDto taskScheduleDto = new TaskScheduleDto(task);
                        taskScheduleDto.setProjName(projName);
                        taskSchedules.get(projId).add(taskScheduleDto);
                    }
                }
            }
        }
        //✅유저가 참여하는 스케줄 불러오기
        else if (isIdExistInParam) {
            String userId = dto.getUserId();

            //유저가 참여중인 모든 스케줄 정보
            List<Participants> participants = participantsRepository.findAllById(user);
            for(Participants participants1 : participants) {
                String scheId = participants1.getScheId().getScheId();
                String projId = participants1.getProjId().getProjId();
                Schedule schedule = scheduleRepository.findByScheId(scheId);
                getScheduleAboutProjId(category, standardDate, endDate, teamSchedules, projId, schedule);
            }

            //과제 불러오기
            if(isTaskInfoRequired) {
                List<Task> tasks = taskRepository.findTasksById(userId);
                for(Task task : tasks) {
                    LocalDateTime taskDate = task.getDate().toLocalDateTime();
                    boolean isInPeriod = taskDate.isAfter(standardDate) && taskDate.isBefore(endDate);
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
            System.out.println("isIdExistInParam && isProjIdExistInParam");
            String projId = dto.getProjId();
            Project project = projRepository.findById(projId).orElse(null);
            if(project == null) throw new UserNotFoundException("존재하는 프로젝트 아이디가 아닙니다");

            teamSchedules.put(projId, new ArrayList<>());
            List<Schedule> schedules = scheduleRepository.findSchedulesByProjId(project);

            for(Schedule schedule : schedules) {
                getScheduleAboutProjId(category, standardDate, endDate, teamSchedules, projId, schedule);
            }

            //과제 가져오기 - 과제는 정해진 카테고리가 없을 때만 불러오기
            if(isTaskInfoRequired) {
                taskSchedules.put(projId, new ArrayList<>());
                if(category==null) {
                    List<Task> tasks = taskRepository.findTasksByProjId(projId);

                    for(Task task : tasks) {
                        LocalDateTime taskDate = task.getDate().toLocalDateTime();
                        boolean isInPeriod = taskDate.isAfter(standardDate) && taskDate.isBefore(endDate);
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
            return new ResponseEntity<>("조회된 레코드가 없습니다", HttpStatus.NO_CONTENT);
        return new ResponseEntity<>(new CalendarScheduleDto(teamSchedules, taskSchedules), HttpStatus.OK);
    }

    private void getScheduleAboutProjId(List<String> category, LocalDateTime standardDate, LocalDateTime endDate, Map<String, List<TeamScheduleDto>> teamSchedules, String projId, Schedule schedule) {
        boolean isInPeriod = schedule.getDate().isAfter(standardDate) && schedule.getDate().isBefore(endDate);
        if(isInPeriod) {
            //만약 해당 프로젝트 아이디의 키값이 없다면 초기화
            if(!teamSchedules.containsKey(projId)) teamSchedules.put(projId, new ArrayList<>());
            //카테고리에 해당하는 데이터 필터링
            if(category != null) {
                String scheduleCategory = null;
                for(String cate : category) {
                    if(schedule.getCategory().equalsIgnoreCase(cate)) {
                        scheduleCategory = cate;
                        break;
                    }
                }
                if(scheduleCategory != null) {
                    TeamScheduleDto teamScheduleDto = new TeamScheduleDto(schedule);
                    teamSchedules.get(projId).add(teamScheduleDto);
                }
            }
            //카테고리가 없다면 모든 데이터 반환
            else {
                TeamScheduleDto teamScheduleDto = new TeamScheduleDto(schedule);
                teamSchedules.get(projId).add(teamScheduleDto);
            }
        }
    }

    /**
     * @param param - 삭제하려는 스케쥴 아이디 리스트
     * @return
     * -1 = 존재하는 스케줄이 없을 때
     * 1 = 성공적으로 삭제됐을 때
     * */
    @Transactional
    public ResponseEntity<?> deleteSchedule(ScheduleDeleteReqDto param) {

        for(String scheduleId : param.getScheId()) {
            Schedule schedule = scheduleRepository.findByScheId(scheduleId);
            if(schedule == null) throw new UserNotFoundException("존재하지 않는 스케줄 아이디입니다");
            //참가자 제거
            List<Participants> participants = participantsRepository.findParticipantsByScheId(schedule);
            participantsRepository.deleteAll(participants);
            //스케줄 삭제
            scheduleRepository.delete(schedule);
        }
        return ResultConstant.returnResult(ResultConstant.OK);
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
    public ResponseEntity<?> updateSchedule(ScheduleUpdateReqDto scheduleDto) {
        String scheId = scheduleDto.getScheId();
        Schedule schedule = scheduleRepository.findByScheId(scheId);
        if(schedule == null)
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "해당 아이디의 스케줄 레코드가 존재하지 않습니다");
        Project project = schedule.getProjId();
        if(project==null)
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 프로젝트가 아닙니다");

        //기존의 참가자 목록
        ArrayList<User> originParticipants = participantsRepository.findParticipantsUserIdByScheId(schedule);

        //참가자 목록 업데이트
        for(String userId : scheduleDto.getParticipants()) {
            User user = userRepository.findById(userId).orElse(null);
            if(user==null)
                throw new UserNotFoundException("존재하는 유저가 아닙니다");

            if(originParticipants.contains(user)) {
                originParticipants.remove(user);
            }
            else { //기존의 참여자 목록에 존재하지 않았던 유저일 경우
                if(!projMemRepository.existsByIdAndProjId(user, project))
                    throw new BadRequestException("프로젝트 구성원이 아닌 유저는 참여자로 추가할 수 없습니다");
                participantsRepository.save(new Participants(schedule, user, schedule.getProjId()));
            }
        }
        //남아 있는 originParticipants 값들은 테이블에서 지우기
        if(!originParticipants.isEmpty()) {
            for(User userId : originParticipants) {
                Participants participants = participantsRepository.findParticipantsByScheIdAndId(schedule, userId);
                participantsRepository.delete(participants);
            }
        }
        schedule.setDate(scheduleDto.getDate());
        schedule.setCategory(scheduleDto.getCategory());
        schedule.setPlace(scheduleDto.getPlace());
        schedule.setDetail(scheduleDto.getDetail());
        schedule.setScheName(scheduleDto.getScheName());
        return ResultConstant.returnResult(ResultConstant.OK);
    }
}
