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
    DEPARTED(5, "퇴장");

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

    // 일반 멤버가 특정 role로 변경 가능한지 확인
    public boolean canMemberChangeTo(CrewMemberRole newRole) {
        return newRole == LEADER || newRole == ADMIN || newRole == EXPELLED || newRole == DEPARTED;
    }

    // 운영진이 특정 role로 변경 가능한지 확인
    public boolean canAdminChangeTo(CrewMemberRole newRole) {
        return newRole == LEADER || newRole == MEMBER || newRole == DEPARTED;
    }

    // 승인 대기 멤버가 특정 role로 변경 가능한지 확인
    public boolean canPendingChangeTo(CrewMemberRole newRole) {
        return newRole == MEMBER || newRole == DEPARTED;
    }
}
