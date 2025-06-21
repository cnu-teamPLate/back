package com.cnu.teamProj.teamProj.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handlerUserNotFound(UserNotFoundException ex) {
        return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnexpectedException(Exception ex) {
        return ResultConstant.returnResultCustom(ResultConstant.UNEXPECTED_ERROR, "예상치 못한 오류가 발생했습니다");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleInvalidFormat(MethodArgumentNotValidException ex) {
        return ResultConstant.returnResultCustom(ResultConstant.INVALID_PARAM, "요청 형식이 잘못되었습니다");
    }
}
