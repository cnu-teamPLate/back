package com.cnu.teamProj.teamProj.security.controller;

import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.security.dto.AuthResponseDto;
import com.cnu.teamProj.teamProj.security.entity.Role;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.jwt.JWTGenerator;
import com.cnu.teamProj.teamProj.security.repository.RoleRepository;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.security.dto.LoginDto;
import com.cnu.teamProj.teamProj.security.dto.RegisterDto;
import com.cnu.teamProj.teamProj.security.service.AuthService;
import com.cnu.teamProj.teamProj.security.service.UserInfoManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping(value = "/auth", produces = "application/json; charset = utf-8")
@Tag(name = "회원 등록", description = "회원 등록 및 회원 가입")
@Slf4j
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTGenerator jwtGenerator;
    private final UserInfoManageService userInfoManageService;
    private final AuthService authService;

    @PostMapping("/login") 
    @Operation(summary = "로그인", description = "로그인 api")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto, HttpServletResponse response){
        try {
            UsernamePasswordAuthenticationToken tempToken = new UsernamePasswordAuthenticationToken(
                    String.valueOf(loginDto.getId()), loginDto.getPwd());
            log.info("tempToken.getCredentials(): {}", tempToken.getCredentials());
            log.info("tempToken.getPrincipal(): {}", tempToken.getPrincipal());
            //authentication = 현재 인증 정보
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getId(), loginDto.getPwd()));
            log.info("authentication.toString(): {}", authentication.toString());
            //SecurityContextHolder.getContext() : 현재 사용자의 security context를 가져옴
            //요청이 들어왔을 때 해당 요청을 처리하는 스레드에 인증 정보를 유지함.
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtGenerator.generateToken(authentication);
            log.info("토큰 정보: {}", token);

            /*
            public void saveTokenInCookie(String token, HttpServletResponse response) {
        Cookie cookie = new Cookie("User-Token", token);
        cookie.setHttpOnly(true);
        int expirationTime = 1000 * 60 * 60; //1시간
        cookie.setMaxAge(expirationTime);
        cookie.setPath("/");
        cookie.setSecure(true);
        response.addCookie(cookie);
    }
            * */
            Cookie cookie = new Cookie("User-Token", token);
            cookie.setHttpOnly(true);
            int expirationTime = 1000 * 60 * 60; // 쿠키 만료시간 : 1시간
            cookie.setMaxAge(expirationTime);
            cookie.setPath("/");
            cookie.setSecure(true);
            response.addCookie(cookie);


            return new ResponseEntity<>(new AuthResponseDto(token, loginDto.getId(), userRepository.findById(loginDto.getId()).toString()), HttpStatus.OK); //유저 아이디(학번), 이름 필수로 넘겨야 함
        } catch (Exception e){
            log.info("로그인 실패 : {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "등록하기", description = "회원 가입 api")
    @Parameters({
            @Parameter(name="name", description = "유저 이름"),
            @Parameter(name="pwd", description = "비밀번호"),
            @Parameter(name = "mail", description = "메일주소"),
            @Parameter(name="phone", description = "핸드폰번호"),
            @Parameter(name="id", description = "학번")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "이미 존재하는 회원"),
            @ApiResponse(responseCode = "200", description = "회원가입 완료")
    })
    public ResponseEntity<String> register(@RequestBody RegisterDto registerDto){
        int ret = authService.registerUser(registerDto);
        return ResultConstant.returnResult(ret);
    }

    //내 정보 수정
    @PutMapping(value = "/update-my-info", produces = "application/json; charset=utf8")
    @Operation(summary = "내 정보 수정", description = "수정되지 않은 정보까지 모두 전달하면, 전달된 학번으로 유저를 조회한 후 수정된 정보를 업데이트 (학번은 변경 불가)")
    @Parameters({
            @Parameter(name="name", description = "유저 이름"),
            @Parameter(name="pwd", description = "비밀번호"),
            @Parameter(name = "mail", description = "메일주소"),
            @Parameter(name="phone", description = "핸드폰번호"),
            @Parameter(name="id", description = "학번")
    })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "존재하지 않는 사용자입니다"),
            @ApiResponse(responseCode = "200", description = "정보가 정상적으로 수정되었습니다")
    })
    public ResponseEntity<String> updateMyInfo(@RequestBody RegisterDto dto) {
        int result = userInfoManageService.updateMyInfo(dto);
        return ResultConstant.returnResult(result);
    }

    //내 정보 보기
    @GetMapping("/read-my-info")
    @Operation(summary = "내 정보 조회", description = "인자로 전달된 학번을 토대로 조회된 정보 반환, 조회된 정보가 없다면 null값 반환")
    @Parameter(name = "userId", description = "학번")
    public User readMyInfo(@RequestParam Map map){
        return userInfoManageService.findMyInfo(map.get("userId").toString());
    }

    //회원탈퇴
    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "요청한 사용자를 회원탈퇴시킵니다.<br/>테스트 시 토큰 값도 같이 전달해줘야 함.")
    public ResponseEntity<String> withdraw(HttpServletRequest request, HttpServletResponse response) {
        return authService.withDraw(request, response);
    }

    //비밀번호 찾기
    @PostMapping("/send-password-mail")
    @Operation(summary = "비밀번호 찾기", description = "메일을 보내면 해당 메일로 임시 비밀번호를 발급함")
    @Parameter(name = "email", example = "esybd02@naver.com")
    public ResponseEntity<String> sendRandomPassword(@RequestBody Map<String,String> userMail) {
        String email = userMail.get("email");
        return authService.sendRandomPassword(email);
    }
}
