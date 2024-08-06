package site.mymeetup.meetupserver.member.entity;

import jakarta.persistence.*;
import site.mymeetup.meetupserver.common.Role;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.geo.entity.Geo;

import java.time.LocalDateTime;

@Builder
@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")

//UserDetails 관련 우선 주석 처리함
//public class Member implements UserDetails {
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    //핸드폰으로 로그인
    @Column(unique = true, nullable = false)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "geo_id", nullable = false)
    private Geo geo;

    @Column(unique = true)
    private String kakao;

    @Column(unique = true)
    private String naver;

    private String password;

    @Column(nullable = false)
    private String nickname;

    private String intro;

    @Column(nullable = false)
    private String birth;

    @Column(nullable = false)
    private int gender;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    private int status;

    private LocalDateTime deadDate;

    private String originalImg;

    private String saveImg;



}


