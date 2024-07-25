package site.mymeetup.meetupserver.test;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public TestDto toDto() {
        TestDto dto = new TestDto();
        dto.setTestId(testId);
        dto.setName(name);
        return dto;
    }
}
