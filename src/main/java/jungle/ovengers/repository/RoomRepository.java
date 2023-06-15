package jungle.ovengers.repository;

import jungle.ovengers.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByGroupIdAndStartTimeAndDeletedFalse(Long groupId, LocalDateTime startTime);

    Optional<RoomEntity> findByIdAndDeletedFalse(Long roomId);
}
