package com.cnu.teamProj.teamProj.schedule.controller;

import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.schedule.dto.*;
import com.cnu.teamProj.teamProj.schedule.entity.MeetingLog;
import com.cnu.teamProj.teamProj.schedule.service.MakePlanService;
import com.cnu.teamProj.teamProj.schedule.service.MeetingService;
import com.cnu.teamProj.teamProj.security.dto.AuthResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value ="/schedule/meeting", produces = "application/json; charset=utf8")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "MeetController", description = "회의 및 시간 조정과 관련된 API")
public class MeetController {
    private final MakePlanService makePlanService;
    private final MeetingService meetingService;
    private final ObjectMapper objectMapper;

    @PostMapping("/upload/when2meet")
    @Operation(summary = "when2meet 폼 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 프로젝트 아이디입니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "종료시간(혹은 종료일)이 시작시간(혹은 시작일)보다 앞설 수 없습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> uploadWhen2meet(@RequestBody When2meetRequestDto param) {
        try {
            return makePlanService.uploadWhen2meet(param);
        } catch(Exception e) {
            return ResultConstant.returnResultCustom(ResultConstant.INVALID_PARAM, "종료일이 시작일보다 앞설 수 없습니다");
        }
    }

    @PostMapping("/upload/when2meet/detail")
    @Operation(summary = "생성된 웬투밋 폼에 각 유저가 값을 업로드")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하는 프로젝트 아이디 혹은 유저가 아닙니다"),
            @ApiResponse(responseCode = "400", description = "입력 오류", content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponse.class),
                    examples = {
                            @ExampleObject(name = "필수값 누락", value = "{\"message\": \"응답에 필요한 필수 요청 값이 전달되지 않았습니다\"}"),
                            @ExampleObject(name = "시작일, 종료일 오류", value = "{\"message\": \"시작값과 종료값의 날짜가 같아야 합니다\"}"),
                            @ExampleObject(name = "시작시간, 종료시간 오류", value = "{\"message\": \"종료시간이 시작시간보다 앞설 수 없습니다\"}")
                    }
            ))
    })
    public ResponseEntity<?> uploadWhen2meetDetails(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody When2meetDetailRequestDto param) {
        try {
            return makePlanService.uploadWhen2meetDetail(param);
        } catch(IllegalArgumentException e) {
            return ResultConstant.returnResultCustom(ResultConstant.INVALID_PARAM, e.getMessage());
        }
    }

    @Operation(summary = "웬투밋 폼 정보 보여주기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = When2meetViewRespDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하는 웬투밋 폼이 없습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/view/when2meet")
    public ResponseEntity<?> getWhen2meetInfo(
            @Parameter(name = "when2meetId", example = "1")
            @RequestParam(value = "when2meetId") int when2meetId
    ) {
        return makePlanService.getWhen2meetInfo(when2meetId);
    }

    @Operation(summary = "프로젝트에 등록된 웬투밋 폼 정보 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = When2meetListDto.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트 아이디가 존재하지 않습니다",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @GetMapping("/view/when2meet-list")
    public ResponseEntity<?> getWhen2meetList(
            @Parameter(name = "projId", example = "cse00001")
            @RequestParam(value="projId") String projId
    ) {
        List<When2meetListDto> ret = makePlanService.getWhen2meetList(projId);
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    @GetMapping("/view/log")
    @Operation(summary = "회의록 불러오기", description = "필요에 따라 스케줄 아이디 또는 프로젝트 아이디를 요청값으로 넘겨주면 됨.<br/>둘 중 하나는 있어야 함.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200(1)", description = "프로젝트 아이디에 대한 반환값이 리스트 형태로 반환됨", content = @Content(schema = @Schema(implementation = MeetingListDto.class))),
            @ApiResponse(responseCode = "200(2)", description = "스케줄 아이디에 대한 자세한 회의록 정보가 반환됨", content = @Content(schema = @Schema(implementation = MeetingLogDto.class))),
            @ApiResponse(responseCode = "400", description = "응답에 필요한 필수 요청 값이 전달되지 않았습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "프로젝트 아이디나 스케줄 아이디가 존재하지 않음", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    @Parameters({
            @Parameter(name = "scheId", description = "스케줄 레코드 아이디", example = "cse00001_2"),
            @Parameter(name = "projId", description = "프로젝트 아이디", example = "cse00001")
    })
    public ResponseEntity<?> getMeetingLog(@RequestParam(value = "scheId", required = false) String scheId, @RequestParam(value = "projId", required = false) String projId) {
        Map<String, String> param = new HashMap<>();
        param.put("scheId", scheId);
        param.put("projId", projId);
        return meetingService.getMeetingLog(param);
    }

    @PostMapping(value = "/upload/log", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "회의록 올리기", description = "스웨거 테스트 시 녹음 파일이 없다면 'Send empty value' 체크 해제해서 보내기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하는 유저 or 프로젝트 or 스케줄 아이디가 아닙니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> uploadMeetingLog(
            @ModelAttribute ScheduleUploadReqDto dto
    ) throws JsonProcessingException {
        MeetingLogDto param = objectMapper.readValue(dto.getParam(), MeetingLogDto.class);
        return meetingService.updateMeetingLog(param, dto.getFile());
    }

    @PostMapping(value = "/convert-speech", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "녹음파일을 텍스트로 변환해주는 api", description = "⚠️아직은 wave, x-flac 형식만 가능합니다 <br/>-> m4a, mp3 타입도 받을 수 있게 방법을 모색중입니다..<br/>테스트를 위한 변환 사이트: https://cloudconvert.com/m4a-to-flac , https://cloudconvert.com/m4a-to-flac")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"message\": \"인코딩 중 문제가 발생했습니다\"}"))),
            @ApiResponse(responseCode = "400", description = "요청 변수 관련 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = {
                    @ExampleObject(name = "오디오 형식 오류", value = "{\"message\": \"지원되지 않는 오디오 형식입니다 (지원되는 오디오 형식: wave, x-flac)\"}"),
                    @ExampleObject(name = "필수값 누락", value = "{\"message\": \"파일 데이터가 없습니다\"}")
            })),
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class),examples = @ExampleObject(value = "{\"result\": \"변환된 텍스트 파일\"}")))
    })
    public ResponseEntity<?> convertSpeechToText(@ModelAttribute RecordRequestDto params) {
        return meetingService.convertSpeechToText(params.getRecord());
    }
}
