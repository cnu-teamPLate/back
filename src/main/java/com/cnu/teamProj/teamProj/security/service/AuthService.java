package com.cnu.teamProj.teamProj.security.service;

import com.cnu.teamProj.teamProj.comment.Comment;
import com.cnu.teamProj.teamProj.comment.CommentRepository;
import com.cnu.teamProj.teamProj.common.ResponseDto;
import com.cnu.teamProj.teamProj.common.ResultConstant;
import com.cnu.teamProj.teamProj.file.entity.Docs;
import com.cnu.teamProj.teamProj.file.repository.DocsRepository;
import com.cnu.teamProj.teamProj.proj.entity.ProjMem;
import com.cnu.teamProj.teamProj.proj.repository.MemberRepository;
import com.cnu.teamProj.teamProj.review.Review;
import com.cnu.teamProj.teamProj.review.ReviewRepository;
import com.cnu.teamProj.teamProj.schedule.entity.MakePlan;
import com.cnu.teamProj.teamProj.schedule.entity.Participants;
import com.cnu.teamProj.teamProj.schedule.repository.MakePlanRepository;
import com.cnu.teamProj.teamProj.schedule.repository.ParticipantsRepository;
import com.cnu.teamProj.teamProj.security.dto.RegisterDto;
import com.cnu.teamProj.teamProj.security.entity.Role;
import com.cnu.teamProj.teamProj.security.entity.User;
import com.cnu.teamProj.teamProj.security.jwt.JWTGenerator;
import com.cnu.teamProj.teamProj.security.repository.RoleRepository;
import com.cnu.teamProj.teamProj.security.repository.UserRepository;
import com.cnu.teamProj.teamProj.task.entity.Task;
import com.cnu.teamProj.teamProj.task.repository.TaskRepository;
import com.cnu.teamProj.teamProj.util.EmailProvider;
import com.cnu.teamProj.teamProj.util.RedisUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final JWTGenerator jwtGenerator;
    private final RedisUtil redisUtil;
    private final CommentRepository commentRepository;
    private final DocsRepository docsRepository;
    private final ReviewRepository reviewRepository;
    private final TaskRepository taskRepository;
    private final MakePlanRepository makePlanRepository;
    private final ParticipantsRepository participantsRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final EmailProvider emailProvider;
    private final PasswordEncoder passwordEncoder;

    public String invalidateToken(HttpServletRequest request, String cause) {
        String token = request.getHeader("Authorization").substring(7);
        if(!jwtGenerator.validateToken(token)) return null;
        String userId = jwtGenerator.getUserNameFromJWT(token);
        long expiration = jwtGenerator.getRemainingTime(token);
        redisUtil.setValuesWithTimeout(token, cause, expiration);

        return userId;
    }

    /**
     * 회원가입 로직
     * @param registerDto
     * - id : username (닉네임)
     * - name : name
     * - pwd : 비밀번호
     * @return 요청 상태
     */
    public ResponseEntity<?> registerUser(RegisterDto registerDto) {
        if(userRepository.existsById(registerDto.getId())){
            return ResultConstant.returnResultCustom(ResultConstant.ALREADY_EXIST, "이미 존재하는 학번입니다");
        }

        if(userRepository.existsByMail(registerDto.getEmail())) {
            return ResultConstant.returnResultCustom(ResultConstant.ALREADY_EXIST, "이미 존재하는 메일입니다");
        }

        User user = new User();
        user.setUsername(registerDto.getName());
        user.setPwd(passwordEncoder.encode(registerDto.getPwd()));
        user.setMail(registerDto.getEmail());
        user.setPhone(registerDto.getPhone());
        user.setId(registerDto.getId());
        if(roleRepository.findByName("ROLE_USER").isEmpty()) {
            log.error("존재하는 역할이 없습니다");
            return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "등록된 역할 정보가 없습니다");
        }
        Role roles = roleRepository.findByName("ROLE_USER").get();
        user.setRoles(Collections.singletonList(roles));
        user.setUsername(registerDto.getId());
        userRepository.save(user);
        return ResultConstant.returnResult(ResultConstant.OK);
    }

    /**
     * 비밀번호 찾기
     * @param email 회원가입 시 등록했던 이메일
     * @return 응답 상태
     */
    public ResponseEntity<?> sendRandomPassword(String email) {
        if(userRepository.findByMail(email).isEmpty()) {
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하는 메일이 없습니다");
        }
        User user = userRepository.findByMail(email).orElseThrow();
        String password = emailProvider.sendCertificationMail(email); //임시 메일 발급
        if(password == null) {
            return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "메일 발송 중 에러가 발생했습니다");
        }
        user.setPwd(passwordEncoder.encode(password)); //
        userRepository.save(user);
        return ResultConstant.returnResultCustom(ResultConstant.OK, "메일 발송이 완료되었습니다");
    }

    /**
     * 회원 탈퇴 로직
     */
    @Transactional
    public ResponseEntity<?> withDraw(HttpServletRequest request, HttpServletResponse response) {
        //액세스 토큰 확인
        String userId = invalidateToken(request, "withdraw");
        if(userId==null) {
            return ResultConstant.returnResultCustom(ResultConstant.NO_PERMISSION, "액세스 토큰이 만료되었습니다. 재로그인해주세요.");
        }
        if(userRepository.findById(userId).isEmpty()) {
            return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, "존재하지 않는 유저입니다.");
        }

        //DB 정보 삭제
        User user = userRepository.findById(userId).orElseThrow();
        User none = userRepository.findById("null").orElseThrow();
        //과제에 대한 코멘트 알수없음으로 변경
        ArrayList<Comment> comments = commentRepository.findCommentsByUserId(userId);
        for(Comment comment : comments) {
            comment.setUserId("null");
            commentRepository.save(comment);
        }
        //파일 정보의 담당 유저 알수없음으로 변경
        ArrayList<Docs> files = docsRepository.findAllById(userId);
        for(Docs file : files) {
            file.setId("null");
            docsRepository.save(file);
        }
        //후기의 담당 유저 알수없음으로 변경
        ArrayList<Review> reviews = reviewRepository.findReviewsByUserId(user);
        for(Review review : reviews) {
            review.setUserId(none);
            reviewRepository.save(review);
        }
        //과제의 담당 유저 알수없음으로 변경
        List<Task> tasks = taskRepository.findTasksById(userId);
        for(Task task : tasks) {
            task.setId("null");
            taskRepository.save(task);
        }
        //웬투밋 관련 레코드 삭제
        List<MakePlan> makePlans = makePlanRepository.findMakePlansByUserId(user);
        makePlanRepository.deleteAll(makePlans);
        //참가자 관련 레코드 삭제
        List<Participants> participants = participantsRepository.findAllById(userId);
        participantsRepository.deleteAll(participants);
        //프로젝트 멤버에서 삭제
        List<ProjMem> projMems = memberRepository.findProjMemsById(user);
        memberRepository.deleteAll(projMems);

        //관련 필드 모두 정리한 후 유저 삭제
        userRepository.delete(user);

        request.getSession().invalidate();

        Cookie cookie = new Cookie("User-Token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResultConstant.returnResult(ResultConstant.OK);
    }

}
