package com.kbtg.bootcamp.posttest.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
