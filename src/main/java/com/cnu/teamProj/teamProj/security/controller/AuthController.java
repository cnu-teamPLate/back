package com.cnu.teamProj.teamProj.security.controller;

import com.cnu.teamProj.teamProj.security.dto.AuthResponseDto;
import com.cnu.teamProj.teamProj.security.entity.Role;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.jwt.JWTGenerator;
import com.cnu.teamProj.teamProj.security.repository.RoleRepository;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.security.dto.LoginDto;
import com.cnu.teamProj.teamProj.security.dto.RegisterDto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/teamProj/auth")
@ApiOperation(value="회원 등록", notes = "회원 등록 및 회원 가입")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JWTGenerator jwtGenerator) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        try {
            UsernamePasswordAuthenticationToken tempToken = new UsernamePasswordAuthenticationToken(
                    String.valueOf(loginDto.getId()), loginDto.getPwd());
            log.info("tempToken.getCredentials(): {}", tempToken.getCredentials());
            log.info("tempToken.getPrincipal(): {}", tempToken.getPrincipal());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getId(), loginDto.getPwd()));
            log.info("authentication.toString(): {}", authentication.toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            System.out.println("token = " + token);
            return new ResponseEntity<>(new AuthResponseDto(token), HttpStatus.OK);
        } catch (Exception e){
            e.printStackTrace();
            //log.error("Authentication failed: {}", e.);
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userRepository.existsById(registerDto.getId())){
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setPwd(passwordEncoder.encode(registerDto.getPwd()));
        user.setMail(registerDto.getMail());
        user.setPhone(registerDto.getPhone());
        user.setId(registerDto.getId());
        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));

        userRepository.save(user);

        return new ResponseEntity<>("User registered success", HttpStatus.OK);
    }
}
