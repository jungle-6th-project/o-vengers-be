package jungle.ovengers.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class RoomResponse {
    private Long roomId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<String> profiles;
}
