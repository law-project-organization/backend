package com.project.law.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * 이외에 예외 처리는 각자 작업 진행 하면서 이어서 작성 하면 됩니다
     *
     * 로직상 커버 되지 않은 예외에 대한 처리는 추후 작업 합니다.
     * **/
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> exceptionHandler(Exception exception){

        // 간단하게 500에 메시지를 일관되게 보내줍니다.
        String message = HttpStatus.INTERNAL_SERVER_ERROR.name();
        return ResponseEntity.internalServerError().body(message);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> illegalArgumentExceptionHandler(IllegalArgumentException illegalArgumentException){

        // 간단하게 500에 메시지를 일관되게 보내줍니다.
        String message = illegalArgumentException.getMessage();

        return ResponseEntity.internalServerError().body(message);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> badRequestExceptionHandler(BadRequestException badRequestException){

        // 간단하게 500에 메시지를 일관되게 보내줍니다.
        String message = badRequestException.getMessage();

        // 로직상 커버 되지 않은 예외에 대한 처리는 추후 작업 합니다.
        return ResponseEntity.internalServerError().body(message);
    }
    
    /**
     * 웹소켓은 아마도 안쓸 것으로 예상
     * **/
    
}
