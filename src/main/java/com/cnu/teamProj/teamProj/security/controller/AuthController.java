package com.cnu.teamProj.teamProj.security.controller;

import com.cnu.teamProj.teamProj.security.dto.AuthResponseDto;
import com.cnu.teamProj.teamProj.security.entity.Role;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.jwt.JWTGenerator;
import com.cnu.teamProj.teamProj.security.repository.RoleRepository;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.security.dto.LoginDto;
import com.cnu.teamProj.teamProj.security.dto.RegisterDto;
import com.cnu.teamProj.teamProj.security.service.UserInfoManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/teamProj/auth")
@Tag(name = "회원 등록", description = "회원 등록 및 회원 가입")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JWTGenerator jwtGenerator;
    private UserInfoManageService userInfoManageService;

    //로그
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JWTGenerator jwtGenerator,
                          UserInfoManageService userInfoManageService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
        this.userInfoManageService = userInfoManageService;
    }

    @PostMapping("/login")
    @Operation(summary = "로그인", description = "로그인 api")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
        try {
            UsernamePasswordAuthenticationToken tempToken = new UsernamePasswordAuthenticationToken(
                    String.valueOf(loginDto.getId()), loginDto.getPwd());
            logger.info("tempToken.getCredentials(): {}", tempToken.getCredentials());
            logger.info("tempToken.getPrincipal(): {}", tempToken.getPrincipal());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getId(), loginDto.getPwd()));
            logger.info("authentication.toString(): {}", authentication.toString());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            System.out.println("token = " + token);
            return new ResponseEntity<>(new AuthResponseDto(token, loginDto.getId(), userRepository.findById(loginDto.getId()).toString()), HttpStatus.OK); //유저 아이디(학번), 이름 필수로 넘겨야 함
        } catch (Exception e){
            logger.info("로그인 실패 : {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "등록하기", description = "회원 가입 api")
//    @Parameters({
//            @Parameter(name="name", description = "유저 이름"),
//            @Parameter(name="pwd", description = "비밀번호"),
//            @Parameter(name = "mail", description = "메일주소"),
//            @Parameter(name="phone", description = "핸드폰번호"),
//            @Parameter(name="id", description = "학번")
//    })
//    @ApiResponses(value = {
//            @ApiResponse(code = 400, message = "이미 존재하는 회원"),
//            @ApiResponse(code = 200, message = "회원가입 완료")
//    })
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        if(userRepository.existsById(registerDto.getId())){
            return new ResponseEntity<>("Username is taken!", HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setName(registerDto.getName());
        user.setPwd(passwordEncoder.encode(registerDto.getPwd()));
        user.setMail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setId(registerDto.getStudentNumber());
        Role roles = roleRepository.findByName("USER").get();
        user.setRoles(Collections.singletonList(roles));
        user.setUsername(registerDto.getId());
        userRepository.save(user);

        return new ResponseEntity<>("User registered success", HttpStatus.OK);
    }

    //내 정보 수정
    @PutMapping("/update-my-info")
//    @Operation(summary = "내 정보 수정", description = "수정되지 않은 정보까지 모두 전달하면, 전달된 학번으로 유저를 조회한 후 수정된 정보를 업데이트 (학번은 변경 불가)")
//    @Parameters({
//            @Parameter(name="name", description = "유저 이름"),
//            @Parameter(name="pwd", description = "비밀번호"),
//            @Parameter(name = "mail", description = "메일주소"),
//            @Parameter(name="phone", description = "핸드폰번호"),
//            @Parameter(name="id", description = "학번")
//    })
    public ResponseEntity<String> updateMyInfo(@RequestBody RegisterDto dto) {
        boolean result = userInfoManageService.updateMyInfo(dto);
        if(result) return new ResponseEntity<>("정보가 정상적으로 수정되었습니다", HttpStatus.OK);
        return new ResponseEntity<>("존재하지 않는 사용자입니다", HttpStatus.BAD_REQUEST);
    }

    //내 정보 보기
    @GetMapping("/read-my-info")
//    @Operation(summary = "내 정보 조회", description = "인자로 전달된 학번을 토대로 조회된 정보 반환, 조회된 정보가 없다면 null값 반환")
//    @Parameter(name = "userId", description = "학번")
    public User readMyInfo(@RequestParam Map map){
        return userInfoManageService.findMyInfo(map.get("userId").toString());
    }
}
