package com.dlim2012.clients.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ApplicationExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex) {
        log.error("An error occurred: ", ex);
        // Add further exception handling logic here...
    }
}
