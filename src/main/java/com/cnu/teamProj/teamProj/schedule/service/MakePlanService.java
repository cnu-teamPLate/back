package com.cnu.teamProj.teamProj.schedule.service;

import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.schedule.dto.MakePlanDto;
import com.cnu.teamProj.teamProj.schedule.entity.MakePlan;
import com.cnu.teamProj.teamProj.schedule.repository.MakePlanRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MakePlanService {
    MakePlanRepository makePlanRepository;
    UserRepository userRepository;
    ProjRepository projRepository;

    @Autowired
    public MakePlanService(MakePlanRepository makePlanRepository, UserRepository userRepository, ProjRepository projRepository) {
        this.makePlanRepository = makePlanRepository;
        this.userRepository = userRepository;
        this.projRepository = projRepository;
    }

    public int uploadWhen2Meet(MakePlanDto param) {
        User userInfo;
        Project projInfo;
        try {
            if(userRepository.findById(param.getUserId()).isPresent() &&
                    projRepository.findById(param.getProjId()).isPresent()) {
                userInfo = userRepository.findById(param.getUserId()).get();
                projInfo = projRepository.findById(param.getProjId()).get();
                MakePlan entity = new MakePlan();
                if(param.getEnd().isBefore(param.getStart())) return -2;
                entity.setEnd(param.getEnd());
                entity.setStart(param.getStart());
                entity.setProjId(projInfo);
                entity.setUserId(userInfo);
                entity.setUserName(userInfo.getName());
                makePlanRepository.save(entity);
                return 1;
            }
            return 0; //존재하지 않는 유저 혹은 프로젝트
        } catch (Exception e) {
            return -1; //값을 저장하는 과저에서 문제 발생
        }
    }
}
