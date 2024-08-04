package site.mymeetup.meetupserver.geo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "geo")
@Entity
public class Geo {
    @Id
    private long geoId;

    private String city;

    private String district;

    private String county;

    private Double latitude;

    private Double longitude;
}
