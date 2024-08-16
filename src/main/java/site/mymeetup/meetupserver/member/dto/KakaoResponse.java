package site.mymeetup.meetupserver.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class KakaoResponse implements OAuth2Resp {

    private final Map<String, Object> properties;

    private final Map<String, Object> account;

    private final String providerId;

    public KakaoResponse(Map<String, Object> attributes) {
        this.properties = (Map<String, Object>) attributes.get("properties");
        this.account = (Map<String, Object>) attributes.get("kakao_account");
        this.providerId = attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getProviderId() {
        return this.providerId;
    }

    @Override
    @JsonProperty("name")
    public String getNickname() {
        return properties.get("name").toString();
    }

    @Override
    @JsonProperty("phone_number")
    public String getPhone() {
        return account.get("mobile").toString();
    }

    @Override
    @JsonProperty("gender")
    public String getGender() {
        return properties.get("gender").toString();
    }

    @Override
    @JsonProperty("birthyear")
    public String getBirthyear() {
        return properties.get("birthyear").toString();
    }

    @Override
    @JsonProperty("birthday")
    public String getBirthday() {
        return properties.get("birthday").toString();
    }

    public String getBirth() {
        return this.getBirthyear() + "-" + this.getBirthday(); // 형식은 "YYYY-MM-DD"로 설정
    }

    @Override
    public String getKakao() {
        return "";
    }

    @Override
    public String getNaver() {
        return "";
    }

}
