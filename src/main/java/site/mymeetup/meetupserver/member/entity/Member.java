package site.mymeetup.meetupserver.member.entity;

import jakarta.persistence.*;
import site.mymeetup.meetupserver.common.Role;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.geo.entity.Geo;

import java.sql.Timestamp;
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

@ManyToOne(fetch = FetchType.LAZY)
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


//==updateMember==//
public void updateMember(Member updateMember){
    Optional.ofNullable(updateMember.getGeo()).ifPresent(geo -> this.geo = geo);
    //==핸드폰 번호 수정의 경우 인증 과정 필요 - 수정 예정==
    //Optional.ofNullable(updateMember.getPhone()).ifPresent(phone -> this.phone = phone);
    //==신규 카카오/네이버 계정 추가, 혹은 기존 카카오/네이버 계정정보 수정의 경우 인증 과정 필요 - 수정 예정 ==
    //Optional.ofNullable(updateMember.getKakao()).ifPresent(kakao -> this.kakao = kakao);
    //Optional.ofNullable(updateMember.getNaver()).ifPresent(naver -> this.naver = naver);
    //==비밀번호 수정의 경우 인증 과정 필요 - 수정 예정==
    //Optional.ofNullable(updateMember.getPassword()).ifPresent(password -> this.password = password);
    Optional.ofNullable(updateMember.getNickname()).ifPresent(nickname -> this.nickname = nickname);
    this.intro = updateMember.getIntro();
    Optional.ofNullable(updateMember.getOriginalImg()).ifPresent(originalImg -> this.originalImg = originalImg);
    Optional.ofNullable(updateMember.getSaveImg()).ifPresent(saveImg -> this.saveImg = saveImg);
}

//==deleteMember==//
public void changeMemberStatus(int status){
    this.status = status;
}

    //========== UserDetails implements ==========//
    /**
     * Token을 고유한 phone 값으로 생성
     */
//    @Override
//    public static String getUsername() {
//        return phone;
//    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//        authorities.add( new SimpleGrantedAuthority("ROLE_"+this.role.name()));
//        return authorities;
//    }
//
//        @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }

}


