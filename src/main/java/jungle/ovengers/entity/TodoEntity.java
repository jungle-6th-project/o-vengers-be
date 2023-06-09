package jungle.ovengers.entity;

import jungle.ovengers.model.request.TodoEditRequest;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
public class TodoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long groupId;

    @Column
    private String content;

    @Column(nullable = false)
    private boolean done;

    @Column(nullable = false)
    private boolean deleted;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column
    @LastModifiedDate
    private LocalDateTime updatedAt;

    public void changeTodoInfo(TodoEditRequest request) {
        this.content = request.getContent();
        this.done = request.isDone();
        if (done) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void delete() {
        this.deleted = true;
    }
}
