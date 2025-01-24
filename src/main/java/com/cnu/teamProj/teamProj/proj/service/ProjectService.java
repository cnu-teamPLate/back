package com.cnu.teamProj.teamProj.proj.service;

import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import com.cnu.teamProj.teamProj.manage.repository.ClassRepository;
import com.cnu.teamProj.teamProj.proj.dto.ProjCreateDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjDto;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProjectService {
    private MemberRepository memberRepository;
    private ProjRepository projRepostiory;
    private ClassRepository classRepository;
    @Autowired
    public ProjectService(MemberRepository memberRepository, ProjRepository projRepostiory, ClassRepository classRepository) {
        this.memberRepository = memberRepository;
        this.projRepostiory = projRepostiory;
        this.classRepository = classRepository;
    }

    public List<ProjDto> findProjectsByUserId(String userId) {
        List<ProjDto> ret = new ArrayList<>();

        //userId를 통해 user가 참여중인 프로젝트 리스트 반환
        List<ProjMem> projs = memberRepository.findById(userId).stream().toList();
        for(ProjMem proj : projs) {
            String projId = proj.getProjId();
            Project project = projRepostiory.findById(projId).stream().toList().get(0);
            ret.add(new ProjDto(project.getClassId().getClassId(), project.getProjName()));
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
        project.setDate(date);
        project.setGoal(dto.getGoal());
        project.setGithub(dto.getGithub());
        project.setProjId(projId);
        project.setClassId(classInfo);
        project.setProjName(dto.getProjName());
        if(dto.getTeamName() != null) project.setTeamName(dto.getTeamName());
        projRepostiory.save(project);
        return 1;
    }
}
