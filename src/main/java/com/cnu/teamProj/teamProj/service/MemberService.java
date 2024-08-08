//package com.cnu.teamProj.teamProj.service;
//
//import com.cnu.teamProj.teamProj.entity.MemberDTO;
//import com.cnu.teamProj.teamProj.entity.Member;
//import com.cnu.teamProj.teamProj.repository.MemberRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class MemberService {
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    public void save(MemberDTO memberDTO) {
//        Member member = new Member();
//        member.setName(memberDTO.getUsername());
//        member.setMail(memberDTO.getMemberEmail());
//        member.setPwd(memberDTO.getMemberPassword());
//        member.setName(memberDTO.getMemberName());
//        memberRepository.save(member);
//    }
//
//    public MemberDTO login(MemberDTO memberDTO) {
//        Member member = memberRepository.findByUsername(memberDTO.getUsername());
//        if (member != null && member.getPwd().equals(memberDTO.getMemberPassword())) {
//            return MemberDTO.toMemberDTO(member);
//        }
//        return null;
//    }
//
//    public List<MemberDTO> findAll() {
//        return memberRepository.findAll().stream()
//                .map(MemberDTO::toMemberDTO)
//                .collect(Collectors.toList());
//    }
//
//    public MemberDTO findById(Long id) {
//        return memberRepository.findById(id)
//                .map(MemberDTO::toMemberDTO)
//                .orElse(null);
//    }
//
//    public MemberDTO updateForm(String email) {
//        Member member = memberRepository.findByMemberEmail(email);
//        return MemberDTO.toMemberDTO(member);
//    }
//
//    public void update(MemberDTO memberDTO) {
//        Member member = memberRepository.findById(memberDTO.getId()).orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));
//        member.setMail(memberDTO.getMemberEmail());
//        member.setPwd(memberDTO.getMemberPassword());
//        member.setName(memberDTO.getMemberName());
//        memberRepository.save(member);
//    }
//
//    public void deleteById(Long id) {
//        memberRepository.deleteById(id);
//    }
//
//    public String emailCheck(String email) {
//        Member member = memberRepository.findByMemberEmail(email);
//        return (member != null) ? "duplicate" : "available";
//    }
//}
