package jungle.ovengers.support.converter;

import jungle.ovengers.entity.RoomEntryHistoryEntity;
import jungle.ovengers.model.response.RoomHistoryResponse;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class RoomHistoryConverter {

    public static RoomHistoryResponse from(RoomEntryHistoryEntity roomEntryHistoryEntity) {
        return RoomHistoryResponse.builder()
                                  .enterTime(roomEntryHistoryEntity.getEnterTime())
                                  .exitTime(roomEntryHistoryEntity.getExitTime())
                                  .build();
    }
}
