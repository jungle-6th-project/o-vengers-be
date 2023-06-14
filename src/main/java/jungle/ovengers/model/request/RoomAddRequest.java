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
public class RoomAddRequest {
    private Long groupId;
    private LocalDateTime from;
    private LocalDateTime to;
}
