package com.cnu.teamProj.teamProj.cls.service;

import com.cnu.teamProj.teamProj.cls.dto.ClassInfoDto;
import com.cnu.teamProj.teamProj.cls.entity.ClassInfo;
import com.cnu.teamProj.teamProj.cls.repository.ClassRepository;
import com.cnu.teamProj.teamProj.security.jwt.JWTAuthenticationFilter;
import org.apache.commons.logging.LogFactory;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class ClassService {

    private ClassRepository classRepository;
    @Autowired
    public ClassService(ClassRepository classRepository) {
        this.classRepository = classRepository;
    }

    public boolean enrollClass(ClassInfoDto dto) {
        if(classRepository.existsById(dto.getClassId())) return false;
        ClassInfo classInfo = new ClassInfo(dto.getClassId(), dto.getClassName(), dto.getProfessor(), 0);
        try{
            classRepository.save(classInfo);
            return true;
        } catch(SqlScriptException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
