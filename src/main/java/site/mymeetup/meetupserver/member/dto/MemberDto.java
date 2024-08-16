package site.mymeetup.meetupserver.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import site.mymeetup.meetupserver.geo.entity.Geo;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.role.Role;

import java.time.LocalDateTime;

public class MemberDto {

    @Getter
    @NoArgsConstructor
    public static class MemberSaveReqDto {

        @NotEmpty(message = "핸드폰 번호는 필수 입력사항입니다")
        @Size(max = 300)
        private String phone;
        @Size(max = 300)
        private String password;
        @NotEmpty(message = "닉네임은 필수 입력사항입니다")
        @Size(max = 20)
        private String nickname;
        private String intro;
        //@Patten(정규표현식)
        @NotEmpty(message = "생년월일은 필수 입력사항입니다")
        @Size(max = 20)
        private String birth;
        @NotNull(message = "성별은 필수 입력사항입니다")
        private int gender;
        private String originalImg;
        private String saveImg;
        @NotNull(message = "관심지역은 필수 입력사항입니다")
        private Long geoId;

        // 비밀번호 인코딩 메서드
        public void encodePassword(BCryptPasswordEncoder encoder) {
            if (this.password != null) {
                this.password = encoder.encode(this.password);
            }
        }

        // 회원가입 DTO -> Entity
        public Member goEntity(Geo geo) {
            return Member.builder()
                    .geo(geo)
                    .phone(phone)
                    .password(password)
                    .nickname(nickname)
                    .intro(intro)
                    .birth(birth)
                    .gender(gender)
                    .role(Role.USER)
                    .status(1)
                    .build();
        }

        // 회원수정 DTO -> Entity
        public Member toEntity(Geo geo, String originalImg, String saveImg) {
            return Member.builder()
                    .geo(geo)
                    .phone(phone)
                    .password(password)
                    .nickname(nickname)
                    .intro(intro)
                    .birth(birth)
                    .gender(gender)
                    .role(Role.USER)
                    .status(1)
                    .originalImg(originalImg != null ? originalImg : "test.jpg")
                    .saveImg(saveImg != null ? saveImg : "test.jpg")
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberLoginReqDto {
        private String phone;
        private String password;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSaveRespDto {
        private Long memberId;

        @Builder
        public MemberSaveRespDto(Member member) {
            this.memberId = member.getMemberId();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberInfoDto {
        private Long memberId;
        private Geo geo;
        private String nickname;
        private String saveImg;

        @Builder
        public MemberInfoDto(Member member) {
            this.memberId = member.getMemberId();
            this.geo = member.getGeo();
            this.nickname = member.getNickname();
            this.saveImg = member.getSaveImg();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSelectRespDto {
        private Long memberId;
        private Geo geo;
        private String phone;
        private String kakao;
        private String naver;
        private String password;
        private String nickname;
        private String intro;
        private String birth;
        private Integer gender;
        private Role role;
        private Integer status;
        private LocalDateTime deadDate;
        private String originalImg;
        private String saveImg;
        private LocalDateTime createDate;
        private LocalDateTime updateDate;

        @Builder
        public MemberSelectRespDto(Member member) {
            this.memberId = member.getMemberId();
            this.geo = member.getGeo();
            this.phone = member.getPhone();
            this.kakao = member.getKakao();
            this.naver = member.getNaver();
            this.password = member.getPassword();
            this.nickname = member.getNickname();
            this.intro = member.getIntro();
            this.birth = member.getBirth();
            this.gender = member.getGender();
            this.role = member.getRole();
            this.status = member.getStatus();
            this.deadDate = member.getDeadDate();
            this.originalImg = member.getOriginalImg();
            this.saveImg = member.getSaveImg();
            this.createDate = member.getCreateDate();
            this.updateDate = member.getUpdateDate();
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSimpleDto {
        private Long memberId;
        private String nickname;
        private String intro;
        private String saveImg;

        @Builder
        public MemberSimpleDto(Member member) {
            this.memberId = member.getMemberId();
            this.nickname = member.getNickname();
            this.intro = member.getIntro();
            this.saveImg = member.getSaveImg();
        }
    }
}
