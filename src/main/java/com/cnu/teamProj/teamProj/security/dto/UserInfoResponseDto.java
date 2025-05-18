package com.cnu.teamProj.teamProj.security.dto;

import com.cnu.teamProj.teamProj.security.entity.Role;
import com.cnu.teamProj.teamProj.security.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserInfoResponseDto {
    @Schema(description = "학번")
    private String id;
    @Schema(description = "메일 정보")
    private String email;
    @Schema(description = "전화번호")
    private String phone;
    @Schema(description = "이름")
    private String username;
    @Schema(description = "유저가 갖고 있는 권한")
    private List<String> roles;

    public UserInfoResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getMail();
        this.phone = user.getPhone();
        this.username = user.getUsername();
        this.roles = new ArrayList<>();
        for(Role role : user.getRoles()) {
            roles.add(role.getName());
        }
    }
}
