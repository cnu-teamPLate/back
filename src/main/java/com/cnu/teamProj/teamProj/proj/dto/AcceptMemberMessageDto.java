package com.cnu.teamProj.teamProj.proj.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;

@Data
@AllArgsConstructor
public class AcceptMemberMessageDto {
    private ArrayList<String> alreadyExists;
    private ArrayList<String> notUsers;
    private ArrayList<String> successMembers;
}
