package jungle.ovengers.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class RoomEntryHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_entry_history_entity")
    private Long id;

    @Column(nullable = false)
    private Long memberRoomId;

    @Column(nullable = false)
    private LocalDateTime enterTime;

    @Column
    private LocalDateTime exitTime;

    public void updateExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }
}
