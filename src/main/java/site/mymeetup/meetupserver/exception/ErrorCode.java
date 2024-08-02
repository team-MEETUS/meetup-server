package site.mymeetup.meetupserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 테스트
    NOT_FOUND_DEPT(HttpStatus.NOT_FOUND, "T-40401", "존재하지 않는 테스트입니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
