package jungle.ovengers.config.batch;

import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.service.RoomService;
import jungle.ovengers.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TodoService todoService;
    private final RoomService roomService;

    private final GroupRepository groupRepository;

    @Bean
    public Job deleteTodoJob() {
        return jobBuilderFactory.get("deleteTodoJob")
                                .start(deleteTodoStep())
                                .next(deleteRoomStep())
                                .build();
    }

    @Bean
    public Job getGroupRoomsJob() {
        return jobBuilderFactory.get("getGroupRoomsJob")
                                .start(getGroupRoomsStep())
                                .build();
    }

    @Bean
    public Step deleteTodoStep() {
        return stepBuilderFactory.get("deleteTodoStep")
                                 .tasklet(((contribution, chunkContext) -> {
                                     log.info("called deleteTodoStep");
                                     todoService.deleteDoneTodo();
                                     return RepeatStatus.FINISHED;
                                 }))
                                 .build();
    }

    @Bean
    public Step deleteRoomStep() {
        return stepBuilderFactory.get("deleteTodoStep")
                                 .tasklet(((contribution, chunkContext) -> {
                                     log.info("called deleteRoomStep");
                                     roomService.deleteExpiredRoom();
                                     return RepeatStatus.FINISHED;
                                 }))
                                 .build();
    }

    @Bean
    public Step getGroupRoomsStep() {
        LocalDateTime from = LocalDate.now()
                                      .atTime(0, 0, 0)
                                      .minusWeeks(1);
        LocalDateTime to = from.plusWeeks(2);

        return stepBuilderFactory.get("getGroupRoomsStep")
                                 .tasklet(((contribution, chunkContext) -> {
                                     log.info("called getGroupRoomsStep");
                                     groupRepository.findAllByDeletedFalse()
                                                    .forEach(groupEntity -> {
                                                        roomService.getRooms(new RoomBrowseRequest(groupEntity.getId(), from, to));
                                                    });
                                     return RepeatStatus.FINISHED;
                                 }))
                                 .build();
    }
}
