package site.mymeetup.meetupserver.chat.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import site.mymeetup.meetupserver.common.entity.BaseEntity;

import java.time.LocalDateTime;

@Data
@Document(collection = "chat")
public class Chat {
    @Id
    private String id;
    private String sender;
    private String receiver;
    private String message;
    private Long crewId;
    private LocalDateTime createDate;
}
