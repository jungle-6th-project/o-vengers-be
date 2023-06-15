package jungle.ovengers.exception;

public class RoomNotFoundException extends RuntimeException {
    private Long roomId;

    public RoomNotFoundException(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 방 번호입니다. roomId: " + roomId;
    }
}
