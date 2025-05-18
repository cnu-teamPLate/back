package com.cnu.teamProj.teamProj.file.service;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.file.dto.DocsDto;
import com.cnu.teamProj.teamProj.file.dto.DocsPutDto;
import com.cnu.teamProj.teamProj.file.dto.DocsViewResponseDto;
import com.cnu.teamProj.teamProj.file.dto.FileDto;
import com.cnu.teamProj.teamProj.file.entity.Docs;
import com.cnu.teamProj.teamProj.file.repository.DocsRepository;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
import com.cnu.teamProj.teamProj.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocsService {
    private static final Logger logger = LoggerFactory.getLogger(DocsService.class);
    private final DocsRepository docsRepostiroy;
    private final S3Service s3Service;
    private final ProjRepository projRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    @Value("${S3_ENDPOINT}")
    private String s3EndPoint;
    /**
     * @param dto - 파일업로드 시 사용할 기본 디티오
     * @return
     *      - 입력 값이 null 이면 0 반환
     *      - 파일 등록에 실패 시 -1 반환
     *      - 존재하지 않는 프로젝트일 경우 -2 반환
     *      - 성공 시 1 반환
     * */
    @Transactional
    public int uploadFileInfoToDocs(DocsDto dto, List<MultipartFile> files) {
        if(dto == null) return 0;
        try{
            if(!projRepository.existsById(dto.getProjId())) {
                logger.warn("존재하지 않는 프로젝트 아이디가 들어왔습니다");
                return -3;
            }
            FileDto fileResult = new FileDto();
            List<Docs> results = new ArrayList<>();
            if(dto.getUrl() == null) {
                for(MultipartFile file : files) {
                    fileResult = s3Service.uploadFile(file, dto.getProjId());
                    if(fileResult == null) return -5;
                    else results.add(new Docs(dto.getId(), dto.getProjId(), fileResult.getUrl(), dto.getTitle(), dto.getDetail(),  dto.getCategory(), fileResult.getFilename()));
                }
            } else {
                fileResult = new FileDto(dto.getUrl(), "");
                results.add(new Docs(dto.getId(), dto.getProjId(), fileResult.getUrl(), dto.getTitle(), dto.getDetail(),  dto.getCategory(), fileResult.getFilename()));
            }

            docsRepostiroy.saveAll(results);
            return 1;
        } catch (IOException e) {
            logger.warn("파일 등록에 실패하셨습니다");
            return -1;
        }
    }

    /**
     * 문서 삭제하는 메소드
     * @param fileId 문서 아이디
     * @return 결괏값
     */
    @Transactional
    public int deleteFile(int fileId) {
        Docs docs = docsRepostiroy.findByFileId(fileId);
        if(docs == null) return ResultConstant.NOT_EXIST;
        String ownerId = docs.getId();

        if(!SecurityUtil.getCurrentUser().equals(ownerId)) return ResultConstant.NO_PERMISSION;
        docsRepostiroy.delete(docs);
        String url = docs.getUrl();
        //문서가 버켓에 올라가 있는 파일일 경우 삭제
        try{
            if(url != null && url.contains(s3EndPoint)) {
                url = url.split("://")[1];
                String filename = url.split(s3EndPoint)[1];
                if(!s3Service.deleteFile(filename)) {
                    throw new Exception();
                }
            }
        } catch(Exception e) {
            return ResultConstant.UNEXPECTED_ERROR;
        }
        return ResultConstant.OK;
    }

    /**
     * 응답값에 추가 정보 넣어주기
     * - 문서 불러오기 메소드 호출 시 중복되는 코드 메소드로 뺌.
     * @param temp 리스트에 넣어줄 값
     * @param projName 프로젝트 이름
     * @param userId 유저명을 불러오기 위한 유저 아이디
     */
    private void insertInfoToDocsViewResponseDto(DocsViewResponseDto temp, String projName, String userId) {
        temp.setProjName(projName);
        //응답값에 넣어줄 유저명
        String userName = userRepository.findById(userId).orElseThrow().getUsername();
        temp.setUserName(userName);
        //응답값에 넣어줄 과제명
        if(temp.getCategory() != -1) {
            logger.info("받은 카테고리 값: {}", temp.getCategory());
            String taskName = taskRepository.findTaskByTaskId(temp.getCategory()).getTaskName();
            temp.setTaskName(taskName);
        }
    }

    /**
     * 문서 불러오기
     * @param param
     *  - taskId : 과제 pk 값 (nullable)
     *  - projId : 프로젝트 아이디 (nullable)
     *  - userId : 작성자 아이디 (nullable)
     *  이 세 개 값 중 하나는 있어야 함
     * @return 조건에 맞는 문서 정보 리스트
     */
    public ResponseEntity<List<DocsViewResponseDto>> getDocs(Map<String, String> param) {
        boolean isProjectOnly = false;
        boolean isUserIdOnly = false;
        boolean isProjIdAndUserId = false;
        boolean isTaskId = false;
        if(param.containsKey("projId") && param.containsKey("userId")) isProjIdAndUserId = true;
        else if(param.containsKey("projId")) isProjectOnly = true;
        else if(param.containsKey("userId")) isUserIdOnly = true;
        else if(param.containsKey("taskId")) isTaskId = true;

        List<DocsViewResponseDto> results = new ArrayList<>();
        try{
            if(isProjectOnly) {
                String projId = param.get("projId");
                //응답값에 넣어줄 프로젝트명
                String projName = projRepository.findById(projId).orElseThrow().getProjName();
                List<Docs> docsList = docsRepostiroy.findAllByProjId(projId);
                for(Docs docs : docsList) {
                    DocsViewResponseDto temp = new DocsViewResponseDto(docs);
                    insertInfoToDocsViewResponseDto(temp, projName, docs.getId());
                    results.add(temp);
                }
            } else if(isUserIdOnly) {
                String userId = param.get("userId");
                //응답값에 넣어줄 유저명
                String userName = userRepository.findById(userId).orElseThrow().getUsername();
                List<Docs> docsList = docsRepostiroy.findAllById(userId);
                for(Docs docs : docsList) {
                    DocsViewResponseDto temp = new DocsViewResponseDto(docs);
                    temp.setUserName(userName);
                    //응답값에 넣어줄 프로젝트명
                    String projId = docs.getProjId();
                    String projName = projRepository.findById(projId).orElseThrow().getProjName();
                    temp.setProjName(projName);
                    //응답값에 넣어줄 과제명
                    if(docs.getCategory() == -1) {
                        String taskName = taskRepository.findTaskByTaskId(docs.getCategory()).getTaskName();
                        temp.setTaskName(taskName);
                    }
                    results.add(temp);
                }
            } else if(isProjIdAndUserId) {
                String projId = param.get("projId");
                //응답값에 넣어줄 프로젝트명
                String projName = projRepository.findById(projId).orElseThrow().getProjName();
                List<Docs> docsList = docsRepostiroy.findAllByProjId(projId);
                for(Docs docs : docsList) {
                    if(docs.getId().equals(param.get("userId"))) {
                        DocsViewResponseDto temp = new DocsViewResponseDto(docs);
                        insertInfoToDocsViewResponseDto(temp, projName, docs.getId());
                        results.add(temp);
                    }
                }
            } else if(isTaskId) {
                int taskId = Integer.parseInt(param.get("taskId"));
                //응답값에 넣어줄 과제 명
                String taskName = taskRepository.findTaskByTaskId(taskId).getTaskName();
                List<Docs> docsList = docsRepostiroy.findAllByCategory(taskId);
                for(Docs docs : docsList) {
                    DocsViewResponseDto temp = new DocsViewResponseDto(docs);
                    temp.setTaskName(taskName);
                    //응답값에 넣어줄 유저 명
                    String userName = userRepository.findById(docs.getId()).orElseThrow().getUsername();
                    temp.setUserName(userName);
                    //응답값에 넣어줄 프로젝트 명
                    String projName = projRepository.findProjectByProjId(docs.getProjId()).getProjName();
                    temp.setProjName(projName);
                    results.add(temp);
                }
            }
        } catch(NoSuchElementException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }


        return new ResponseEntity<>(results, HttpStatus.OK);

    }

    /**
     * @param dto - 파일업로드 시 사용할 기본 디티오
     * @return
     *      - 입력 값이 null 이면 0 반환
     *      - 파일 등록에 실패 시 -1 반환
     *      - 존재하지 않는 프로젝트일 경우 -2 반환
     *      - 성공 시 1 반환
     * */
    @Transactional
    public int updateDocs(DocsPutDto dto, MultipartFile file) {
        if(dto == null) return ResultConstant.REQUIRED_PARAM_NON;

        int fileId = dto.getFileId();
        Docs docs = docsRepostiroy.findByFileId(fileId);
        if(docs == null) return ResultConstant.NOT_EXIST;

        //만약 기존 작성자와 수정하려는 사람이 다르다면 권한 부족 에러 보내기
        if(!SecurityUtil.getCurrentUser().equals(docs.getId())) {
            return ResultConstant.NO_PERMISSION;
        }

        FileDto fileResult;
        if(file != null) {
            if(Objects.requireNonNull(file.getOriginalFilename()).length() > 20) {
                logger.error("파일명은 20자 이내여야 합니다");
                return ResultConstant.INVALID_PARAM;
            }
            try{
                //만약 업로드한 파일을 수정하는 것이라면 기존의 파일을 삭제해야 함
                if(!docs.getFilename().trim().isEmpty()) {
                    String url = docs.getUrl();
                    url = url.split("://")[1];
                    String filename = url.split(s3EndPoint)[1];
                    s3Service.deleteFile(filename);
                }
                fileResult = s3Service.uploadFile(file, docs.getProjId());
            } catch(Exception e) {
                return ResultConstant.UNEXPECTED_ERROR;
            }
        } else {
            //수정된 url 이 외부 링크인데 기존의 url 값이 버킷을 가리킨다면 버킷에 들어있던 객체를 삭제해야 함.
            if(dto.getUrl() != null && !dto.getUrl().contains(s3EndPoint) && docs.getUrl().contains(s3EndPoint)) {
                String url = docs.getUrl();
                url = url.split("://")[1];
                String filename = url.split(s3EndPoint)[1];
                s3Service.deleteFile(filename);
            }
            fileResult = new FileDto(dto.getUrl(), docs.getFilename());
        }
        docs.setUrl(fileResult.getUrl());
        docs.setFilename(fileResult.getFilename());
        docs.setTitle(dto.getTitle());
        docs.setDetail(dto.getDetail());

        docsRepostiroy.save(docs);
        return ResultConstant.OK;
    }
}
