package com.cnu.teamProj.teamProj.file.service;

import static com.cnu.teamProj.teamProj.common.ResultConstant.*;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.file.dto.*;
import com.cnu.teamProj.teamProj.file.entity.Doc;
import com.cnu.teamProj.teamProj.file.entity.Docs;
import com.cnu.teamProj.teamProj.file.entity.File;
import com.cnu.teamProj.teamProj.file.repository.DocRepository;
import com.cnu.teamProj.teamProj.file.repository.DocsRepository;
import com.cnu.teamProj.teamProj.file.repository.FileRepository;
import com.cnu.teamProj.teamProj.proj.entity.Project;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
import com.cnu.teamProj.teamProj.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.concurrent.DelegatingSecurityContextExecutorService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.transform.Result;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@RequiredArgsConstructor
public class DocsService {
    private static final Logger logger = LoggerFactory.getLogger(DocsService.class);
    private final DocsRepository docsRepostiroy;
    private final S3Service s3Service;
    private final ProjRepository projRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final FileRepository fileRepository;
    private final DocRepository docRepository;
    private final PlatformTransactionManager transactionManager;

    @Value("${S3_ENDPOINT}")
    private String s3EndPoint;
    /**
     * @param dto - 파일업로드 시 사용할 기본 디티오
     * @return ResponseEntity 반환
     * */
    @Transactional
    public ResponseEntity<?> uploadFileInfoToDocs(DocsDto dto, List<MultipartFile> files) {
        if(dto == null) return returnResult(REQUIRED_PARAM_NON);
        try{
            if(!projRepository.existsById(dto.getProjId())) {
                logger.warn("존재하지 않는 프로젝트 아이디가 들어왔습니다");
                return returnResultCustom(NOT_EXIST, "프로젝트 아이디가 존재하지 않습니다");
            }
            FileDto fileResult = new FileDto();
            List<Docs> results = new ArrayList<>();
            if(dto.getUrl() == null) {
                System.out.println("안으로 들어옴");
                for(MultipartFile file : files) {
                    fileResult = s3Service.uploadFile(file, dto.getProjId());
                    
                    if(fileResult == null) return returnResultCustom(INVALID_PARAM, "파일의 이름명이 잘못되었습니다");
                    else results.add(new Docs(dto.getId(), dto.getProjId(), fileResult.getUrl(), dto.getTitle(), dto.getDetail(),  dto.getCategory(), fileResult.getFilename()));
                }
            } else {
                fileResult = new FileDto(dto.getUrl().get(0), "");
                results.add(new Docs(dto.getId(), dto.getProjId(), fileResult.getUrl(), dto.getTitle(), dto.getDetail(),  dto.getCategory(), fileResult.getFilename()));
            }

            docsRepostiroy.saveAll(results);
            return returnResult(OK);
        } catch (IOException e) {
            logger.warn("파일 등록에 실패하셨습니다");
            return returnResult(UNEXPECTED_ERROR);
        }
    }
    @Transactional
    public ResponseEntity<?> uploadFileInfoToDoc(DocsDto dto, List<MultipartFile> files) {
        if(dto == null) return returnResult(REQUIRED_PARAM_NON);
        try{
            if(!projRepository.existsById(dto.getProjId())) {
                logger.warn("존재하지 않는 프로젝트 아이디가 들어왔습니다");
                return returnResultCustom(NOT_EXIST, "프로젝트 아이디가 존재하지 않습니다");
            }
            Doc doc = new Doc(dto.getTitle(), dto.getDetail(), dto.getCategory());
            doc.setCreatedBy(userRepository.findById(dto.getId()).orElse(null));
            doc.setProjId(projRepository.findProjectByProjId(dto.getProjId()));

            docRepository.save(doc);
            FileDto fileResult = new FileDto();
            List<File> results = new ArrayList<>();
            String id = "doc:"+doc.getId();
            if(files != null && !files.isEmpty()) {
                for(MultipartFile file : files) {
                    fileResult = s3Service.uploadFile(file, dto.getProjId());
                    if(fileResult == null) return returnResultCustom(INVALID_PARAM, "파일의 이름명이 잘못되었습니다");
                    else {
                        results.add(new File(fileResult.getFilename(), fileResult.getUrl(), id));
                    }
                }
            }
            if(dto.getUrl() != null && !dto.getUrl().isEmpty()) {
                for(String url : dto.getUrl()) {
                    results.add(new File("", url, id));
                }
            }
            fileRepository.saveAll(results);
            return returnResult(OK);
        } catch(IOException e) {
            logger.warn("파일 등록에 실패했습니다");
            return returnResult(UNEXPECTED_ERROR);
        }
    }

