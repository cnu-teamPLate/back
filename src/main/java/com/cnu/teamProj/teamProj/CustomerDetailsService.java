package com.cnu.teamProj.teamProj;

import com.cnu.teamProj.teamProj.entity.MemberEntity;
import com.cnu.teamProj.teamProj.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MemberEntity member = memberRepository.findByUsername(username);
        if (member == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return User.withUsername(member.getMemberEmail())
                .password(member.getMemberPassword())
                .roles(member.getRole())
                .build();
    }
}
