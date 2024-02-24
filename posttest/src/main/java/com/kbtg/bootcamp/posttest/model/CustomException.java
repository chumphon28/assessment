package com.kbtg.bootcamp.posttest.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException {
    private HttpStatus httpStatus;
    private String code;
    private String message;


    public CustomException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = httpStatus.getReasonPhrase().toUpperCase();
        this.message = message;
    }
}
