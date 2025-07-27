package com.cnu.teamProj.teamProj.security.controller;

import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.security.dto.*;
import com.cnu.teamProj.teamProj.security.entity.Role;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.jwt.JWTGenerator;
import com.cnu.teamProj.teamProj.security.repository.RoleRepository;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.security.service.AuthService;
import com.cnu.teamProj.teamProj.security.service.UserInfoManageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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

import javax.xml.transform.Result;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = "application/json; charset = utf-8")
@Tag(name = "인증/인가", description = "회원 등록 및 회원 가입")
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = AuthResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인 정보가 일치하지 않습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "로그인 시 요청값")
            @RequestBody LoginDto loginDto,
            HttpServletResponse response){
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

            Cookie cookie = new Cookie("User-Token", token);
            cookie.setHttpOnly(true);
            int expirationTime = 1000 * 60 * 60; // 쿠키 만료시간 : 1시간
            cookie.setMaxAge(expirationTime);
            cookie.setPath("/");
            cookie.setSecure(true);
            response.addCookie(cookie);


            return new ResponseEntity<>( new AuthResponseDto(token, loginDto.getId(), userRepository.findById(loginDto.getId()).get().getUsername()), HttpStatus.OK); //유저 아이디(학번), 이름 필수로 넘겨야 함
        } catch (Exception e){
            return ResultConstant.returnResultCustom(ResultConstant.NO_PERMISSION, "로그인 정보가 일치하지 않습니다");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "등록하기", description = "회원 가입 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "이미 값이 존재합니다.(이미 존재하는 아이디 값 혹은 메일 정보입니다.)", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "회원가입 완료", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "등록된 역할 정보가 없습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody
            @RequestBody RegisterDto registerDto){
        return authService.registerUser(registerDto);
    }

    //내 정보 수정
    @PutMapping(value = "/update-my-info", produces = "application/json; charset=utf8")
    @Operation(summary = "내 정보 수정", description = "수정되지 않은 정보까지 모두 전달하면, 전달된 학번으로 유저를 조회한 후 수정된 정보를 업데이트 (⚠️학번 및 비밀번호는 변경 불가)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "해당 학번의 회원이 존재하지 않습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "이미 존재하는 이메일 입니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "요청 권한이 없습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "정보가 정상적으로 수정되었습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> updateMyInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody
            @RequestBody RegisterDto dto) {
        return userInfoManageService.updateMyInfo(dto);
    }

    //비밀번호 수정하기
    @PutMapping(value = "/update-pwd", produces = "application/json; charset=utf8")
    @Operation(summary = "비밀번호 수정", description = "비밀번호를 수정하고자 하는 유저의 아이디를 같이 전달합니다\n해당 api를 테스트 하기 전에는 로그인 요청을 먼저 보내 헤더에 토큰 정보를 담아 해당 api를 요청해야 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "요청 권한이 없습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 입니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "예상치 못한 오류가 발생했습니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> updatePwd(@RequestBody LoginDto dto) {
        return userInfoManageService.updatePwd(dto);
    }

    //내 정보 보기
    @GetMapping("/read-my-info")
    @Operation(summary = "내 정보 조회", description = "인자로 전달된 학번을 토대로 조회된 정보 반환, 조회된 정보가 없다면 null값 반환")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", content = @Content(schema = @Schema(implementation = UserInfoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "해당 학번의 유저가 존재하지 않을 때", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> readMyInfo(@Parameter(name="userId", description = "학번", example = "20211079") @RequestParam Map map){
        UserInfoResponseDto ret =  userInfoManageService.findMyInfo(map.get("userId").toString());
        if(ret == null) return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "회원 정보가 존재하지 않습니다");
        return new ResponseEntity<>(ret, HttpStatus.OK);
    }

    //회원탈퇴
    @DeleteMapping("/withdraw")
    @Operation(summary = "회원 탈퇴", description = "요청한 사용자를 회원탈퇴시킵니다.<br/>테스트 시 토큰 값도 같이 전달해줘야 함.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "400", description = "액세스 토큰이 만료되었습니다. 재로그인해주세요.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저입니다.", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "요청이 성공적으로 처리되었습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> withdraw(HttpServletRequest request, HttpServletResponse response) {
        return authService.withDraw(request, response);
    }

    //비밀번호 찾기
    @PostMapping("/send-password-mail")
    @Operation(summary = "비밀번호 찾기", description = "메일을 보내면 해당 메일로 임시 비밀번호를 발급함")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "존재하는 메일이 없습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "메일 발송 중 에러가 발생했습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "200", description = "메일 발송이 완료되었습니다", content = @Content(schema = @Schema(implementation = ResponseDto.class)))
    })
    public ResponseEntity<?> sendRandomPassword(@io.swagger.v3.oas.annotations.parameters.RequestBody @RequestBody EmailRequestDto dto) {
        String email = dto.getEmail();
        return authService.sendRandomPassword(email);
    }
}
