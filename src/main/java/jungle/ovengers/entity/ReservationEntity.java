package jungle.ovengers.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class ReservationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(nullable = false)
    private LocalDateTime reservedTime;

    @OneToOne
    @JoinColumn(name = "owner_member_id", nullable = false)
    private MemberEntity memberEntity;

    @ManyToOne
    @JoinColumn(name = "member_group_id")
    private MemberGroupEntity memberGroupEntity;

}
