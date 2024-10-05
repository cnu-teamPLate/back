package com.cnu.teamProj.teamProj.member.service;

import com.cnu.teamProj.teamProj.member.dto.ProjMemDto;
import org.springframework.stereotype.Service;

import java.util.List;

public interface MemberService {
    List<ProjMemDto> findProjMemByProjID (String projId);
}
