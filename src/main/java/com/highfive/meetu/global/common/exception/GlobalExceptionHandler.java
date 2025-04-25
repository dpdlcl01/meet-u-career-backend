package com.highfive.meetu.global.common.exception;

import com.highfive.meetu.global.common.exception.NotFoundException;
import com.highfive.meetu.global.common.exception.ResourceNotFoundException;
import com.highfive.meetu.global.common.response.ResultData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({NotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ResultData<Void>> handleNotFound(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .header("Content-Type", "application/json")
                .body(ResultData.fail(ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResultData<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(ResultData.fail(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResultData<Void>> handleOther(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .header("Content-Type", "application/json")
                .body(ResultData.fail("서버 오류가 발생했습니다."));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ResultData<Void>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .header("Content-Type", "application/json")
                .body(ResultData.fail(e.getMessage()));
    }
}