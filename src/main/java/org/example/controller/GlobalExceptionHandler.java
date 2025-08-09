package org.example.controller;

import org.example.dto.response.MessageResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.format.DateTimeParseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<MessageResponseDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Invalid parameter format for '%s'. Expected: %s, Received: %s", 
            ex.getName(), ex.getRequiredType().getSimpleName(), ex.getValue());
        return new ResponseEntity<>(new MessageResponseDTO(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<MessageResponseDTO> handleDateTimeParseException(DateTimeParseException ex) {
        String message = String.format("Invalid date format: %s. Please use YYYY-MM-DD format.", ex.getParsedString());
        return new ResponseEntity<>(new MessageResponseDTO(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<MessageResponseDTO> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        String message = String.format("Missing required parameter: %s", ex.getParameterName());
        return new ResponseEntity<>(new MessageResponseDTO(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<MessageResponseDTO> handleGenericException(Exception ex) {
        String message = "An unexpected error occurred: " + ex.getMessage();
        return new ResponseEntity<>(new MessageResponseDTO(message), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
