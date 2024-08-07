package site.mymeetup.meetupserver.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import site.mymeetup.meetupserver.exception.ErrorCode;

@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(false, null, new ApiError(errorCode.getCode(), errorCode.getMessage()));
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(false, null, new ApiError(code, message));
    }

}
