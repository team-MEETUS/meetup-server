package site.mymeetup.meetupserver.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.mymeetup.meetupserver.response.ApiResponse;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ResourceNotFoundException 예외
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(CustomException e) {
        log.error("ResourceNotFoundException: {}", e.getErrorCode());
        ApiResponse<?> response = ApiResponse.error(e.getErrorCode());
        return new ResponseEntity<>(response, e.getErrorCode().getHttpStatus());
    }

    // 모든 예외
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
//        log.error("Exception: ", e);
//        ApiResponse<?> response = ApiResponse.error("알 수 없는 에러");
//        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
}

