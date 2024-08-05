package site.mymeetup.meetupserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.mymeetup.meetupserver.response.ApiError;
import site.mymeetup.meetupserver.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ResourceNotFoundException 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleCustomException(CustomException ex) {
        log.error("CustomException: {}", ex.getErrorCode());
        ApiResponse<?> response = ApiResponse.error(ex.getErrorCode());
        return new ResponseEntity<>(response, ex.getErrorCode().getHttpStatus());
    }

    // 유효성 검사 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());

        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();

        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errorMessage.append(fieldError.getField());
            errorMessage.append(" : ");
            errorMessage.append(fieldError.getDefaultMessage());
            errorMessage.append("; ");
        }

        // 유효성 검사 오류 응답 생성
        ApiResponse<?> response = ApiResponse.error("VALIDATION_ERROR", errorMessage.toString());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 모든 예외
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
//        log.error("Exception: ", e);
//        ApiResponse<?> response = ApiResponse.error("알 수 없는 에러");
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}

