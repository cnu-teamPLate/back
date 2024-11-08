package com.cnu.teamProj.teamProj.member.service;

import com.cnu.teamProj.teamProj.member.dto.ProjMemDto;
import com.cnu.teamProj.teamProj.member.entity.ProjMem;
import com.cnu.teamProj.teamProj.member.repository.MemberRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MemberServiceImpl implements MemberService{
    private MemberRepository memberRepository;
    private UserRepository userRepository;
    @Autowired
    public MemberServiceImpl(MemberRepository memberRepository, UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<ProjMemDto> findProjMemByProjID(String projId) {
        if(!memberRepository.existsById(projId)) return null; //요청이 왔던 projId가 DB에 없다면 null값 반환
        List<ProjMemDto> membersInfo = new ArrayList<>();
        List<ProjMem> members = memberRepository.findById(projId).stream().toList();
        for(ProjMem mem : members) {
            User user = userRepository.findById(mem.getId()).stream().toList().get(0);
            ProjMemDto memDto = new ProjMemDto(user.getId(), user.getName(), user.getMail(), user.getPhone());
            membersInfo.add(memDto);
        }
        return membersInfo;
    }

}
