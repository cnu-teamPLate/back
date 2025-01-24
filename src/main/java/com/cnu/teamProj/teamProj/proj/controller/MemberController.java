package com.cnu.teamProj.teamProj.proj.controller;

import com.cnu.teamProj.teamProj.proj.dto.AcceptMemberMessageDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjMemDto;
import com.cnu.teamProj.teamProj.proj.service.MemberServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teamProj/member")
@Tag(name = "멤버 관리", description = "멤버 조회와 관련된 코드")
public class MemberController {
    //test
    private MemberServiceImpl memberService;
    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    //특정 프로젝트 선택 시 해당 프로젝트의 팀원 불러오는 api
    @GetMapping("/project/{projId}")
    @Operation(summary = "프로젝트에 참여중인 멤버", description = "특정 프로젝트에 참여중인 멤버 리스트를 반환")
    public List<ProjMemDto> getProjMem(@PathVariable(value = "projId") String projId) {
        return memberService.findProjMemByProjID(projId);
    }

    //프로젝트에 팀원 생성하기
    @PostMapping("/post")
    public AcceptMemberMessageDto postProjMem(@RequestBody List<ProjMemDto> members) {
        if(members.get(0).getMail() == null) return memberService.acceptNewMember(members);
        return memberService.acceptNewMemberByMail(members);
    }

    //멤버 삭제하기//RequestParam, map 으로 받기//userId, projId map.key
    //@DeleteMapping("/{memberId}")
    //@Operation(summary = "멤버 삭제", description="특정 멤버를 삭제")
    //public ResponseEntity<String> deleteMember(@PathVariable(value="memberId") String memberId) {
      //  boolean isDeleted=memberService.deleteMemberById(memberId);
       // if (isDeleted) {
         //   return ResponseEntity.ok("멤버 삭제 성공");
        //} else {
          //  return ResponseEntity.status(404).body("멤버를 찾을 수 없습니다.");
        //}
    //}

    @DeleteMapping("/delete")
    @Operation(summary = "멤버 삭제", description = "userId와 projId를 기반으로 특정 멤버를 삭제")
    public ResponseEntity<String> deleteMember(@RequestParam Map<String, String> params) {
        String userId = params.get("userId");
        String projId = params.get("projId");

        if (userId == null || projId == null) {
            return ResponseEntity.badRequest().body("userId와 projId를 모두 제공해야 합니다.");
        }

        boolean isDeleted = memberService.deleteMemberByUserAndProj(userId, projId);
        if (isDeleted) {
            return ResponseEntity.ok("멤버 삭제 성공");
        } else {
            return ResponseEntity.status(404).body("멤버를 찾을 수 없습니다.");
        }
    }




}
