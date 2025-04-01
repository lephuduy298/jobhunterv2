package com.lephuduy.jobhunter.util.error;

import com.lephuduy.jobhunter.domain.dto.response.RestResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalException {
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<RestResponse<Object>> handleAllException(Exception ex) {
//        RestResponse<Object> res = new RestResponse<Object>();
//        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//        res.setMessage(ex.getMessage());
//        res.setError("Internal Server Error");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
//    }

    @ExceptionHandler(
//            value = {
            IdInvalidException.class
//            BadCredentialsException.class,
//            UsernameNotFoundException.class
//            }
    )
    public ResponseEntity<RestResponse<Object>> handleIdException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Exception occurs....");
        res.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(res);
    }

    @ExceptionHandler(value = NoResourceFoundException.class)
    public ResponseEntity<RestResponse<Object>> handleNotFoundException(Exception ex) {
        RestResponse<Object> res = new RestResponse<Object>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("404 Not Found. URL may not exists....");
        res.setMessage(ex.getMessage());
        return ResponseEntity.badRequest().body(res);
    }
//
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<RestResponse<Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        BindingResult result = ex.getBindingResult();
//        final List<FieldError> fieldErrors = result.getFieldErrors();
//
//        RestResponse<Object> res = new RestResponse<Object>();
//        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        res.setError(ex.getBody().getDetail());
//
//        // convert to list<string>
//        List<String> errors = new ArrayList<String>();
//        for (FieldError fieldError : fieldErrors) {
//            errors.add(fieldError.getDefaultMessage());
//        }
//        res.setMessage(errors.size() > 1 ? errors : errors.get(0));
//
//        return ResponseEntity.badRequest().body(res);
//    }
//
//    @ExceptionHandler(StorageException.class)
//    public ResponseEntity<RestResponse<Object>> handleStorageFileException(Exception ex) {
//        RestResponse<Object> res = new RestResponse<Object>();
//        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
//        res.setError("Exception upload file....");
//        res.setMessage(ex.getMessage());
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
//    }
//
//    @ExceptionHandler(PermissionException.class)
//    public ResponseEntity<RestResponse<Object>> handlePermissionException(Exception ex) {
//        RestResponse<Object> res = new RestResponse<Object>();
//        res.setStatusCode(HttpStatus.FORBIDDEN.value());
//        res.setError("Forbidden");
//        res.setMessage(ex.getMessage());
//        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
//    }
}
