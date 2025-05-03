package com.cnu.teamProj.teamProj.file.repository;

import com.cnu.teamProj.teamProj.file.entity.Docs;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public interface DocsRepository extends JpaRepository<Docs, String> {
    ArrayList<Docs> findAllById(String userId);
}
