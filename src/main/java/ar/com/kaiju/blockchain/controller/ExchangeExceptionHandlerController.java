package ar.com.kaiju.blockchain.controller;

import java.util.NoSuchElementException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExchangeExceptionHandlerController 
{
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleNoSuchElementException(NoSuchElementException nse)
    {
        log.error("No element found: ", nse);
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<?> handleInternalServerError(Throwable t)
    {
        log.error("Unexpected Error: ", t);
        return ResponseEntity.internalServerError().build();
    }
}
