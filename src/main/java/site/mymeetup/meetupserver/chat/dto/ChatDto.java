package site.mymeetup.meetupserver.chat.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import site.mymeetup.meetupserver.chat.entity.Chat;

import java.time.LocalDateTime;

public class ChatDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ChatFindRespDto {
        private String id;
        private String message;
        private String sender;
        private LocalDateTime createDate;

        @Builder
        public ChatFindRespDto(Chat chat) {
            this.id = chat.getId();
            this.message = chat.getMessage();
            this.sender = chat.getSender();
            this.createDate = chat.getCreateDate();
        }
    }
}
