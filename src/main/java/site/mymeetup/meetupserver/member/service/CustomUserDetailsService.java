package site.mymeetup.meetupserver.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import site.mymeetup.meetupserver.config.AES128;
import site.mymeetup.meetupserver.exception.CustomException;
import site.mymeetup.meetupserver.exception.ErrorCode;
import site.mymeetup.meetupserver.member.dto.CustomUserDetails;
import site.mymeetup.meetupserver.member.entity.Member;
import site.mymeetup.meetupserver.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AES128 aes128;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        if (username == null || username.isEmpty()) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        String encryptPhone;
        try {
            encryptPhone = aes128.encrypt(username);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        // Find member
        Member memberData = memberRepository.findByPhone(encryptPhone);

        if (memberData == null) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }
        return new CustomUserDetails(memberData);
    }
}