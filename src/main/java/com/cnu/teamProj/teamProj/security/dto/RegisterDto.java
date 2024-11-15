package com.cnu.teamProj.teamProj.security.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String name;
    private String pwd;
    private String email; //mail
    private String phone;
    private String studentNumber; //학번
    private String id; //username(닉네임)
}
