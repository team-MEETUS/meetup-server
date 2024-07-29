package site.mymeetup.meetupserver.test;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Entity
@Data
@NoArgsConstructor
@Table(name = "test")
public class Test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "test_id")
    private Long testId;

    @Column(name = "name")
    private String name;

    @Column(name = "create_date")
    private Timestamp createdDate;

    @Column(name = "update_date")
    private Timestamp updateDate;

    public TestDto toDto() {
        TestDto dto = new TestDto();
        dto.setTestId(testId);
        dto.setName(name);
        dto.setCreateDate(createdDate);
        dto.setUpdateDate(updateDate);
        return dto;
    }
}
