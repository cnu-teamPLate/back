package com.cnu.teamProj.teamProj.proj.service;

import com.cnu.teamProj.teamProj.proj.dto.ProjMemDto;

import java.util.List;

public interface MemberService {
    List<ProjMemDto> findProjMemByProjID (String projId);
}
