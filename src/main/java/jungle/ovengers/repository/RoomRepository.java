package jungle.ovengers.repository;

import jungle.ovengers.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByGroupIdAndStartTimeAndDeletedFalse(Long groupId, LocalDateTime startTime);

    Optional<RoomEntity> findByIdAndDeletedFalse(Long roomId);

    List<RoomEntity> findByGroupIdAndDeletedFalse(Long groupId);
    @Query("SELECT r FROM RoomEntity r WHERE r.id IN :roomIds AND r.deleted = false")
    List<RoomEntity> findAllByIdAndDeletedFalse(List<Long> roomIds);

    Optional<RoomEntity> findByGroupIdAndOwnerId(Long groupId, Long ownerId);
}
