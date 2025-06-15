package com.cnu.teamProj.teamProj.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handlerUserNotFound(UserNotFoundException ex) {
        return ResultConstant.returnResultCustom(ResultConstant.NOT_EXIST, ex.getMessage());
    }
}
