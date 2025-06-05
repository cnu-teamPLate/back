package com.cnu.teamProj.teamProj.file.controller;

import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.file.dto.*;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "DocsController", description = "문서와 관련된 API")
@RequestMapping(value = "/file" , produces = "application/json; charset=utf8")
@Slf4j
public class DocsController {
    private DocsService docsService;

    public DocsController(DocsService docsService) {
        this.docsService = docsService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "파일 업로드", description = "파일 업로드를 할 때 사용되는 메소드입니다")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "입력 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(name = "파일 이름 오류", value = "{\"message\": \"파일의 이름명이 잘못되었습니다\"}"),
                                    @ExampleObject(name = "필수값 누락", value = "{\"message\": \"응답에 필요한 필수 요청 값이 전달되지 않았습니다\"}")
                            }
                    )
            ),
            @ApiResponse(responseCode = "404", description = "프로젝트 아이디가 존재하지 않습니다"),
            @ApiResponse(responseCode = "200", description = "성공적으로 저장되었습니다"),
            @ApiResponse(responseCode = "500", description = "예상치 못한 문제가 발생했습니다")
    })
    public ResponseEntity<?> uploadFile(@ModelAttribute DocsUploadRequestDto dto) {
        DocsDto docsDto = new DocsDto(dto);
        return docsService.uploadFileInfoToDoc(docsDto, dto.getFiles());
//        return docsService.uploadFileInfoToDocs(docsDto, dto.getFiles());
    }


    @Operation(summary = "문서 삭제", description = "파일 삭제 시 사용하는 api로, 작성자 본인만 삭제가 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 요청 값 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"results\": \"null\"}"))),
            @ApiResponse(responseCode = "500", description = "비동기 처리 중 에러 발생", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"results\": \"[]\"}"))),
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllFilesResponseDto.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody FileDeleteRequestDto dto) {
//        List<FileResponseDto> ret = docsService.deleteAllFiles(dto.getFiles());
        List<FileResponseDto> ret = docsService.deleteAllDocs(dto.getDocs());
        AllFilesResponseDto retToJson = new AllFilesResponseDto();
        retToJson.setResults(ret);
        if(ret == null) return new ResponseEntity<>(retToJson, HttpStatus.BAD_REQUEST);
        if(ret.isEmpty()) return new ResponseEntity<>(retToJson, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(retToJson, HttpStatus.OK);
    }

    @Operation(summary = "문서 불러오기", description = "➡️ 프로젝트에 등록된 파일을 모두 불러오고 싶다면 -> projId값<br/>➡️ 특정 유저가 등록한 파일을 불러오고 싶다면 -> userId값<br/>➡️ 프로젝트에 등록된 파일 중 특정 유저의 파일을 불러오고 싶다면 -> projId값과 userId모두")
    @GetMapping("/view")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = DocsViewResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "필수 요청 값 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "파일 이름 오류", value = "{\"message\": \"필수 요청 값이 존재하지 않습니다\"}"))),
            @ApiResponse(responseCode = "404", description = "존재하는 아이디가 아님", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class), examples = @ExampleObject(name = "파일 이름 오류", value = "{\"message\": \"존재하는 프로젝트 혹은 유저 혹은 과제 아이디가 아닙니다.\"}")))
    })
    public ResponseEntity<?> loadFile(
            @Parameter(name = "projId", description = "프로젝트 아이디", example = "cse00001") @RequestParam(value = "projId", required = false) String projId,
            @Parameter(name = "userId", description = "학번 정보", example = "01111111") @RequestParam(value = "userId", required = false) String userId
    ) {
        Map<String, String> map = new HashMap<>();
        if(projId == null && userId == null) return ResultConstant.returnResultCustom(ResultConstant.INVALID_PARAM, "필수 요청 값이 존재하지 않습니다");
        if(projId != null) map.put("projId", projId);
        if(userId != null) map.put("userId", userId);
        return docsService.getDocsNew(map);
    }

    @Operation(summary = "문서 수정", description = "문서를 수정할 때 사용되는 api 입니다")
    @PutMapping(value = "/put", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "문서 수정은 작성자만 가능", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"message\": \"요청 권한이 없습니다\"}"))),
            @ApiResponse(responseCode = "400", description = "필수 파라미터가 없거나 파일명이 20자를 넘어감", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"message\": \"응답에 필요한 필수 요청 값이 전달되지 않았습니다.\"}"))),
            @ApiResponse(responseCode = "404", description = "전달된 파일 아이디값이 존재하지 않음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"message\": \"해당 아이디의 값이 없습니다.\"}"))),
            @ApiResponse(responseCode = "500", description = "서버 에러", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"message\": \"예상치 못한 오류가 발생했습니다.\"}"))),
            @ApiResponse(responseCode = "200", description = "성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseDto.class),examples = @ExampleObject(value = "{\"message\": \"요청이 성공적으로 처리되었습니다\"}")))

    })
    public ResponseEntity<?> updateFile(@ModelAttribute DocUpdateReqDto dto) {
        log.info("컨트롤러에서 받는 데이터 : {}", dto.getFiles());
        int ret = docsService.updateDoc(dto);
        return ResultConstant.returnResult(ret);
    }
}
