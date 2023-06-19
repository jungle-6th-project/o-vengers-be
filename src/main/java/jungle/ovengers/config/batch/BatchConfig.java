package jungle.ovengers.config.batch;

import jungle.ovengers.repository.TodoRepository;
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

    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
                                .start(step())
                                .build();
    }

    @Bean
    public Step step() {
        return stepBuilderFactory.get("step")
                                 .tasklet(((contribution, chunkContext) -> {
                                     log.info("Step!");
                                     todoService.deleteDoneTodo();
                                     return RepeatStatus.FINISHED;
                                 }))
                                 .build();
    }
}
