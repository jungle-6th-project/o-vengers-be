package jungle.ovengers.support.converter;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.model.request.GroupAddRequest;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class GroupConverter {
    public GroupEntity to(GroupAddRequest request, Long memberId) {
        return GroupEntity.builder()
                          .ownerId(memberId)
                          .groupName(request.getGroupName())
                          .isSecret(request.isSecret())
                          .password(request.getPassword())
                          .path(request.getPath())
                          .build();
    }
}
