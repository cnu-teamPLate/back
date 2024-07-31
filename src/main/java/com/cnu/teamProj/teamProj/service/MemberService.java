package com.cnu.teamProj.teamProj.service;

import com.cnu.teamProj.teamProj.entity.MemberDTO;
import com.cnu.teamProj.teamProj.entity.MemberEntity;
import com.cnu.teamProj.teamProj.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    public void save(MemberDTO memberDTO) {
        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(memberDTO.getUsername());
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberRepository.save(memberEntity);
    }

    public MemberDTO login(MemberDTO memberDTO) {
        MemberEntity memberEntity = memberRepository.findByUsername(memberDTO.getUsername());
        if (memberEntity != null && memberEntity.getMemberPassword().equals(memberDTO.getMemberPassword())) {
            return MemberDTO.toMemberDTO(memberEntity);
        }
        return null;
    }

    public List<MemberDTO> findAll() {
        return memberRepository.findAll().stream()
                .map(MemberDTO::toMemberDTO)
                .collect(Collectors.toList());
    }

    public MemberDTO findById(Long id) {
        return memberRepository.findById(id)
                .map(MemberDTO::toMemberDTO)
                .orElse(null);
    }

    public MemberDTO updateForm(String email) {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(email);
        return MemberDTO.toMemberDTO(memberEntity);
    }

    public void update(MemberDTO memberDTO) {
        MemberEntity memberEntity = memberRepository.findById(memberDTO.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
        memberEntity.setMemberEmail(memberDTO.getMemberEmail());
        memberEntity.setMemberPassword(memberDTO.getMemberPassword());
        memberEntity.setMemberName(memberDTO.getMemberName());
        memberRepository.save(memberEntity);
    }

    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    public String emailCheck(String email) {
        MemberEntity memberEntity = memberRepository.findByMemberEmail(email);
        return (memberEntity != null) ? "duplicate" : "available";
    }
}
