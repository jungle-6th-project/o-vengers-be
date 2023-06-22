package jungle.ovengers.config.batch;

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

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final TodoService todoService;
    private final RoomService roomService;

    @Bean
    public Job deleteTodoJob() {
        return jobBuilderFactory.get("deleteTodoJob")
                                .start(deleteTodoStep())
                                .next(deleteRoomStep())
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
}
