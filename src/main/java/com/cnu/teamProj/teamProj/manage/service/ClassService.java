package com.cnu.teamProj.teamProj.manage.service;

import com.cnu.teamProj.teamProj.manage.dto.ClassInfoDto;
import com.cnu.teamProj.teamProj.manage.entity.ClassInfo;
import com.cnu.teamProj.teamProj.manage.repository.ClassRepository;
import org.hibernate.tool.schema.spi.SqlScriptException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
