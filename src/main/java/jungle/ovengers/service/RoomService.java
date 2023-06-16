package jungle.ovengers.service;

import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.model.response.RoomResponse;
import jungle.ovengers.repository.RoomRepository;
import jungle.ovengers.support.converter.RoomConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public List<RoomResponse> getRooms(RoomBrowseRequest request) {
        LocalDateTime from = request.getFrom();
        LocalDateTime to = request.getTo();

        return roomRepository.findByGroupIdAndDeletedFalse(request.getGroupId())
                             .stream()
                             .filter(room -> room.isAfter(from) && room.isBefore(to))
                             .map(RoomConverter::from)
                             .collect(Collectors.toList());
    }
}
