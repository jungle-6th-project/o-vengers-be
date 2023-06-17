package jungle.ovengers.support.validator;

import jungle.ovengers.model.request.RoomAddRequest;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class RoomValidator {
    public static void validateIfRoomTimeAfterNow(RoomAddRequest request) {
        if (isInvalidTime(request)) {
            throw new IllegalArgumentException("현재 시각 이전의 예약은 생성할 수 없습니다.");
        }
    }

    private static boolean isInvalidTime(RoomAddRequest request) {
        return LocalDateTime.now()
                            .isAfter(request.getEndTime());
    }
}
