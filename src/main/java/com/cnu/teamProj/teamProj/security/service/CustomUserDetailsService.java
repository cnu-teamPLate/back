package com.cnu.teamProj.teamProj.security.service;


import com.cnu.teamProj.teamProj.security.entity.Role;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()->new UsernameNotFoundException("Username not found"));
        return new org.springframework.security.core.userdetails.User(String.valueOf(user.getId()), user.getPwd(),mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<GrantedAuthority> mapRolesToAuthorities(List<Role> roles){
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName())) //각 권한을 GrantedAuthority 객체로 변환
                .collect(Collectors.toList());
    }


}
