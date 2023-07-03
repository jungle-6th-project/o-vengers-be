package jungle.ovengers.enums;

import lombok.ToString;

@ToString
public enum MemberStatus {
    REGULAR("정회원"),
    SEPARATE("탈퇴 대기 회원"),
    WITHDRAWN("탈퇴 회원"),
    ;

    private final String description;

    MemberStatus(String description) {
        this.description = description;
    }
}
