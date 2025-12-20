package com.cnu.teamProj.teamProj.security.service;

import com.cnu.teamProj.teamProj.common.ResultConstant;

import com.cnu.teamProj.teamProj.common.UserNotFoundException;
import com.cnu.teamProj.teamProj.security.SecurityConstants;
import com.cnu.teamProj.teamProj.security.dto.LoginDto;
import com.cnu.teamProj.teamProj.security.dto.RegisterDto;
import com.cnu.teamProj.teamProj.security.dto.UserInfoResponseDto;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
@Service
public class UserInfoManageService {
    //TODO test
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserInfoManageService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //내 정보 수정
    public ResponseEntity<?> updateMyInfo(RegisterDto paramUser){
        String userID = paramUser.getId();
        if(!SecurityUtil.getCurrentUser().equals(userID)) {
            return ResultConstant.returnResult(ResultConstant.NO_PERMISSION);
        }
        Optional<User> user = userRepository.findById(userID);
        if(user.isPresent()){
            User existUser = user.get();
            if(!existUser.getMail().equalsIgnoreCase(paramUser.getEmail()) && userRepository.findByMail(paramUser.getEmail()).isPresent()) {
                if(!Objects.equals(userRepository.findByMail(paramUser.getEmail()).orElseThrow().getId(), existUser.getId())) {
                    return ResultConstant.returnResultCustom(ResultConstant.ALREADY_EXIST, "이미 존재하는 이메일 입니다");
                }
            } else if(!existUser.getMail().equalsIgnoreCase(paramUser.getEmail())) existUser.setMail(paramUser.getEmail());

            if(!existUser.getPhone().equalsIgnoreCase(paramUser.getPhone())) existUser.setPhone(paramUser.getPhone());
            if(!existUser.getUsername().equalsIgnoreCase(paramUser.getName())) existUser.setUsername(paramUser.getName());

            existUser.setPwd(passwordEncoder.encode(paramUser.getPwd()));

            userRepository.save(existUser);
            return ResultConstant.returnResult(ResultConstant.OK);
        }
        return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "해당 학번의 회원이 존재하지 않습니다");
    }

    //비밀번호 수정
    public ResponseEntity<?> updatePwd(LoginDto dto) {
        String userId = dto.getId();
        boolean isSameUser = SecurityUtil.getCurrentUser().equals(userId);
        if(!isSameUser) return ResultConstant.returnResult(ResultConstant.NO_PERMISSION);

        User user = userRepository.findById(userId).orElse(null);
        if(user == null) throw new UserNotFoundException("존재하지 않는 유저 입니다");
        try{
            user.setPwd(passwordEncoder.encode(dto.getPwd()));
            userRepository.save(user);
        } catch(Exception e) {
            return ResultConstant.returnResult(ResultConstant.UNEXPECTED_ERROR);
        }
        return ResultConstant.returnResult(ResultConstant.OK);
    }

    //내 정보 보기
    public UserInfoResponseDto findMyInfo(String userId){
        User user = userRepository.findById(userId).orElse(null);
        if(user != null) return new UserInfoResponseDto(user);
        else return null;
    }
}
