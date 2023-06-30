package jungle.ovengers.repository;

import jungle.ovengers.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {

    void deleteByMemberIdAndRoomId(Long memberId, Long roomId);

    List<NotificationEntity> findByNotificationTime(LocalDateTime localDateTime);
}
