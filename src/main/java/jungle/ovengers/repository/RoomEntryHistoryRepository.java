package jungle.ovengers.repository;

import jungle.ovengers.entity.RoomEntryHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomEntryHistoryRepository extends JpaRepository<RoomEntryHistoryEntity, Long> {
   List<RoomEntryHistoryEntity> findByMemberRoomIdAndExitTimeIsNull(Long memberRoomId);
}
