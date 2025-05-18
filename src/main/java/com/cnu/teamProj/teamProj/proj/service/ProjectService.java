package com.cnu.teamProj.teamProj.proj.service;

import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import com.cnu.teamProj.teamProj.manage.repository.ClassRepository;
import com.cnu.teamProj.teamProj.proj.dto.ProjCreateDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjUpdateDto;
import com.cnu.teamProj.teamProj.proj.dto.StudentInfoDto;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final MemberRepository memberRepository;
    private final ProjRepository projRepository;
    private final ClassRepository classRepository;
    private final UserRepository userRepository;

    public List<ProjDto> findProjectsByUserId(String userId) {
        List<ProjDto> ret = new ArrayList<>();

        //userId를 통해 user가 참여중인 프로젝트 리스트 반환
        if(!userRepository.existsById(userId)) return null;
        User user = userRepository.findById(userId).get();
        List<ProjMem> projs = memberRepository.findProjMemsById(user);
        for(ProjMem proj : projs) {
            String projId = proj.getProjId().getProjId();
            Project project = projRepository.findById(projId).stream().toList().get(0);
            //반환값에 맞게 필터링
            ProjDto newProject = new ProjDto(project.getClassId().getClassId(), project.getProjName(), project.getDate().toString(), project.getGoal(), project.getGithub(), project.getTeamName(), null);
            List<StudentInfoDto> newProjectTeamOnes = new ArrayList<>();
            //프로젝트 id로 프로젝트 정보 불러오기
            List<ProjMem> projMems = memberRepository.findProjMemsByProjId(project);
            //요청값에 맞게 변환
            for(ProjMem projMem : projMems) {
                String teamOneID  = projMem.getId().getId();
                String teamOneName = "";
                try{
                    teamOneName = userRepository.findById(teamOneID).orElseThrow().getUsername();
                } catch(Exception e) {
                    return null;
                }
                newProjectTeamOnes.add(new StudentInfoDto(teamOneID, teamOneName));
            }
            newProject.setTeamones(newProjectTeamOnes);
            ret.add(newProject);
        }
        return ret;
    }

    public int createProject(ProjCreateDto dto) {
        //입력값 체크
        if(dto==null) return 0;
        //등록된 수업이 아니라면 바로 -1반환
        if(!classRepository.existsById(dto.getClassId())) return -1;
        //날짜 형식 체크
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");
        ZonedDateTime date;
        try{
            date = ZonedDateTime.parse(dto.getDate().toString(), formatter);
        } catch(DateTimeParseException e){
            return -2;
        }


        //프로젝트 아이디 생성 및 project 테이블에 반영
        ClassInfo classInfo = classRepository.findById(dto.getClassId()).stream().toList().get(0);
        classInfo.setProjCnt(classInfo.getProjCnt() + 1);
        classRepository.save(classInfo);
        String projId = dto.getClassId()+(classInfo.getProjCnt());

        Project project = new Project();
        project.setScheCnt(0);
        project.setDate(date);
        project.setGoal(dto.getGoal());
        project.setGithub(dto.getGithub());
        project.setProjId(projId);
        project.setClassId(classInfo);
        project.setProjName(dto.getProjName());
        if(dto.getTeamName() != null) project.setTeamName(dto.getTeamName());
        projRepository.save(project);
        return 1;
    }

    public boolean updateProject(ProjUpdateDto dto) {
        Optional<Project> projectOptional=projRepository.findById(dto.getProjectId());
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();

            //수정 가능한 필드만 업데이트
            if (dto.getDate()!=null) project.setDate(dto.getDate());
            if (dto.getGoal()!=null) project.setGoal(dto.getGoal());
            if (dto.getProjName()!= null) project.setProjName(dto.getProjName());
            if (dto.getGithub()!= null) project.setGithub(dto.getGithub());
            if (dto.getTeamName()!= null) project.setTeamName(dto.getTeamName());

            projRepository.save(project);
            return true;

        }
        return false;
    }


}
