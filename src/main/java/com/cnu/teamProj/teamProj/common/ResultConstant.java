package com.cnu.teamProj.teamProj.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResultConstant {
    public static final int NOT_EXIST = -1; //404
    public static final int OK = 1; //200
    public static final int UNEXPECTED_ERROR = -5; //500
    public static final int REQUIRED_PARAM_NON = -2; //400
    public static final int NO_PERMISSION = -3; //401
    public static final int INVALID_PARAM = -4; //400
    public static final int ALREADY_EXIST = -6; //409

    public static ResponseEntity<ResponseDto> returnResult(int result) {
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
                status = HttpStatus.UNAUTHORIZED;
                break;
            case INVALID_PARAM:
                message = "요청 값이 조건에 부합하지 않습니다";
                status = HttpStatus.BAD_REQUEST;
                break;
            case ALREADY_EXIST:
                message = "이미 값이 존재합니다";
                status = HttpStatus.CONFLICT;
                break;
            default:
                message = "요청이 성공적으로 처리되었습니다";
                status = HttpStatus.OK;
        }

        return new ResponseEntity<>(new ResponseDto(message, status.value()), status);
    }

    public static ResponseEntity<?> returnResultCustom(int result, String message) {
        HttpStatus status = switch (result) {
            case NOT_EXIST -> HttpStatus.NOT_FOUND;
            case UNEXPECTED_ERROR -> HttpStatus.INTERNAL_SERVER_ERROR;
            case REQUIRED_PARAM_NON, INVALID_PARAM -> HttpStatus.BAD_REQUEST;
            case NO_PERMISSION -> HttpStatus.UNAUTHORIZED;
            case ALREADY_EXIST -> HttpStatus.CONFLICT;
            default -> {
                message = "요청이 성공적으로 처리되었습니다";
                yield HttpStatus.OK;
            }
        };
        if(message==null || message.trim().isEmpty()) {
            message = status.getReasonPhrase();
        }
        return new ResponseEntity<>(new ResponseDto(message, status.value()), status);
    }
}
