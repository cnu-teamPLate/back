package com.cnu.teamProj.teamProj.security.service;

import com.cnu.teamProj.teamProj.security.dto.RegisterDto;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class UserInfoManageService {
    UserRepository userRepository;

    @Autowired
    public UserInfoManageService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //내 정보 수정
    public boolean updateMyInfo(RegisterDto paramUser){
        String userID = paramUser.getStudentNumber();
        Optional<User> user = userRepository.findById(userID);
        if(user.isPresent()){
            User existUser = user.get();
            existUser.setName(paramUser.getName());
            existUser.setMail(paramUser.getEmail());
            existUser.setPhone(paramUser.getPhone());
            existUser.setPwd(paramUser.getPwd());
            existUser.setUsername(paramUser.getId());
            userRepository.save(existUser);
            return true;
        }
        return false;
    }

    //내 정보 보기
    public User findMyInfo(String userId){
        Optional<User> user = userRepository.findById(userId);
        return user.orElse(null);
    }
}
