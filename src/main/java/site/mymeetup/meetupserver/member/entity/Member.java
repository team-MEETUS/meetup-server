package site.mymeetup.meetupserver.member.entity;

import jakarta.persistence.*;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.member.role.Role;

import java.time.LocalDateTime;
import java.util.Optional;

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

    // 멤버 수정
    public void updateMember(Member updateMember) {
        this.geo = updateMember.getGeo();
        //==핸드폰 번호 수정의 경우 인증 과정 필요 - 수정 예정==
        Optional.ofNullable(updateMember.getPhone()).ifPresent(phone -> this.phone = phone);
        //==신규 카카오/네이버 계정 추가, 혹은 기존 카카오/네이버 계정정보 수정의 경우 인증 과정 필요 - 수정 예정 ==
        this.kakao = updateMember.getKakao();
        this.naver = updateMember.getNaver();
        //==비밀번호 수정의 경우 인증 과정 필요 - 수정 예정==
        this.password = updateMember.getPassword();
        Optional.ofNullable(updateMember.getNickname()).ifPresent(nickname -> this.nickname = nickname);
        this.intro = updateMember.getIntro();
        Optional.ofNullable(updateMember.getBirth()).ifPresent(birth -> this.birth = birth);
        Optional.of(updateMember.getGender()).ifPresent(gender -> this.gender = gender);
        this.originalImg = updateMember.getOriginalImg();
        this.saveImg = updateMember.getSaveImg();
    }

    // 멤버 삭제
    public void changeMemberStatus(int status) {
        this.status = status;
    }
}