package site.mymeetup.meetupserver.chat.entity;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import site.mymeetup.meetupserver.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Document(collection = "chat")
public class Chat {
    @Id
    private String id;
    private String message;
    private Long senderId;
    private Long receiverId;
    private Long crewId;
    private LocalDateTime createDate;
}
