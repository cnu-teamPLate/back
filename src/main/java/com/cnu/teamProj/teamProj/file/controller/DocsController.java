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
        return docsService.uploadFileInfoToDocs(docsDto, dto.getFiles());
    }


    @Operation(summary = "문서 삭제", description = "파일 삭제 시 사용하는 api로, 작성자 본인만 삭제가 가능")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "필수 요청 값 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"results\": \"null\"}"))),
            @ApiResponse(responseCode = "500", description = "비동기 처리 중 에러 발생", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class),examples = @ExampleObject(value = "{\"results\": \"[]\"}"))),
            @ApiResponse(responseCode = "200", description = "요청 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = AllFilesResponseDto.class)))
    })
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteFile(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody FileDeleteRequestDto dto) {
        List<FileResponseDto> ret = docsService.deleteAllFiles(dto.getFiles());
        AllFilesResponseDto retToJson = new AllFilesResponseDto();
        retToJson.setResults(ret);
        if(ret == null) return new ResponseEntity<>(retToJson, HttpStatus.BAD_REQUEST);
        if(ret.isEmpty()) return new ResponseEntity<>(retToJson, HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(retToJson, HttpStatus.OK);
    }

    @Operation(summary = "문서 불러오기", description = "프로젝트에 등록된 파일을 모두 불러오고 싶다면 -> projId값<br/>특정 유저가 등록한 파일을 불러오고 싶다면 -> userId값<br/>프로젝트에 등록된 파일 중 특정 유저의 파일을 불러오고 싶다면 -> projId값과 userId모두<br/>특정 과제에 등록된 파일을 불러오고 싶다면 -> taskId값<br/>을 넘겨야 합니다.")
    @Parameters(value = {
            @Parameter(name = "projId", example = "cse00001",required = true, description = "프로젝트에 "),
            @Parameter(name = "userId", example = "01111111",required = true, description = "프로젝트 아이디 값 혹은 유저 아이디 값 중 하나는 필수로 들어가야 합니다"),
            @Parameter(name = "taskId", example = "4", description = "파일 아이디는 필요한 경우에만 지정해주면 됩니다.")

    })
    @GetMapping("/view")
    public ResponseEntity<List<DocsViewResponseDto>> loadFile(@RequestParam Map<String, String> map) {
        return docsService.getDocs(map);
    }

    @Operation(summary = "문서 수정", description = "문서를 수정할 때 사용되는 api 입니다")
    @Parameters(value = {
            @Parameter(name = "docs", description = "문서와 관련된 모든 정보", example =
                    "{\n" +
                            "\t\"title\" : \"펭귄에 대하여\",\n" +
                            "\t\"detail\" : \"펭귄에 대해 자료조사한 내용입니다\",\n" +
                            "\t\"fileId\" : \"13\",\n" +
                            "\t\"url\" : \"https://yozm.wishket.com/magazine/detail/2955/\"\n" +
                            "}"),
            @Parameter(name = "file", description = "파일 데이터를 MultipartFile 형식으로 받음")
    })
    @PutMapping("/put")
    public ResponseEntity<?> updateFile(
            @RequestPart(value = "docs", required = false) DocsPutDto docsDto,
            @RequestPart(value = "file", required = false)MultipartFile file) {
        int ret = docsService.updateDocs(docsDto, file);
        return ResultConstant.returnResult(ret);
    }
}
