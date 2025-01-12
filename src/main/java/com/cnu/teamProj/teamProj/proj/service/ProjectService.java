package com.cnu.teamProj.teamProj.proj.service;

import com.cnu.teamProj.teamProj.proj.dto.ProjDto;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectService {
    private MemberRepository memberRepository;
    private ProjRepository projRepostiory;
    @Autowired
    public ProjectService(MemberRepository memberRepository, ProjRepository projRepostiory) {
        this.memberRepository = memberRepository;
        this.projRepostiory = projRepostiory;
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
}
