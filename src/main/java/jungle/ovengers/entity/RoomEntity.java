package jungle.ovengers.entity;

import jungle.ovengers.support.converter.ProfileImagesConverter;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class RoomEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(nullable = false)
    private Long groupId;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private boolean deleted;

    @Column(columnDefinition = "TEXT")
    @Builder.Default
    @Convert(converter = ProfileImagesConverter.class)
    private List<String> profiles = new ArrayList<>();

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void delete() {
        this.deleted = true;
    }

    public void addProfile(String profile) {
        this.profiles.add(profile);
    }

    public void removeProfile(String profile) {
        this.profiles.remove(profile);
    }

    public boolean isAfter(LocalDateTime from) {
        return this.startTime.isAfter(from) || this.startTime.isEqual(from);
    }

    public boolean isBefore(LocalDateTime to) {
        return this.endTime.isBefore(to) || this.endTime.isEqual(to);
    }

    public boolean isAfterEndTime(LocalDateTime time) {
        return time.isAfter(this.endTime);
    }

    public boolean isBeforeStartTime(LocalDateTime time) {
        return time.isBefore(this.startTime);
    }
}
