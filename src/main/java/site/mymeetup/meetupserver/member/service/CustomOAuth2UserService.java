package site.mymeetup.meetupserver.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.geo.repository.GeoRepository;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.dto.KakaoResponse;
import site.mymeetup.meetupserver.member.dto.NaverResponse;
import site.mymeetup.meetupserver.member.dto.OAuth2Resp;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;
import site.mymeetup.meetupserver.member.role.Role;


@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final GeoRepository geoRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 제공자
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Resp oAuth2Resp = null;

        // 제공자별 분기 처리
        if ("naver".equals(registrationId)) {
            oAuth2Resp = new NaverResponse(oAuth2User.getAttributes());
        } else if ("kakao".equals(registrationId)) {
            oAuth2Resp = new KakaoResponse(oAuth2User.getAttributes());
        } else {
            throw new CustomException(ErrorCode.MEMBER_PROVIDER_NOT_EXIST);
        }

        // 넘어온 회원정보가 기존 회원인지 핸드폰 번호로 판단
        Member memberData = memberRepository.findByPhone(oAuth2Resp.getPhone());

        // 존재하지 않는다면 신규 회원으로 저장
        if (memberData == null) {
            String formattedBirthday = oAuth2Resp.getBirth();

            Member newMember = Member.builder()
                    .nickname(oAuth2Resp.getNickname())
                    .phone(oAuth2Resp.getPhone())
                    .gender(Integer.parseInt(oAuth2Resp.getGender()))
                    .birth(formattedBirthday)
                    .role(Role.USER)
                    .build();

            if ("naver".equals(oAuth2Resp.getProvider())) {
                newMember.builder().naver(oAuth2Resp.getNaver()).build();
            } else if ("kakao".equals(oAuth2Resp.getProvider())) {
                newMember.builder().kakao(oAuth2Resp.getKakao()).build();
            }

            memberRepository.save(newMember);
            return new CustomUserDetails(memberData);
        // 기존 회원이지만 SNS 정보가 없는 경우 업데이트
        } else {
            boolean updated = false;
            if ("naver".equals(oAuth2Resp.getProvider()) && memberData.getNaver() == null) {
            Member member = Member.builder()
                .naver(oAuth2Resp.getNaver()).build();
                updated = true;
            } else if ("kakao".equals(oAuth2Resp.getProvider()) && memberData.getKakao() == null) {
                Member member = Member.builder()
                .kakao(oAuth2Resp.getKakao()).build();
                updated = true;
            }
            if (updated) {
                memberRepository.save(memberData);
            }
            return new CustomUserDetails(memberData);
        }
    }
}