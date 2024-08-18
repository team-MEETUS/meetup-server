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

    //회원가입 req
    @Getter
    @NoArgsConstructor
    public static class MemberSaveReqDto {

        @NotEmpty(message = "핸드폰 번호는 필수 입력사항입니다")
        @Size(max = 300)
        private String phone;
        @NotEmpty(message = "닉네임은 필수 입력사항입니다")
        @Size(max = 20)
        private String nickname;
        @NotEmpty(message = "생년월일은 필수 입력사항입니다")
        @Size(max = 20)
        private String birth;
        @NotNull(message = "성별은 필수 입력사항입니다")
        private int gender;
        @NotNull(message = "관심지역은 필수 입력사항입니다")
        private Long geoId;
        private String password;

        // 비밀번호 인코딩 메서드
        public void encodePassword(BCryptPasswordEncoder encoder) {
            if (this.password != null) {
                this.password = encoder.encode(this.password);
            }
        }

        // 회원가입 DTO -> Entity
        public Member toEntity(Geo geo) {
            return Member.builder()
                    .geo(geo)
                    .phone(phone)
                    .nickname(nickname)
                    .birth(birth)
                    .gender(gender)
                    .role(Role.USER)
                    .status(1)
                    .build();
        }
    }

    //회원수정 req
    @Getter
    @NoArgsConstructor
    public static class MemberUpdateReqDto {
        @NotEmpty(message = "핸드폰 번호는 필수 입력사항입니다")
        @Size(max = 300)
        private String phone;
        @NotEmpty(message = "닉네임은 필수 입력사항입니다")
        @Size(max = 20)
        private String nickname;
        @NotEmpty(message = "생년월일은 필수 입력사항입니다")
        @Size(max = 20)
        private String birth;
        private String intro;
        @NotNull(message = "성별은 필수 입력사항입니다")
        private int gender;
        @NotNull(message = "관심지역은 필수 입력사항입니다")
        private Long geoId;
        private String originalImg;
        private String saveImg;

        // 회원수정 DTO -> Entity
        public Member toEntity(Geo geo, String originalImg, String saveImg) {
            return Member.builder()
                    .geo(geo)
                    .phone(phone)
                    .nickname(nickname)
                    .birth(birth)
                    .intro(intro)
                    .gender(gender)
                    .role(Role.USER)
                    .status(1)
                    .originalImg(originalImg != null ? originalImg : "test.jpg")
                    .saveImg(saveImg != null ? saveImg : "test.jpg")
                    .build();
        }
    }

    // 회원가입 resp
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSaveRespDto {
        private Long memberId;

        @Builder
        public MemberSaveRespDto(Member member) {
            this.memberId = member.getMemberId();
        }
    }

    // 회원수정 resp
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberUpdateRespDto {
        private Long memberId;

        @Builder
        public MemberUpdateRespDto(Member member) {
            this.memberId = member.getMemberId();
        }
    }

    // 회원 정보 조회
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
    public static class MemberSnsDto {
        private Long memberId;
        private Geo geo;
        private String nickname;

    }



    // 로그인 req
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberLoginReqDto {
        private String phone;
        private String password;
    }

    // 회원 정보 조회 resp
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSelectRespDto {
        private Long memberId;
        private Geo geo;
        private String phone;
        private String nickname;
        private String intro;
        private String birth;
        private Integer gender;
        private Role role;
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
            this.nickname = member.getNickname();
            this.intro = member.getIntro();
            this.birth = member.getBirth();
            this.gender = member.getGender();
            this.role = member.getRole();
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

    // 문자 인증
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSMSReqDto {
        private String phone;
    }
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class MemberSMSRespDto {
        private Integer randomNum;

        @Builder
        public MemberSMSRespDto(Integer randomNum) {
            this.randomNum = randomNum;
        }
    }
}