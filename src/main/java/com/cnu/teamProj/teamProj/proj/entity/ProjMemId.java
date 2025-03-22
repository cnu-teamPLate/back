package com.cnu.teamProj.teamProj.proj.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjMemId implements Serializable {
    private String id;
    private String projId;
}
