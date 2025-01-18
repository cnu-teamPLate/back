package com.cnu.teamProj.teamProj.proj.service;

import com.cnu.teamProj.teamProj.proj.dto.AcceptMemberMessageDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjMemDto;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService{
    private MemberRepository memberRepository;
    private UserRepository userRepository;
    private ProjRepository projRepository;
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, UserRepository userRepository, ProjRepository projRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
        this.projRepository = projRepository;
    }

    @Override
    public List<ProjMemDto> findProjMemByProjID(String projId) {
        if(!memberRepository.existsByProjId(projId)) {
            return null;
        }//요청이 왔던 projId가 DB에 없다면 null값 반환
        List<ProjMemDto> membersInfo = new ArrayList<>();
        List<ProjMem> members = memberRepository.findProjMemsByProjId(projId).stream().toList();
        for(ProjMem mem : members) {
            User user = userRepository.findById(mem.getId()).stream().toList().get(0);
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
            if(!userRepository.existsById(projMemDto.getId())) {
                notUsers.add(projMemDto.getId());
            } else if(memberRepository.existsProjMemByIdAndProjId(projMemDto.getId(), projMemDto.getProjId())) {
                alreadyParticipants.add(projMemDto.getId());
            } else {
                ProjMem projMem = new ProjMem(projMemDto.getId(), projMemDto.getProjId());
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
            if(!userRepository.existsByMail(projMemDto.getMail())) { //mail정보를 통해 등록된 회원인지 판단
                notUsers.add(projMemDto.getMail());
            } else {
                User user = userRepository.findByMail(projMemDto.getMail()).stream().toList().get(0);
                if(memberRepository.existsProjMemByIdAndProjId(user.getId(), projMemDto.getProjId())) {
                    alreadyParticipants.add(projMemDto.getMail());
                }
                else {
                    ProjMem projMem = new ProjMem(user.getId(), projMemDto.getProjId());
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

    //메일로 인원 등록

}
