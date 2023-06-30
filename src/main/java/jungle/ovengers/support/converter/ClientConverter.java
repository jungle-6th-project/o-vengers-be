package jungle.ovengers.support.converter;

import jungle.ovengers.entity.ClientEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.model.request.ClientAddRequest;
import jungle.ovengers.model.response.ClientAddResponse;
import lombok.experimental.UtilityClass;

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
        return ClientEntity.builder()
                           .memberId(memberEntity.getId())
                           .fcmToken(request.getFcmToken())
                           .build();
    }
}
