package jungle.ovengers.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomJoinRequest {
    private Long roomId;
    private Long groupId;

    @Override
    public String toString() {
        return "RoomJoinRequest{" +
                "roomId=" + roomId +
                '}';
    }
}
