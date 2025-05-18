package com.cnu.teamProj.teamProj.security.dto;

import lombok.Data;

@Data
public class RegisterDto {
    private String id; //학번
    private String pwd;
    private String name;
    private String email; //mail
    private String phone;
}