    /**
     * 문서 삭제하는 메소드
     * @param fileId 문서 아이디
     * @return 결괏값
     */
    public int deleteFile(int fileId) {
        Docs docs = docsRepostiroy.findByFileId(fileId);
        if(docs == null) return NOT_EXIST;
        String ownerId = docs.getId();

        if(!SecurityUtil.getCurrentUser().equals(ownerId)) return NO_PERMISSION;
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
            return UNEXPECTED_ERROR;
        }
        return OK;
    }
    @Transactional
    public List<FileResponseDto> deleteAllFiles(List<Integer> files) {

        if(files == null) return null;

        SecurityContext securityContext = SecurityContextHolder.getContext();
        ExecutorService rawExecutor = Executors.newFixedThreadPool(10);


        ExecutorService executorService = new DelegatingSecurityContextExecutorService(rawExecutor, securityContext);

        List<Future<FileResponseDto>> futures = new ArrayList<>();

        for(Integer fileId : files) {
            futures.add(executorService.submit(()->{
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                return transactionTemplate.execute(status -> {
                    int ret = deleteFile(fileId);
                    String cause = "";
                    switch (ret) {
                        case NOT_EXIST -> cause = "파일 아이디가 존재하지 않습니다";
                        case NO_PERMISSION -> cause = "파일 삭제 권한이 없습니다";
                        case OK -> cause = null;
                        default -> cause = "예상치 못한 에러가 발생했습니다";
                    }
                    logger.info("fileId: {}, cause: {}", fileId, cause);
                    return new FileResponseDto(fileId, cause);
                });
            }));
        }

        List<FileResponseDto> results = new ArrayList<>();
        for(Future<FileResponseDto> future : futures) {
            try{
                results.add(future.get());
            } catch (Exception e) {
                logger.error("비동기 처리 과정에서 에러가 발생했습니다:{}", e.getMessage());
            }
        }
        return results;
    }

    public int deleteDoc(int docId) {
        Doc doc = docRepository.findById(docId).orElse(null);
        if(doc == null) return NOT_EXIST;
        String ownerId = doc.getCreatedBy().getId();
//        if(!SecurityUtil.getCurrentUser().equals(ownerId)) return NO_PERMISSION;

        //s3에서 관련 파일 지우기
        String fileId = "doc:"+docId;
        List<File> files = fileRepository.findFilesByFileType(fileId);

        try {
            for(File file : files) {
                String url = file.getUrl();
                if(url != null && url.contains(s3EndPoint)) {
                    url = url.split("://")[1];
                    String filename = url.split(s3EndPoint)[1];
                    if(!s3Service.deleteFile(filename)) {
                        throw new Exception();
                    }
                }
            }
        } catch(Exception e) {
            return UNEXPECTED_ERROR;
        }

        //DB 에서 파일 정보 지우기
        fileRepository.deleteAll(files);
        //DB 에서 문서 지우기
        docRepository.delete(doc);
        return OK;
    }

    @Transactional
    public List<FileResponseDto> deleteAllDocs(List<Integer> docs) {
        if(docs == null || docs.isEmpty()) return null;

        SecurityContext securityContext = SecurityContextHolder.getContext();
        ExecutorService rawExecutor = Executors.newFixedThreadPool(10);

        ExecutorService executorService = new DelegatingSecurityContextExecutorService(rawExecutor, securityContext);
        List<Future<FileResponseDto>> futures = new ArrayList<>();

        for(Integer docId : docs) {
            futures.add(executorService.submit(()->{
                TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
                transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
                return transactionTemplate.execute(status -> {
                    int ret = deleteDoc(docId);
                    String cause = "";
                    switch(ret) {
                        case NOT_EXIST -> cause = "파일 아이디가 존재하지 않습니다";
                        case NO_PERMISSION -> cause = "파일 삭제 권한이 없습니다";
                        case OK -> cause = null;
                        default -> cause = "예상치 못한 에러가 발생했습니다";
                    }
                    return new FileResponseDto(docId, cause);
                });
            }));
        }

        List<FileResponseDto> results = new ArrayList<>();
        for(Future<FileResponseDto> future : futures) {
            try{
                results.add(future.get());
            } catch(Exception e) {
                logger.error("비동기 처리 과정에서 에러가 발생했습니다:{}", e.getMessage());
            }
        }

        return results;
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
    public ResponseEntity<?> getDocs(Map<String, String> param) {
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
            return returnResultCustom(NOT_EXIST, "존재하는 프로젝트 혹은 유저 혹은 과제 아이디가 아닙니다.");
        }


        return new ResponseEntity<>(results, HttpStatus.OK);

    }

    public ResponseEntity<?> getDocsNew(Map<String, String> param) {
        boolean isProjectOnly = false;
        boolean isUserIdOnly = false;
        boolean isProjIdAndUserId = false;

        if(param.containsKey("projId") && param.containsKey("userId")) isProjIdAndUserId = true;
        else if(param.containsKey("projId")) isProjectOnly = true;
        else if(param.containsKey("userId")) isUserIdOnly = true;
        else return returnResult(REQUIRED_PARAM_NON);

        List<DocViewRespDto> results = new ArrayList<>();
        try{
            String projId = param.get("projId");
            String userId = param.get("userId");
            List<Doc> docs;
            if(isProjectOnly) { //프로젝트 아이디로 검색하는 경우
                Project project = projRepository.findById(projId).orElseThrow();
                docs = docRepository.findDocsByProjId(project);
            } else if(isUserIdOnly) {
                User user = userRepository.findById(userId).orElseThrow();
                docs = docRepository.findDocsByCreatedBy(user);
            } else {
                Project project = projRepository.findById(projId).orElseThrow();
                User user = userRepository.findById(userId).orElseThrow();
                docs = docRepository.findDocsByProjIdAndCreatedBy(project, user);
            }
            insertFileInDocDto(docs,results);
        } catch (NoSuchElementException e) {
            return returnResultCustom(NOT_EXIST, "존재하는 프로젝트 혹은 유저 혹은 과제 아이디가 아닙니다.");
        }

        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    private void insertFileInDocDto(List<Doc> docs, List<DocViewRespDto> results) {
        for(Doc doc : docs) {
            DocViewRespDto ret = new DocViewRespDto(doc);
            String fileType = "doc:"+doc.getId();
            List<FileDto> fileDtos = new ArrayList<>();
            List<File> files = fileRepository.findFilesByFileType(fileType);
            for(File file : files) {
                fileDtos.add(new FileDto(file));
            }
            ret.setFiles(fileDtos);
            results.add(ret);
        }
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
        if(dto == null) return REQUIRED_PARAM_NON;

        int fileId = dto.getFileId();
        Docs docs = docsRepostiroy.findByFileId(fileId);
        if(docs == null) return NOT_EXIST;

        //만약 기존 작성자와 수정하려는 사람이 다르다면 권한 부족 에러 보내기
        if(!SecurityUtil.getCurrentUser().equals(docs.getId())) {
            return NO_PERMISSION;
        }

        FileDto fileResult;
        if(file != null) {
            if(Objects.requireNonNull(file.getOriginalFilename()).length() > 20) {
                logger.error("파일명은 20자 이내여야 합니다");
                return INVALID_PARAM;
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
                return UNEXPECTED_ERROR;
            }
        } else {
            //수정된 url 이 외부 링크인데 기존의 url 값이 버킷을 가리킨다면 버킷에 들어있던 객체를 삭제해야 함.
            if(dto.getUrl() != null && !dto.getUrl().contains(s3EndPoint) && docs.getUrl().contains(s3EndPoint)) {
                String url = docs.getUrl();
                url = url.split("://")[1];
                String filename = url.split(s3EndPoint)[1];
                s3Service.deleteFile(filename);
            } else if(dto.getUrl() == null) {
                dto.setUrl(docs.getUrl());
            }
            fileResult = new FileDto(dto.getUrl(), docs.getFilename());
        }
        docs.setUrl(fileResult.getUrl());
        docs.setFilename(fileResult.getFilename());
        docs.setTitle(dto.getTitle());
        docs.setDetail(dto.getDetail());

        docsRepostiroy.save(docs);
        return OK;
    }

    @Transactional
    public int updateDoc(DocUpdateReqDto dto) {
        if(dto == null) return REQUIRED_PARAM_NON;

        int docId = dto.getId();
        String fileType = "doc:"+docId;
        Doc doc = docRepository.findById(docId).orElse(null);
        if(doc == null) return NOT_EXIST;
//        if(!SecurityUtil.getCurrentUser().equals(doc.getCreatedBy().getId())) {
//            return NO_PERMISSION;
//        }

        //기존에 있던 파일 정보
        List<File> files = fileRepository.findFilesByFileType(fileType);

        //수정 내용 반영
        doc.setTitle(dto.getTitle());
        doc.setCategory(dto.getCategory());
        doc.setDetail(dto.getDetail());

        //doc 과 관련되어 있던 파일 정보 불러오기
        logger.info("전달받은 파일데이터 개수: {}", dto.getFiles());
        if(dto.getFiles() != null && !dto.getFiles().isEmpty()) {
            try {

                //전달받은 파일에 대한 유효성 검사
                for(MultipartFile file : dto.getFiles()) {
                    if(Objects.requireNonNull(file.getOriginalFilename()).length() > 20) {
                        return INVALID_PARAM;
                    }
                }

                if(dto.getFiles() != null && !dto.getFiles().isEmpty()) {
                    //기존에 있던 MultipartFile 타입의 데이터 삭제
                    for(File file : files) {
                        String url = file.getUrl();
                        if(url.contains(s3EndPoint)) {
                            url = url.split("://")[1];
                            String filename = url.split(s3EndPoint)[1];
                            s3Service.deleteFile(filename);
                            fileRepository.delete(file);
                        }
                    }
                    //전달받은 파일 업로드
                    for(MultipartFile file : dto.getFiles()) {
                        //S3에 업로드
                        FileDto fileResult = s3Service.uploadFile(file, doc.getProjId().getProjId());
                        logger.info("파일 데이터: {}", fileResult.getFilename());
                        //파일 테이블에 업로드
                        fileRepository.save(new File(fileResult.getUrl(), fileResult.getFilename(), fileType));
                    }
                }
            } catch (Exception e) {
                return UNEXPECTED_ERROR;
            }
        }
        if(dto.getUrls() != null && !dto.getUrls().isEmpty()) {
            //기존에 DB에 저장된 외부 url 데이터 삭제
            for(File file : files) {
                String url = file.getUrl();
                if(!url.contains(s3EndPoint)) fileRepository.delete(file);
            }
            //새롭게 들어온 외부 url 저장
            for(String url : dto.getUrls()) {
                fileRepository.save(new File("", url, fileType));
            }
        }
        return OK;
    }
}
