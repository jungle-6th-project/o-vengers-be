package jungle.ovengers.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import jungle.ovengers.entity.ClientEntity;
import jungle.ovengers.entity.NotificationEntity;
import jungle.ovengers.repository.ClientRepository;
import jungle.ovengers.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ClientRepository clientRepository;

    public void sendEnterTimePushAlarm(LocalDateTime now) throws FirebaseMessagingException {
        List<String> tokens = findReceivers(now);

        if (tokens.isEmpty()) {
            return;
        }
        MulticastMessage message = MulticastMessage.builder()
                                                   .addAllTokens(tokens)
                                                   .setNotification(Notification.builder()
                                                                                .setTitle("뽀독뽀독 - 온라인 독서실")
                                                                                .setBody("공부방 입장 시간입니다.")
                                                                                .build())
                                                   .build();
        FirebaseMessaging.getInstance()
                         .sendEachForMulticast(message);
    }

    private List<String> findReceivers(LocalDateTime now) {
        List<Long> memberIds = notificationRepository.findByNotificationTime(now)
                                                     .stream()
                                                     .map(NotificationEntity::getMemberId)
                                                     .collect(Collectors.toList());

        return clientRepository.findByMemberIds(memberIds)
                               .stream()
                               .map(ClientEntity::getFcmToken)
                               .collect(Collectors.toList());
    }
}
