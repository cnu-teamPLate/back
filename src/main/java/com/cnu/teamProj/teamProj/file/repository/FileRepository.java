package com.cnu.teamProj.teamProj.file.repository;

import com.cnu.teamProj.teamProj.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Integer> {
    List<File> findFilesByFileType(String fileType);
    File findByFileType(String filetType);
}
