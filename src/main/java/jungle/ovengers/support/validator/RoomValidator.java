package jungle.ovengers.support.validator;

import jungle.ovengers.model.request.RoomAddRequest;
import lombok.experimental.UtilityClass;

import java.time.Duration;
import java.time.LocalDateTime;

@UtilityClass
public final class RoomValidator {

    private final int INTERVAL_MINUTES = 25;
    public static void validateIfRoomTimeAfterNow(RoomAddRequest request) {
        if (isInvalidTime(request)) {
            throw new IllegalArgumentException("유효하지 않은 요청입니다.");
        }
    }

    private static boolean isInvalidTime(RoomAddRequest request) {
        return isLateTime(request) || isInvalidTimeInterval(request);
    }

    private static boolean isLateTime(RoomAddRequest request) {
        return LocalDateTime.now()
                            .isAfter(request.getEndTime());
    }

    private static boolean isInvalidTimeInterval(RoomAddRequest request) {
        return !Duration.between(request.getStartTime(), request.getEndTime())
                       .equals(Duration.ofMinutes(INTERVAL_MINUTES));
    }
}
