package site.mymeetup.meetupserver.member.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class NaverResponse implements OAuth2Resp {
    private final Map<String, Object> attribute;

    public NaverResponse(Map<String, Object> attribute) {
        this.attribute = (Map<String, Object>) attribute.get("response");
    }

    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    @JsonProperty("id")
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    @JsonProperty("name")
    public String getNickname() {
        return attribute.get("name").toString();
    }

    @Override
    @JsonProperty("mobile")
    public String getPhone() {
        return attribute.get("mobile").toString();
    }

    @Override
    @JsonProperty("gender")
    public String getGender() {
        return attribute.get("gender").toString();
    }

    @Override
    @JsonProperty("birthyear")
    public String getBirthyear() {
        return attribute.get("birthyear").toString();
    }

    @Override
    @JsonProperty("birthday")
    public String getBirthday() {
        return attribute.get("birthday").toString();
    }

    @Override
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