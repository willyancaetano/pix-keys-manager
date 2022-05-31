package com.pix.keys.handler;

import com.pix.keys.dto.InvalidParamsDto;
import com.pix.keys.dto.ProblemDetailsResponseDto;
import com.pix.keys.exception.DataNotFoundException;
import com.pix.keys.exception.KeyTypeNotAllowedException;
import com.pix.keys.exception.NumberOfKeysExceededException;
import com.pix.keys.exception.PixKeyAlreadyExistsException;
import com.pix.keys.exception.PixKeyNotActiveException;
import com.pix.keys.exception.PixKeyNotExistsException;
import com.pix.keys.exception.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation error");

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            dto.addInvalidParam(new InvalidParamsDto(fieldName, errorMessage));
        });

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handleValidationException(ValidationException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage());

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(PixKeyAlreadyExistsException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handlePixKeyAlreadyExistsException(PixKeyAlreadyExistsException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage());

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(NumberOfKeysExceededException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handleNumberOfKeysExceededException(NumberOfKeysExceededException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage());

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(PixKeyNotExistsException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handlePixKeyNotExistsException(PixKeyNotExistsException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(PixKeyNotActiveException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handlePixKeyNotActiveException(PixKeyNotActiveException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage());

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(KeyTypeNotAllowedException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handleKeyTypeNotAllowedException(KeyTypeNotAllowedException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage());

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handleIllegalArgumentException(IllegalArgumentException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                ex.getMessage());

        return ResponseEntity.unprocessableEntity().contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

    @ExceptionHandler(DataNotFoundException.class)
    public ResponseEntity<ProblemDetailsResponseDto> handleDataNotFoundException(DataNotFoundException ex) {

        ProblemDetailsResponseDto dto = new ProblemDetailsResponseDto(
                ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.APPLICATION_PROBLEM_JSON).body(dto);
    }

}
