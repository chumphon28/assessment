package com.kbtg.bootcamp.posttest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbtg.bootcamp.posttest.exception.CustomException;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Utils {

    public static String stringify(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    public static void assertException(HttpStatus httpStatus, Exception ex) {
        CustomException customException = (CustomException) ex.getCause();
        assertEquals(httpStatus, customException.getHttpStatus());
        assertEquals(httpStatus.getReasonPhrase().toUpperCase(), customException.getCode());
    }
}
