package com.cnu.teamProj.teamProj.file.repository;

import com.cnu.teamProj.teamProj.file.entity.Docs;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;

public interface DocsRepository extends JpaRepository<Docs, String> {
    ArrayList<Docs> findAllById(String userId);
    ArrayList<Docs> findAllByProjId(String projId);
    ArrayList<Docs> findAllByCategory(int category);
    boolean existsDocsByProjIdAndFilename(String projId, String filename);
    ArrayList<Docs> findDocsByProjIdAndFilename(String projId, String filename);
    Docs findByUrl(String url);
    Docs findByFileId(int fileId);
}
