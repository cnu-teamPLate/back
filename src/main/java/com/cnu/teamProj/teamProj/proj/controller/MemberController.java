package com.cnu.teamProj.teamProj.proj.controller;

import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.proj.dto.AcceptMemberMessageDto;
import com.cnu.teamProj.teamProj.proj.dto.ProjMemDto;
import com.cnu.teamProj.teamProj.proj.dto.StudentInfoDto;
import com.cnu.teamProj.teamProj.proj.dto.UserProjMapDto;
import com.cnu.teamProj.teamProj.proj.service.MemberServiceImpl;
import io.lettuce.core.dynamic.annotation.Param;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/member", produces = "application/json; charset=utf-8;")
@Tag(name = "MemberController", description = "프로젝트 멤버 관리와 관련된 API")
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
    @Parameter(name = "projId", description = "url의 {projId}부분에 프로젝트 아이디 값을 넣어 요청을 보내야 함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404 NOT_FOUND", description = "존재하지 않는 프로젝트임"),
            @ApiResponse(responseCode = "200 OK", description = "프로젝트에 참여중인 멤버들의 정보에 대한 리스트가 반환됨")
    })
    public ResponseEntity<List<ProjMemDto>> getProjMem(@PathVariable(value = "projId") String projId) {
        List<ProjMemDto> responseVal = memberService.findProjMemByProjID(projId);
        if(responseVal==null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(responseVal, HttpStatus.OK);
    }

    //프로젝트에 팀원 생성하기
    @PostMapping("/post")
    @Operation(summary = "프로젝트에 새로운 멤버 추가하기", description = "id, mail 중 하나는 꼭 값이 들어가야 함")
    @Parameters(value = {
            @Parameter(name = "id", description = "학번 정보", example = "20211079", allowEmptyValue = true),
            @Parameter(name = "mail", description = "메일 정보", example = "aaaa@aaaa", allowEmptyValue = true),
            @Parameter(name = "projId", description = "프로젝트 아이디 = 수업 코드 + 등록된 플젝 개수", example = "cse00001")
    })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "alreadyExists = 이미 프로젝트 회원인 멤버의 리스트, notUsers = 서비스의 회원이 아닌 멤버의 리스트, successMembers = 등록 성공한 멤버의 리스트")
    })
    public AcceptMemberMessageDto postProjMem(@RequestBody List<ProjMemDto> members) {
        if(members.get(0).getMail() == null) return memberService.acceptNewMember(members);
        return memberService.acceptNewMemberByMail(members);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "멤버 삭제", description = "userId와 projId를 기반으로 특정 멤버를 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "응답에 필요한 필수 요청 값이 전달되지 않았습니다.",  content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "멤버를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> deleteMember(
            @io.swagger.v3.oas.annotations.parameters.RequestBody
            @RequestBody UserProjMapDto params) {
        String userId = params.getUserId();
        String projId = params.getProjId();

        if (userId == null || projId == null) {
            return ResultConstant.returnResult(ResultConstant.INVALID_PARAM);
        }

        boolean isDeleted = memberService.deleteMemberByUserAndProj(userId, projId);
        if (isDeleted) {
            return ResultConstant.returnResult(ResultConstant.OK);
        } else {
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "멤버를 찾을 수 없습니다.");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "유저 검색", description = "유저 아이디 혹은 이름으로 검색")
    @Parameter(name = "query", description = "검색어. <br/>/member/search?query=20 형태로 요청 보내면 됨.", example = "20")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "NOT_FOUND", description = "검색된 데이터가 없을 때"),
            @ApiResponse(responseCode = "OK", description = "검색된 데이터 값과 함께 반환!!")
    })
    public ResponseEntity<List<StudentInfoDto>> findUserBySearch(@RequestParam(value = "query") String query) {
        List<StudentInfoDto> ret = memberService.findUserBySearch(query);
        if(ret == null) return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

}
