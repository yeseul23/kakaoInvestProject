package com.kakaopay.investment.exception;

import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(InvestmentException.class)
    @ResponseBody
    public Map<String, Object> handleApiException(InvestmentException e, HttpServletRequest request, HttpServletResponse response) {
        ErrorCodeInfo errorCodeInfo = e.getErrorCodeInfo();
        response.setStatus(errorCodeInfo.getStatusCode());
        return ImmutableMap.of("errorMsg", errorCodeInfo.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, Object> handleMethodArgumentNotValidException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return ImmutableMap.of("errorMsg", HttpStatus.BAD_REQUEST.getReasonPhrase());
    }


    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleMethodException(MethodArgumentNotValidException e, HttpServletRequest request) {
        return ImmutableMap.of("errorMsg", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}
