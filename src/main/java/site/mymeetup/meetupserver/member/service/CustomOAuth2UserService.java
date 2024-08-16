package site.mymeetup.meetupserver.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.album.entity.Album;
import site.mymeetup.meetupserver.chat.entity.Chat;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static site.mymeetup.meetupserver.member.dto.MemberDto.MemberSaveRespDto;

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

        // 제공자_회원아이디 형식으로 SNS ID 생성
        String snsMemberId = oAuth2Resp.getProvider() + "_" + oAuth2Resp.getProviderId();

        // 넘어온 회원정보가 기존 회원인지 핸드폰 번호로 판단
        Member memberData = memberRepository.findByPhone(oAuth2Resp.getPhone());

        // 존재하지 않는다면 신규 회원으로 저장
        if (memberData == null) {
            String formattedBirthday = oAuth2Resp.getBirth();

            Member member = Member.builder()
                    .nickname(oAuth2Resp.getNickname())
                    .phone(oAuth2Resp.getPhone())
                    .gender(Integer.parseInt(oAuth2Resp.getGender()))
                    .birth(formattedBirthday)
                    .role(Role.USER)
                    .build();

            if ("naver".equals(oAuth2Resp.getProvider())) {
                member.builder().naver(oAuth2Resp.getNaver()).build();
            } else if ("kakao".equals(oAuth2Resp.getProvider())) {
                member.builder().kakao(oAuth2Resp.getKakao()).build();
            }

            memberRepository.save(member);
            return new CustomUserDetails(memberData);
        // 기존 회원의 경우, SNS 업데이트 필요시 처리
        } else {
            if ("naver".equals(oAuth2Resp.getProvider()) && memberData.getNaver() == null) {
            Member member = Member.builder()
                .naver(oAuth2Resp.getNaver()).build();
            } else if ("kakao".equals(oAuth2Resp.getProvider()) && memberData.getKakao() == null) {
                Member member = Member.builder()
                .kakao(oAuth2Resp.getKakao()).build();
            }
        }
        return new CustomUserDetails(memberData);
    }
}