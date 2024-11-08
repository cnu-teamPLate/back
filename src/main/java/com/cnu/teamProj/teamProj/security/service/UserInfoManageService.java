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
        String userID = paramUser.getId();
        Optional<User> user = userRepository.findById(userID);
        if(user.isPresent()){
            User existUser = user.get();
            existUser.setName(existUser.getName());
            existUser.setMail(existUser.getMail());
            existUser.setPhone(existUser.getPhone());
            existUser.setPwd(existUser.getPwd());
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
