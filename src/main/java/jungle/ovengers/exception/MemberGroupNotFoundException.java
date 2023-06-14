package jungle.ovengers.exception;

public class MemberGroupNotFoundException extends RuntimeException {
    private final Long memberId;
    private final Long groupId;

    public MemberGroupNotFoundException(Long memberId, Long groupId) {
        this.memberId = memberId;
        this.groupId = groupId;
    }

    @Override
    public String getMessage() {
        return "사용자가 참여한 그룹의 id가 아닙니다. 사용자 id : " + memberId + " 그룹 id : " + groupId;
    }
}