package com.cnu.teamProj.teamProj.member.controller;

import com.cnu.teamProj.teamProj.member.dto.ProjMemDto;
import com.cnu.teamProj.teamProj.member.repository.MemberRepository;
import com.cnu.teamProj.teamProj.member.service.MemberServiceImpl;
import com.cnu.teamProj.teamProj.security.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/teamProj/member")
@Tag(name = "멤버 관리", description = "멤버 조회와 관련된 코드")
public class MemberController {

    private MemberServiceImpl memberService;
    @Autowired
    public MemberController(MemberServiceImpl memberService) {
        this.memberService = memberService;
    }

    //특정 프로젝트 선택 시 해당 프로젝트의 팀원 불러오는 api
    @GetMapping("/project")
    @Operation(summary = "프로젝트에 참여중인 멤버", description = "특정 프로젝트에 참여중인 멤버 리스트를 반환")
    public List<ProjMemDto> getProjMem(@RequestParam Map map) {
        return memberService.findProjMemByProjID(String.valueOf(map.get("projId")));
    }

}
