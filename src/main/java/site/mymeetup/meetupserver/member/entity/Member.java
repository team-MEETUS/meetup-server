package site.mymeetup.meetupserver.member.entity;

import jakarta.persistence.*;
import lombok.*;

import site.mymeetup.meetupserver.common.entity.BaseEntity;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.member.role.Role;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Builder
@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")

public class Member extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberId;

    //핸드폰으로 로그인
    @Column(unique = true, nullable = false)
    private String phone;

    @ManyToOne
    @JoinColumn(name = "geo_id", nullable = false)
    private Geo geo;

    private String kakao;

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

    @OneToMany(mappedBy = "member")
    private List<MemberInterest> memberInterest;

    // 멤버 수정. 성별, 생년월일 수정 불가
    public void updateMember(Member updateMember) {
        if(updateMember.getGeo() !=null) {
            this.geo = updateMember.getGeo();
        }
        if(updateMember.getPhone()!=null) {
            this.phone = updateMember.getPhone();
        }
        this.kakao = updateMember.getKakao();
        this.naver = updateMember.getNaver();
        if(updateMember.getPassword()!=null) {
            this.password = updateMember.getPassword();
        }
        if(updateMember.getNickname()!=null) {
            this.nickname = updateMember.getNickname();
        }
        if (updateMember.getBirth() != null) {
            this.birth = updateMember.getBirth();
        }
        this.gender = updateMember.getGender();
        if(updateMember.getIntro()!=null) {
            this.intro = updateMember.getIntro();
        }
        if(updateMember.getOriginalImg()!=null) {
            this.originalImg = updateMember.getOriginalImg();
        }
        if(updateMember.getSaveImg()!=null) {
            this.saveImg = updateMember.getSaveImg();
        }
    }

    // 멤버 삭제
    public void changeMemberStatus(int status) {
        this.status = status;
    }
}
