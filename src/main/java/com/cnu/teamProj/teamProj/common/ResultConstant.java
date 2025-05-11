package com.cnu.teamProj.teamProj.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResultConstant {
    public static final int NOT_EXIST = -1;
    public static final int OK = 1;
    public static final int UNEXPECTED_ERROR = -5;
    public static final int REQUIRED_PARAM_NON = -2;
    public static final int NO_PERMISSION = -3;
    public static final int INVALID_PARAM = -4;
    public static final int ALREADY_EXIST = -6;

    public static ResponseEntity<String> returnResult(int result) {
        String message = "";
        HttpStatus status;
        switch (result) {
            case NOT_EXIST :
                message = "해당 아이디의 값이 없습니다.";
                status = HttpStatus.NOT_FOUND;
                break;
            case UNEXPECTED_ERROR:
                message = "예상치 못한 오류가 발생했습니다.";
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                break;
            case REQUIRED_PARAM_NON:
                message = "응답에 필요한 필수 요청 값이 전달되지 않았습니다.";
                status = HttpStatus.BAD_REQUEST;
                break;
            case NO_PERMISSION:
                message = "요청 권한이 없습니다";
                status = HttpStatus.NOT_ACCEPTABLE;
                break;
            case INVALID_PARAM:
                message = "요청 값이 조건에 부합하지 않습니다";
                status = HttpStatus.BAD_REQUEST;
                break;
            case ALREADY_EXIST:
                message = "이미 값이 존재합니다";
                status = HttpStatus.BAD_REQUEST;
                break;
            default:
                message = "요청이 성공적으로 처리되었습니다";
                status = HttpStatus.OK;
        }

        return new ResponseEntity<>(message, status);
    }
}
