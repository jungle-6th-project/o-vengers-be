package jungle.ovengers.repository;

import jungle.ovengers.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {
    Optional<ClientEntity> findByMemberId(Long memberId);
    @Query("SELECT c FROM ClientEntity c WHERE c.memberId IN :memberIds")
    List<ClientEntity> findByMemberIds(List<Long> memberIds);
}
