package jungle.ovengers.entity;

import lombok.*;
import org.springframework.cglib.core.Local;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class MemberRoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_room_id")
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long roomId;

    @Column
    private Duration durationTime;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private boolean deleted;

    public boolean isAfter(LocalDateTime from) {
        return this.time.isAfter(from) || this.time.isEqual(from);
    }

    public boolean isBefore(LocalDateTime to) {
        return this.time.isBefore(to);
    }
}
