package com.cnu.teamProj.teamProj.schedule.service;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.common.UserNotFoundException;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.schedule.dto.*;
import com.cnu.teamProj.teamProj.schedule.entity.When2meet;
import com.cnu.teamProj.teamProj.schedule.entity.When2meetDate;
import com.cnu.teamProj.teamProj.schedule.entity.When2meetDetails;
import com.cnu.teamProj.teamProj.schedule.repository.When2meetDateRepo;
import com.cnu.teamProj.teamProj.schedule.repository.When2meetDetailsRepo;
import com.cnu.teamProj.teamProj.schedule.repository.When2meetRepo;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MakePlanService {
    private final UserRepository userRepository;
    private final ProjRepository projRepository;
    private final When2meetRepo when2meetRepo;
    private final When2meetDateRepo when2meetDateRepo;
    private final When2meetDetailsRepo when2meetDetailsRepo;

    @Transactional
    public ResponseEntity<?> uploadWhen2meet(When2meetRequestDto dto) {
        //유효성 검사
        Project projId = projRepository.findProjectByProjId(dto.getProjId());
        if(projId == null) return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하지 않는 프로젝트 아이디입니다");
        if(dto.getDates() == null || dto.getDates().isEmpty() || dto.getTitle() == null || dto.getEndTime() == null || dto.getStartTime() == null) return ResultConstant.returnResult(ResultConstant.REQUIRED_PARAM_NON);
        if(dto.getStartTime().isAfter(dto.getEndTime())) return ResultConstant.returnResultCustom(ResultConstant.INVALID_PARAM, "종료시간이 시작시간보다 앞설 수 없습니다");
        //웬투밋 생성
        When2meet when2meet = new When2meet(dto);
        when2meet.setProjId(projId);
        when2meetRepo.save(when2meet);
        //생성된 웬투밋의 날짜 나열
        for(TimeRangeDto time : dto.getDates()) {
            if(time.getStartDate().isAfter(time.getEndDate())) {
                throw new IllegalArgumentException("endDate가 startDate보다 앞설 수 없습니다");
            }
            when2meetDateRepo.save(new When2meetDate(time.getStartDate(), time.getEndDate(), when2meet));
        }
        return ResultConstant.returnResult(ResultConstant.OK);
    }

    @Transactional
    public ResponseEntity<?> uploadWhen2meetDetail(When2meetDetailRequestDto dto) {
        When2meet when2meet = when2meetRepo.findById(dto.getWhen2meetId()).orElse(null);
        if(when2meet == null) return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 프로젝트 아이디가 아닙니다");
        if(dto.getDetails() == null || dto.getDetails().isEmpty()) return ResultConstant.returnResult(ResultConstant.REQUIRED_PARAM_NON);

        for(When2meetDetailDto detail : dto.getDetails()) {
            User userId = userRepository.findById(detail.getUserId()).orElse(null);
            if(userId == null) throw new UserNotFoundException("존재하는 유저 아이디가 아닙니다");
            for(DateTimeRangeDto range : detail.getDates()) {
                LocalDateTime start = range.getStartDate();
                LocalDateTime end = range.getEndDate();
                if(!start.toLocalDate().equals(end.toLocalDate())) throw new IllegalArgumentException("시작값과 종료값의 날짜가 같아야 합니다");
                if(start.toLocalTime().isAfter(end.toLocalTime())) throw new IllegalArgumentException("종료시간이 시작시간보다 앞설 수 없습니다");
                when2meetDetailsRepo.save(new When2meetDetails(start, end, userId, when2meet));
            }
        }
        return ResultConstant.returnResult(ResultConstant.OK);
    }

    @Transactional
    public ResponseEntity<?> getWhen2meetInfo(int when2meetId) {
        When2meet when2meet = when2meetRepo.findById(when2meetId).orElse(null);
        if(when2meet == null) return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 웬투밋 폼이 없습니다");

        //웬투밋 폼에 대한 정보 저장
        When2meetRequestDto form = new When2meetRequestDto(when2meet);
        form.setDates(new ArrayList<>());
        List<When2meetDate> dates = when2meetDateRepo.findWhen2meetDatesByWhen2meetId(when2meet);

        for(When2meetDate date : dates){
            form.getDates().add(new TimeRangeDto(date.getStartDate(), date.getEndDate()));
        }

        //웬투밋 폼에 등록한 내용 저장
        List<When2meetDetails> details = when2meetDetailsRepo.findAllByWhen2meetId(when2meet);
        Map<LocalDate, List<When2meetDetailRespDto>> schedules = new HashMap<>();
        for(When2meetDetails detail : details) {
            LocalDateTime start = detail.getStartDate();
            LocalDateTime end = detail.getEndDate();
            User user = detail.getUserId();
            LocalDate key = start.toLocalDate();
            if(!schedules.containsKey(key)) schedules.put(key, new ArrayList<>());
            schedules.get(key).add(new When2meetDetailRespDto(start.toLocalTime(), end.toLocalTime(), user.getId(), user.getUsername()));
        }

        When2meetViewRespDto resp = new When2meetViewRespDto(form, schedules);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    public List<When2meetListDto> getWhen2meetList(String projID) {
        Project project = projRepository.findProjectByProjId(projID);
        if(project == null) {
            throw new UserNotFoundException("프로젝트 아이디가 존재하지 않습니다");
        }
        List<When2meet> when2meets = when2meetRepo.findWhen2meetsByProjId(project);
        List<When2meetListDto> ret = new ArrayList<>();
        for(When2meet meet : when2meets) {
            //when2meet의 구체적인 날짜 정보 가져오기
            List<TimeRangeDto> times = new ArrayList<>(); //반환값에 넣어줄 데이터
            List<When2meetDate> dates = when2meetDateRepo.findWhen2meetDatesByWhen2meetId(meet);
            for(When2meetDate date : dates) {
                times.add(new TimeRangeDto(date.getStartDate(), date.getEndDate()));
            }
            //when2meet 폼 정보 저장
            ret.add(new When2meetListDto(meet, times, meet.getId()));
        }
        return ret;
    }

}
