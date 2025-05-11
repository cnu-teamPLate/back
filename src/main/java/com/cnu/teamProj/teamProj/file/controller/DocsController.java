package com.cnu.teamProj.teamProj.file.controller;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.file.dto.DocsDto;
import com.cnu.teamProj.teamProj.file.dto.DocsPutDto;
import com.cnu.teamProj.teamProj.file.dto.DocsViewResponseDto;
import com.cnu.teamProj.teamProj.file.service.DocsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@Tag(name = "DocsController", description = "파일과 관련된 API")
@RequestMapping(value = "/file" , produces = "application/json; charset=utf8")
public class DocsController {
    private DocsService docsService;

    public DocsController(DocsService docsService) {
        this.docsService = docsService;
    }

    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "파일 업로드를 할 때 사용되는 메소드입니다")
    @Parameters(value = {
            @Parameter(name = "docs", description = "문서와 관련된 모든 정보", example =
                    "{\n" +
                    "\t\"id\" : \"20241121\",\n" +
                    "\t\"projId\" : \"cse00001\",\n" +
                    "\t\"title\" : \"펭귄에 대하여\",\n" +
                    "\t\"detail\" : \"펭귄에 대해 자료조사한 내용입니다\",\n" +
                    "\t\"uploadDate\" : \"2025-01-14T00:02:27.000Z\",\n" +
                    "\t\"category\" : \"task1\"\n" +
                    "}"),
            @Parameter(name = "file", description = "파일 데이터를 MultipartFile 형식으로 받음")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204 NO_CONTENT", description = "입력값이 들어오지 않았습니다"),
            @ApiResponse(responseCode = "500 INTERNAL_SERVER_ERROR", description = "파일 등록에 실패했습니다"),
            @ApiResponse(responseCode = "404 NOT_FOUND", description = "프로젝트 아이디가 존재하지 않습니다"),
            @ApiResponse(responseCode = "200 OK", description = "성공적으로 저장되었습니다"),
            @ApiResponse(responseCode = "400 BAD_REQUEST", description = "예상치 못한 문제가 발생했습니다")
    })

    public ResponseEntity<String> uploadFile(
            @RequestPart(value = "docs", required = false) DocsDto docsDto,
            @RequestPart(value = "file", required = false)MultipartFile file) {
        int ret = docsService.uploadFileInfoToDocs(docsDto, file);
        return switch (ret) {
            case 0 -> new ResponseEntity<>("입력값이 들어오지 않았습니다", HttpStatus.NO_CONTENT);
            case -1 -> new ResponseEntity<>("파일 등록에 실패했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
            case -2 -> new ResponseEntity<>("프로젝트 아이디가 존재하지 않습니다", HttpStatus.NOT_FOUND);
            case 1 -> new ResponseEntity<>("성공적으로 저장되었습니다", HttpStatus.OK);
            case -5 -> new ResponseEntity<>("파일의 이름명이 잘못되었습니다", HttpStatus.BAD_REQUEST);
            default -> new ResponseEntity<>("예상치 못한 문제가 발생했습니다", HttpStatus.BAD_REQUEST);
        };
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFile(@RequestBody Map<String, Integer> map) {
        int ret = docsService.deleteFile(map.get("fileId"));
        HttpStatus status;
        String resultText;
        if(ret == ResultConstant.NOT_EXIST) {
            status = HttpStatus.NOT_FOUND;
            resultText = "존재하지 않는 문서입니다";
        } else if(ret == ResultConstant.UNEXPECTED_ERROR) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            resultText = "예상치 못한 오류가 발생했습니다";
        } else if (ret == ResultConstant.NO_PERMISSION) {
            status = HttpStatus.NOT_ACCEPTABLE;
            resultText = "파일에 대한 권한이 없습니다";
        }else {
            status = HttpStatus.OK;
            resultText = "파일이 성공적으로 삭제되었습니다";
        }
        return new ResponseEntity<>(resultText, status);
    }

    @GetMapping("/view")
    public ResponseEntity<List<DocsViewResponseDto>> loadFile(@RequestParam Map<String, String> map) {
        return docsService.getDocs(map);
    }

    @PutMapping("/put")
    public ResponseEntity<String> updateFile(
            @RequestPart(value = "docs", required = false) DocsPutDto docsDto,
            @RequestPart(value = "file", required = false)MultipartFile file) {
        int ret = docsService.updateDocs(docsDto, file);
        return ResultConstant.returnResult(ret);
    }
}
