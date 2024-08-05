package site.mymeetup.meetupserver.member.entity;

import jakarta.persistence.*;
import site.mymeetup.meetupserver.common.Role;
import lombok.*;
import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.geo.entity.Geo;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Data
@Getter
@NoArgsConstructor
@Table(name = "member")

//UserDetails 관련 우선 주석 처리함
    //public class Member implements UserDetails {
    public class Member extends BaseEntity {

    @Id    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private LocalDateTime dead_date;

    private String originalImg;

    private String saveImg;

    @Column(nullable = false)
    private Timestamp createDate;

    @Column(nullable = false)
    private Timestamp updateDate;

    //== 생성자 ==//
    @Builder
    public Member(String phone, String nickname, String birth, int gender, Role role, int status, Geo geo, String originalImg, String saveImg) {
        this.phone = phone;
        this.nickname = nickname;
        this.birth = birth;
        this.gender = gender;
        this.role = role;
        this.status = status;
        this.geo = geo;
        this.originalImg = originalImg;
        this.saveImg = saveImg;
    }

    //==update==//
    public void updateMember(String phone, String password){
        this.phone = phone;
        this.password = password;
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


