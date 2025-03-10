package com.cnu.teamProj.teamProj.file.service;

import com.cnu.teamProj.teamProj.file.dto.DocsDto;
import com.cnu.teamProj.teamProj.file.entity.Docs;
import com.cnu.teamProj.teamProj.file.repository.DocsRepostiroy;
import com.cnu.teamProj.teamProj.proj.repository.ProjRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class DocsService {
    private static final Logger logger = LoggerFactory.getLogger(DocsService.class);
    private DocsRepostiroy docsRepostiroy;
    private S3Service s3Service;
    private ProjRepository projRepository;

    public DocsService(DocsRepostiroy docsRepostiroy, S3Service s3Service, ProjRepository projRepository) {
        this.docsRepostiroy = docsRepostiroy;
        this.s3Service = s3Service;
        this.projRepository = projRepository;
    }
    /**
     * @param dto - 파일업로드 시 사용할 기본 디티오
     * @return
     *      - 입력 값이 null 이면 0 반환
     *      - 파일 등록에 실패 시 -1 반환
     *      - 존재하지 않는 프로젝트일 경우 -2 반환
     *      - 성공 시 1 반환
     * */
    public int uploadFileInfoToDocs(DocsDto dto, MultipartFile file) {
        if(dto == null) return 0;
        try{
            if(!projRepository.existsById(dto.getProjId())) {
                logger.warn("존재하지 않는 프로젝트 아이디가 들어왔습니다");
                return -3;
            }
            String url = s3Service.uploadFile(file, dto.getProjId());
            Docs docs = new Docs(dto.getId(), dto.getProjId(), url, dto.getTitle(), dto.getDetail(), dto.getUploadDate(), dto.getCategory());
            docsRepostiroy.save(docs);
            return 1;
        } catch (IOException e) {
            logger.warn("파일 등록에 실패하셨습니다");
            return -1;
        }
    }
}
