package jungle.ovengers.service;

import com.google.auth.oauth2.GoogleCredentials;
import jungle.ovengers.config.security.AuditorHolder;
import jungle.ovengers.entity.ClientEntity;
import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.exception.MemberNotFoundException;
import jungle.ovengers.model.request.ClientAddRequest;
import jungle.ovengers.model.response.ClientAddResponse;
import jungle.ovengers.repository.ClientRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.support.converter.ClientConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Transactional
@RequiredArgsConstructor
@Service
public class ClientService {

    private final MemberRepository memberRepository;
    private final ClientRepository clientRepository;
    private final AuditorHolder auditorHolder;

    public ClientAddResponse saveFcmToken(ClientAddRequest request) {
        Long memberId = auditorHolder.get();
        MemberEntity memberEntity = memberRepository.findByIdAndDeletedFalse(memberId)
                                                    .orElseThrow(() -> new MemberNotFoundException(memberId));

        ClientEntity clientEntity = clientRepository.findByMemberId(memberId)
                                                    .orElse(null);

        if (clientEntity == null) {
            return ClientConverter.from(clientRepository.save(ClientConverter.to(request, memberEntity)));
        }

        clientEntity.updateFcmToken(request.getFcmToken());
        return ClientConverter.from(clientEntity);
    }

//    public String getAccessToken() throws IOException {
//        String path = "o-vengers-firebase-adminsdk-gpcsd-dc4c8a5963.json";
//        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(path).getInputStream())
//                                                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
//
//
//    }
}
