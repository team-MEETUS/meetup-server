package site.mymeetup.meetupserver.crew.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.crew.role.CrewMemberRole;
import site.mymeetup.meetupserver.crew.role.CrewMemberRoleConverter;
import site.mymeetup.meetupserver.member.entity.Member;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "crew_and_member")
@Entity
public class CrewMember extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "crew_and_member_id")
    private Long crewMemberId;

    @Convert(converter = CrewMemberRoleConverter.class)
    @Column(nullable = false)
    private CrewMemberRole role;

    @ManyToOne
    @JoinColumn(name = "crew_id")
    private Crew crew;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
