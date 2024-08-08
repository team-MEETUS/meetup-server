package site.mymeetup.meetupserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // S3
    NO_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "I40001", "파일 확장자가 존재하지 않습니다."),
    INVALID_FILE_EXTENTION(HttpStatus.BAD_REQUEST, "I40002", "유효하지 않은 파일 확장자입니다."),
    IMAGE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "I40003", "이미지 값이 기존과 일치하지 않습니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "I40401", "이미지를 찾을 수 없습니다."),
    IO_EXCEPTION_ON_IMAGE_UPLOAD(HttpStatus.INTERNAL_SERVER_ERROR, "I50001", "이미지 업로드 중 입출력 예외가 발생했습니다."),
    PUT_OBJECT_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR, "I50002", "S3에 이미지 업로드 중 예외가 발생했습니다."),
    IO_EXCEPTION_ON_IMAGE_DELETE(HttpStatus.INTERNAL_SERVER_ERROR, "I50003", "이미지 삭제 중 입출력 예외가 발생했습니다."),

    // 회원
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "M40401", "존재하지 않는 회원입니다."),
    MEMBER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "M40301", "비활성화된 회원은 접근할 수 없습니다."),

    // 모임
    CREW_BAD_REQUEST(HttpStatus.BAD_REQUEST, "C40001", "올바르지 않은 파라미터 값 입니다."),
    CREW_NOT_FOUND(HttpStatus.NOT_FOUND, "C40401", "존재하지 않는 모임입니다."),
    INVALID_PAGE_NUMBER(HttpStatus.BAD_REQUEST, "C40002", "유효하지 않은 페이지 넘버 입니다."),

    // 모임원
    ALREADY_CREW_MEMBER(HttpStatus.BAD_REQUEST, "CM40001", "이미 모임원 입니다."),
    CREW_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "CM40401", "존재하지 않는 모임원입니다."),
    CREW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "C40301", "권한이 없습니다."),

    // 모임 찜
    ALREADY_CREW_LIKE(HttpStatus.BAD_REQUEST, "CL40001", "이미 찜을 한 모임입니다."),
    NOT_CREW_LIKE(HttpStatus.BAD_REQUEST, "CL40002", "찜을 하지않은 모임입니다."),

    // 게시판
    BOARD_ACCESS_DENIED(HttpStatus.FORBIDDEN, "B40301", "일반 모임원은 공지 등록이 불가능합니다."),
    BOARD_WRITER_ACCESS_DENIED(HttpStatus.FORBIDDEN, "B40302", "작성자와 로그인 회원이 일치하지 않습니다."),
    BOARD_CREW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "B40303", "접근할 수 없는 게시글입니다."),
    BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "B40401", "존재하지 않는 게시글입니다."),
    BOARD_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "B40402", "존재하지 않는 카테고리입니다."),
    BOARD_DELETE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "B40304", "삭제는 작성자 또는 운영진 모임장만 가능합니다."),

    // 댓글
    BOARD_COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "BM40401", "존재하지 않는 댓글입니다."),
    BOARD_COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "BM40301", "작성자가 아니면 수정이 불가능합니다."),
    BOARD_COMMENT_ID_ACCESS_DENIED(HttpStatus.FORBIDDEN, "BM40302", "접근할 수 없는 댓글입니다."),

    // 관심사
    INTEREST_BAD_REQUEST(HttpStatus.BAD_REQUEST, "I40001", "관심사 대분류에 속한 소분류가 아닙니다."),
    INTEREST_BIG_NOT_FOUND(HttpStatus.NOT_FOUND, "I40401", "존재하지 않는 관심사입니다."),
    INTEREST_SMALL_NOT_FOUND(HttpStatus.NOT_FOUND, "I40402", "존재하지 않는 상세 관심사입니다."),

    // 지역
    GEO_NOT_FOUND(HttpStatus.NOT_FOUND, "G40401", "존재하지 않는 지역입니다."),
  
    // 채팅
    CHAT_NOT_FOUND(HttpStatus.BAD_REQUEST, "C40001", "존재하지 않는 모임 채팅방입니다."),

    // 사진첩
    ALBUM_ACCESS_DENIED(HttpStatus.FORBIDDEN, "A40301", "일반 모임원은 사진첩 등록이 불가능합니다."),
    ALBUM_CREW_ACCESS_DENIED(HttpStatus.FORBIDDEN, "A40303", "접근할 수 없는 사진첩입니다."),
    ALBUM_NOT_FOUND(HttpStatus.NOT_FOUND, "A40401", "존재하지 않는 사진첩입니다."),
    ALBUM_DELETE_ACCESS_DENIED(HttpStatus.FORBIDDEN, "A40304", "사진첩 삭제는 작성자 또는 운영진 모임장만 가능합니다."),

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
