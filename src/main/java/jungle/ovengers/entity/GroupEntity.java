package jungle.ovengers.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

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

    @Column(nullable = false)
    private Long ownerId;

    @Column(nullable = false)
    private String groupName;

    @Column(nullable = false)
    private boolean isSecret;

    @Column
    private String password;

    @Column(nullable = false)
    private String path;

    @Column(updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private boolean deleted;

    public boolean isOwner(Long memberId) {
        return Objects.equals(this.ownerId, memberId);
    }

    public boolean isEqualPassword(String password) {
        return this.password.equals(password);
    }

    public void delete() {
        this.deleted = true;
    }
}
