package jungle.ovengers.support.converter;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.model.request.GroupAddRequest;
import jungle.ovengers.model.response.GroupResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class GroupConverter {
    public GroupEntity to(GroupAddRequest request, Long memberId) {
        return GroupEntity.builder()
                          .ownerId(memberId)
                          .groupName(request.getGroupName())
                          .isSecret(request.isSecret())
                          .password(request.getPassword())
                          .path(request.getPath())
                          .createdAt(LocalDateTime.now())
                          .deleted(false)
                          .build();
    }

    public GroupResponse from(GroupEntity groupEntity) {
        return GroupResponse.builder()
                            .groupId(groupEntity.getId())
                            .groupName(groupEntity.getGroupName())
                            .isSecret(groupEntity.isSecret())
                            .build();
    }
}
