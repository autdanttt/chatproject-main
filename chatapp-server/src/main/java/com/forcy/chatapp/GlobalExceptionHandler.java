package com.forcy.chatapp;

import com.forcy.chatapp.auth.RefreshTokenNotFoundException;
import com.forcy.chatapp.security.jwt.JwtValidationException;
import com.forcy.chatapp.user.UsernameAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import com.forcy.chatapp.user.UserNotFoundException;

import java.util.Date;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO handleGenericException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.addError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex) {
        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.FORBIDDEN.value());
        error.addError("Access Denied: " + ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.warn("Access denied: {}", ex.getMessage());
        return error;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleConstrainViolationException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();
        ConstraintViolationException  violationException = (ConstraintViolationException) ex;
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(request.getServletPath());

        var constraintViolation =  violationException.getConstraintViolations();
        constraintViolation.forEach(constraint ->{
            error.addError(constraint.getPropertyPath() + ": "+ constraint.getMessage());
        });
        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO handleBadRequestException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO handleUsernameAlreadyExistsException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.CONFLICT.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleUserNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @ExceptionHandler(RefreshTokenNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorDTO handleRefreshTokenNotFoundException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();

        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.NOT_FOUND.value());
        error.addError(ex.getMessage());
        error.setPath(request.getServletPath());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }


//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ResponseBody
//    public ErrorDTO handleHttpMessageNotReadable(HttpServletRequest request, HttpMessageNotReadableException ex) {
//        return new ErrorDTO(
//                new Date(),
//                HttpStatus.BAD_REQUEST.value(),
//                request.getRequestURI(),
//                List.of("Invalid or malformed JSON request: " + ex.getMostSpecificCause().getMessage())
//        );
//    }


    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());
        error.setStatus(status.value());
        error.setPath(((ServletWebRequest) request).getRequest().getRequestURI());

        // Rút gọn thông báo lỗi
        String simplified = simplifyHttpMessageNotReadableMessage(ex.getMostSpecificCause().getMessage());
        error.addError("Malformed JSON request: " + simplified);

        LOGGER.error("Malformed JSON: {}", ex.getMessage());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    private String simplifyHttpMessageNotReadableMessage(String message) {
        if (message == null) return "Unknown error";

        if (message.contains("Unrecognized field")) {
            // Ví dụ: Unrecognized field "messageType"
            int start = message.indexOf("\"");
            int end = message.indexOf("\"", start + 1);
            if (start != -1 && end != -1) {
                String originalFeld = message.substring(start + 1, end);
                return "Field invalid: " +originalFeld;
            }
        }

        return message.split("\n")[0]; // fallback ngắn gọn
    }

    @ExceptionHandler(JwtValidationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public ErrorDTO handleJwtValidationException(HttpServletRequest request, Exception ex){
        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());
        error.setStatus(HttpStatus.UNAUTHORIZED.value());
        error.setPath(request.getServletPath());
        error.addError(ex.getMessage());

        LOGGER.error(ex.getMessage(), ex);
        return error;
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ErrorDTO error = new ErrorDTO();
        error.setTimestamp(new Date());

        LOGGER.error(ex.getMessage(), ex);
        error.setStatus(HttpStatus.BAD_REQUEST.value());
        error.setPath(((ServletWebRequest) request).getRequest().getServletPath());
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(fieldError -> {
            error.addError(fieldError + ": "+ fieldError.getDefaultMessage());
        });
        return new ResponseEntity<>(error, headers, status);
    }

}
