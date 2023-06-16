package jungle.ovengers.service;

import jungle.ovengers.model.request.RankBrowseRequest;
import jungle.ovengers.model.response.RankResponse;
import jungle.ovengers.repository.RankRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RankService {

    private final RankRepository rankRepository;

    public List<RankResponse> getRanksInGroup(RankBrowseRequest request) {
        return null;
    }
}
