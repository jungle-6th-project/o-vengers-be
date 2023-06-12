package jungle.ovengers.entity;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class GroupEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity ownerMember;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private boolean is_secret;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String path;
}
