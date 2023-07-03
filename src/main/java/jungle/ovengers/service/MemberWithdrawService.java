package jungle.ovengers.service;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.enums.MemberStatus;
import jungle.ovengers.repository.ClientRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.NotificationRepository;
import jungle.ovengers.repository.TokenStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MemberWithdrawService {

    private final MemberRepository memberRepository;
    private final ClientRepository clientRepository;
    private final NotificationRepository notificationRepository;
    private final TokenStorage tokenStorage;
    private final GroupService groupService;

    @Async
    public void deleteAssociations(MemberEntity memberEntity) {
        tokenStorage.deleteRefreshToken(memberEntity.getId());
        groupService.deleteAllAssociation(memberEntity);

        clientRepository.deleteByMemberId(memberEntity.getId());
        notificationRepository.deleteByMemberId(memberEntity.getId());
    }

    /** 스케쥴링에 의한 연관 데이터 삭제는 동기적으로 일어나도록 자가 호출로 구현. 개선 필요 **/
    public void withdrawFailedMembers() {
        memberRepository.findAllByStatus(MemberStatus.SEPARATE)
                .forEach(memberEntity -> {
                    memberEntity.delete();
                    deleteAssociations(memberEntity);
                });
    }
}
