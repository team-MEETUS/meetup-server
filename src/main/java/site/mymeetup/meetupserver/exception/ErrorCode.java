package site.mymeetup.meetupserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 관심사
    INTEREST_BAD_REQUEST(HttpStatus.BAD_REQUEST, "I40001", "관심사 대분류에 속한 소분류가 아닙니다."),
    INTEREST_BIG_NOT_FOUND(HttpStatus.NOT_FOUND, "I40401", "존재하지 않는 관심사입니다."),
    INTEREST_SMALL_NOT_FOUND(HttpStatus.NOT_FOUND, "I40402", "존재하지 않는 상세 관심사입니다."),

    // 채팅
    CHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "C40001", "존재하지 않는 모임 채팅방입니다."),

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
