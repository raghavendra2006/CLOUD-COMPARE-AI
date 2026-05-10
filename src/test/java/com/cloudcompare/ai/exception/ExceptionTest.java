package com.cloudcompare.ai.exception;

import com.cloudcompare.ai.dto.ApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ExceptionTest {

    @Test
    void testBusinessException() {
        BusinessException ex = new BusinessException("Error", HttpStatus.BAD_REQUEST);
        assertEquals("Error", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testGlobalExceptionHandler() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        // Test BusinessException
        BusinessException busEx = new BusinessException("Business Error", HttpStatus.CONFLICT);
        ResponseEntity<ApiResponse<Void>> busResp = handler.handleBusinessException(busEx);
        assertEquals(HttpStatus.CONFLICT, busResp.getStatusCode());
        assertEquals("Business Error", busResp.getBody().getError());

        // Test AccessDeniedException
        org.springframework.security.access.AccessDeniedException accEx = 
            new org.springframework.security.access.AccessDeniedException("Denied");
        ResponseEntity<ApiResponse<Void>> accResp = handler.handleAccessDeniedException(accEx);
        assertEquals(HttpStatus.FORBIDDEN, accResp.getStatusCode());

        // Test Global Exception
        Exception genEx = new Exception("System Error");
        ResponseEntity<ApiResponse<Void>> genResp = handler.handleGlobalException(genEx);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, genResp.getStatusCode());
        assertTrue(genResp.getBody().getError().contains("unexpected system error"));
    }
}
