package jungle.ovengers.service;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.entity.RankEntity;
import jungle.ovengers.model.request.RankBrowseRequest;
import jungle.ovengers.model.response.RankResponse;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.repository.RankRepository;
import jungle.ovengers.support.converter.RankConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RankService {

    private final RankRepository rankRepository;
    private final MemberRepository memberRepository;

    public List<RankResponse> getRanksInGroup(RankBrowseRequest request) {
        List<RankEntity> ranks = rankRepository.findByGroupIdAndDeletedFalse(request.getGroupId());
        ranks.sort(Comparator.comparing(RankEntity::getDuration)
                             .reversed());

        List<Long> memberIds = ranks.stream()
                                    .map(RankEntity::getMemberId)
                                    .collect(Collectors.toList());

        Map<Long, MemberEntity> memberMap = memberRepository.findAllById(memberIds)
                                                            .stream()
                                                            .filter(memberEntity -> !memberEntity.isDeleted())
                                                            .collect(Collectors.toMap(MemberEntity::getId, Function.identity()));
        return ranks.stream()
                    .map(rank -> RankConverter.from(rank, memberMap.get(rank.getMemberId())))
                    .collect(Collectors.toList());
    }
}
