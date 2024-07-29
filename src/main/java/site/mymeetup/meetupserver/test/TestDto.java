package site.mymeetup.meetupserver.test;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDto {
    private Long testId;
    private String name;
    private Timestamp createDate;
    private Timestamp updateDate;
}
