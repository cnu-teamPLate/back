package com.cnu.teamProj.teamProj.proj.service;

import com.cnu.teamProj.teamProj.proj.dto.AcceptMemberMessageDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjMemDto;
import com.cnu.teamProj.teamProj.proj.dto.StudentInfoDto;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberServiceImpl {
    private MemberRepository memberRepository;
    private UserRepository userRepository;
    private ProjRepository projRepository;
    private final static Logger logger = LoggerFactory.getLogger(MemberServiceImpl.class);
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, UserRepository userRepository, ProjRepository projRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.projRepository = projRepository;
    }

    public List<ProjMemDto> findProjMemByProjID(String projId) {
        if(projRepository.findById(projId).isEmpty()) {
            return null;
        }
        Project project = projRepository.findById(projId).get();
        if(!memberRepository.existsByProjId(project)) {
            return null;
        }//요청이 왔던 projId가 DB에 없다면 null값 반환
        List<ProjMemDto> membersInfo = new ArrayList<>();
        List<ProjMem> members = memberRepository.findProjMemsByProjId(project).stream().toList();
        for(ProjMem mem : members) {
            User user = userRepository.findById(mem.getId().getId()).stream().toList().get(0);
            ProjMemDto memDto = new ProjMemDto(user.getId(), user.getName(), user.getMail(), user.getPhone(), null);
            membersInfo.add(memDto);
        }//
        return membersInfo;
    }

    //학번으로 인원 등록
    public AcceptMemberMessageDto acceptNewMember(List<ProjMemDto> dtos) {
        if(!projRepository.existsById(dtos.get(0).getProjId())) return null;
        ArrayList<String> alreadyParticipants = new ArrayList<>();
        ArrayList<String> notUsers = new ArrayList<>();
        ArrayList<String> successMem = new ArrayList<>();
        for(ProjMemDto projMemDto : dtos) {
            User user = userRepository.findById(projMemDto.getId()).get();
            Project project = projRepository.findById(projMemDto.getProjId()).get();
            if(!userRepository.existsById(projMemDto.getId())) {
                notUsers.add(projMemDto.getId());
            } else if(memberRepository.existsProjMemByIdAndProjId(user, project)) {
                alreadyParticipants.add(projMemDto.getId());
            } else {
                if(userRepository.findById(projMemDto.getId()).isEmpty() || projRepository.findById(projMemDto.getProjId()).isEmpty()) {
                    logger.error("유저 혹은 프로젝트가 존재하지 않습니다");
                    return null;
                }
                ProjMem projMem = new ProjMem(user, project);
                successMem.add(projMemDto.getId());
                memberRepository.save(projMem);
            }
        }
        AcceptMemberMessageDto messageDto = new AcceptMemberMessageDto(alreadyParticipants, notUsers, successMem);
        return messageDto;
    }

    //이메일로 인원 등록
    public AcceptMemberMessageDto acceptNewMemberByMail(List<ProjMemDto> dtos) {
        if(!projRepository.existsById(dtos.get(0).getProjId())) return null;
        ArrayList<String> alreadyParticipants = new ArrayList<>();
        ArrayList<String> notUsers = new ArrayList<>();
        ArrayList<String> successMem = new ArrayList<>();
        for(ProjMemDto projMemDto : dtos) {
            User user = userRepository.findByMail(projMemDto.getMail()).stream().toList().get(0);
            Project project = projRepository.findById(projMemDto.getProjId()).get();

            if(!userRepository.existsByMail(projMemDto.getMail())) { //mail정보를 통해 등록된 회원인지 판단
                notUsers.add(projMemDto.getMail());
            } else {
                if(memberRepository.existsProjMemByIdAndProjId(user, project)) {
                    alreadyParticipants.add(projMemDto.getMail());
                }
                else {
                    ProjMem projMem = new ProjMem(user, project);
                    successMem.add(projMemDto.getMail());
                    memberRepository.save(projMem);
                }
            }
            /*
            else if(memberRepository.existsProjMemByIdAndProjId(projMemDto.getId(), projMemDto.getProjId())) {
                alreadyParticipants.add(projMemDto.getId());
            } else {
                ProjMem projMem = new ProjMem(projMemDto.getId(), projMemDto.getProjId());
                successMem.add(projMemDto.getId());
                memberRepository.save(projMem);
            }

             */
        }
        AcceptMemberMessageDto messageDto = new AcceptMemberMessageDto(alreadyParticipants, notUsers, successMem);
        return messageDto;
    }

    //멤버 삭제
    @Transactional
    public boolean deleteMemberByUserAndProj(String userId, String projId) {
        if(!userRepository.existsById(userId) || !projRepository.existsById(projId)) {
            return false;
        }

        User user = userRepository.findById(userId).get();
        Project project = projRepository.findById(projId).get();
        // 사용자와 프로젝트 ID로 멤버 존재 여부 확인
        if (memberRepository.existsByIdAndProjId(user, project)) {
            memberRepository.deleteByIdAndProjId(user, project);
            return true;
        }
        return false;
    }

    public List<StudentInfoDto> findUserBySearch(String query) {
        if(query.trim().isEmpty()) return null;
        logger.info("query: {}", query);//test

        List<User> users = new ArrayList<>();
        if (query.matches("\\d+")) {
            logger.info("숫자로 인식됨");
            query += "%";
            users = userRepository.findUsersById(query);
            logger.info("users: {}", users.size());
        } else {
            logger.info("문자로 인식됨");
            query += "%";
            users = userRepository.findUsersByName(query);
        }

        if(users.isEmpty()) return null;
        List<StudentInfoDto> results = new ArrayList<>();
        for(User user : users) {
            results.add(new StudentInfoDto(user.getId(), user.getName()));
        }
        return results;
    }

}


