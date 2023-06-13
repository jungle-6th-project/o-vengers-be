package jungle.ovengers.exception;

public class GroupNotFoundException extends RuntimeException {
    private final Long groupId;

    public GroupNotFoundException(Long groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 그룹입니다. groupId: " + groupId;
    }
}