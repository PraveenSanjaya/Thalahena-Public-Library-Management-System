package com.ThalahenaPublicLibrary.ThalahenaPublicLibrarydemo.security;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Malformed/incomplete requests (missing required parameter or part, wrong parameter type)
     * must surface as 400 Bad Request, not fall through to the generic 500 handler below.
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<?> handleBadRequestException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 400);
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        response.put("path", getCurrentRequestPath());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * A write that violates a database constraint (e.g. deleting a member/book that still has
     * dependent transactions/fines, or a unique-key clash not already pre-checked in the
     * controller) is a client-caused conflict, not a server fault — surface it as 409, not 500.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 409);
        response.put("error", "Conflict");
        response.put("message", "This operation conflicts with existing related data (e.g. borrow history, fines, or reservations) and cannot be completed.");
        response.put("path", getCurrentRequestPath());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 403);
        response.put("error", "Forbidden");
        response.put("message", "Access denied: You do not have permission to access this resource");
        response.put("path", getCurrentRequestPath());
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 401);
        response.put("error", "Unauthorized");
        response.put("message", "Invalid username or password");
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", 500);
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String getCurrentRequestPath() {
        try {
            org.springframework.web.context.request.RequestAttributes requestAttributes = 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                jakarta.servlet.http.HttpServletRequest request = 
                    ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();
                return request.getRequestURI();
            }
        } catch (Exception e) {
            // Ignore if not in request context
        }
        return "unknown";
    }
}
