package com.cnu.teamProj.teamProj.security.service;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.security.dto.RegisterDto;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;
@Service
public class UserInfoManageService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserInfoManageService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //내 정보 수정
    public int updateMyInfo(RegisterDto paramUser){
        String userID = paramUser.getStudentNumber();
        Optional<User> user = userRepository.findById(userID);
        if(user.isPresent()){
            User existUser = user.get();
            if(userRepository.findByMail(paramUser.getEmail()).isPresent()) {
                if(!Objects.equals(userRepository.findByMail(paramUser.getEmail()).orElseThrow().getId(), existUser.getId())) {
                    return ResultConstant.ALREADY_EXIST;
                }
            }
            existUser.setName(paramUser.getName());
            existUser.setMail(paramUser.getEmail());
            existUser.setPhone(paramUser.getPhone());

            existUser.setPwd(passwordEncoder.encode(paramUser.getPwd()));
            existUser.setUsername(paramUser.getId());
            userRepository.save(existUser);
            return ResultConstant.OK;
        }
        return ResultConstant.NOT_EXIST;
    }

    //내 정보 보기
    public User findMyInfo(String userId){
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }
}
