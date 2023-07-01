package jungle.ovengers.support.converter;

import jungle.ovengers.entity.ClientEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.model.request.ClientAddRequest;
import jungle.ovengers.model.response.ClientAddResponse;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

@UtilityClass
public final class ClientConverter {

    public ClientAddResponse from(ClientEntity clientEntity) {
        return ClientAddResponse.builder()
                                .id(clientEntity.getId())
                                .memberId(clientEntity.getMemberId())
                                .fcmToken(clientEntity.getFcmToken())
                                .build();
    }

    public ClientEntity to(ClientAddRequest request, MemberEntity memberEntity) {
        LocalDateTime now = LocalDateTime.now();
        return ClientEntity.builder()
                           .memberId(memberEntity.getId())
                           .fcmToken(request.getFcmToken())
                           .createdAt(now)
                           .updatedAt(now)
                           .build();
    }
}
