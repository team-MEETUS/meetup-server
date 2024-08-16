package site.mymeetup.meetupserver.member.dto;

public interface OAuth2Resp {

    String getProvider();
    String getProviderId();
    String getNickname();
    String getPhone();
    String getGender();
    String getBirthyear();
    String getBirthday();
    String getBirth();
    String getKakao();
    String getNaver();
}
