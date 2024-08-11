package site.mymeetup.meetupserver.member.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    public CustomUserDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("로그인 시도: 전화번호 = " + username);

        Member memberData = memberRepository.findByPhone(username);

        if (memberData == null) {
            System.out.println("사용자를 찾을 수 없습니다: " + username);
            throw new UsernameNotFoundException("User not found with phone: " + username);
        }

        System.out.println("사용자 정보 조회 성공: " + memberData);
        return new CustomUserDetails(memberData);
    }
}
