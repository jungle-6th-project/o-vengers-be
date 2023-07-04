package jungle.ovengers.support.converter;

import jungle.ovengers.entity.GroupEntity;
import jungle.ovengers.entity.MemberGroupEntity;
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
                          .deleted(false)
                          .build();
    }

    public GroupResponse from(GroupEntity groupEntity) {
        return GroupResponse.builder()
                            .groupId(groupEntity.getId())
                            .groupName(groupEntity.getGroupName())
                            .isSecret(groupEntity.isSecret())
                            .path(groupEntity.getPath())
                            .build();
    }

    public GroupResponse from(GroupEntity groupEntity, MemberGroupEntity memberGroupEntity) {
        return GroupResponse.builder()
                            .groupId(groupEntity.getId())
                            .groupName(groupEntity.getGroupName())
                            .isSecret(groupEntity.isSecret())
                            .path(groupEntity.getPath())
                            .color(memberGroupEntity.getColor())
                            .build();
    }
}
