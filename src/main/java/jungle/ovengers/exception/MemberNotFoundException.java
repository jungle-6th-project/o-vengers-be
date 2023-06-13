package jungle.ovengers.exception;

public class MemberNotFoundException extends RuntimeException {
    private final Long memberId;

    public MemberNotFoundException(Long memberId) {
        this.memberId = memberId;
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 회원입니다. memberId: " + memberId;
    }
}