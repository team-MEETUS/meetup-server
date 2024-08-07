package site.mymeetup.meetupserver.crew.role;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CrewMemberRole {
    EXPELLED(0, "강퇴"),
    MEMBER(1, "일반"),
    ADMIN(2, "운영진"),
    LEADER(3, "모임장"),
    PENDING(4, "승인대기"),
    REJECTED(5, "승인거절");

    private final int status;
    private final String name;

    CrewMemberRole(int status, String name) {
        this.status = status;
        this.name = name;
    }

    // status -> Role
    public static CrewMemberRole enumOf(Integer status) {
        return Arrays.stream(CrewMemberRole.values())
                .filter(c -> c.getStatus() == status)
                .findAny().orElse(null);
    }
}
