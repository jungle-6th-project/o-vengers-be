package jungle.ovengers.config.batch;

import jungle.ovengers.entity.MemberEntity;
import jungle.ovengers.enums.MemberStatus;
import jungle.ovengers.model.request.RoomBrowseRequest;
import jungle.ovengers.repository.GroupRepository;
import jungle.ovengers.repository.MemberRepository;
import jungle.ovengers.service.MemberWithdrawService;
import jungle.ovengers.service.NotificationService;
import jungle.ovengers.service.RoomService;
import jungle.ovengers.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
    private final GroupRepository groupRepository;
    private final MemberRepository memberRepository;

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final EntityManagerFactory entityManagerFactory;
    private final TodoService todoService;
    private final RoomService roomService;
    private final NotificationService notificationService;
    private final MemberWithdrawService memberWithdrawService;

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
    public Job sendPushMessagesJob() {
        return jobBuilderFactory.get("sendPushMessagesJob")
                                .start(sendPushMessagesStep())
                                .build();
    }

    @Bean
    public Job memberWithdrawJob() {
        return jobBuilderFactory.get("memberJob")
                                .start(memberWithdrawStep(memberWithdrawItemReader(memberRepository), memberWithdrawItemProcessor(), memberWithdrawItemWriter(memberRepository)))
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

    @Bean
    public Step sendPushMessagesStep() {
        return stepBuilderFactory.get("getGroupRoomsStep")
                                 .tasklet(((contribution, chunkContext) -> {
                                     log.info("called sendPushMessagesStep()");
                                     notificationService.sendEnterTimePushAlarm(LocalDateTime.now()
                                                                                             .truncatedTo(ChronoUnit.MINUTES));
                                     return RepeatStatus.FINISHED;
                                 }))
                                 .build();
    }

    @Bean
    public Step withdrawFailedMemberStep() {
        return stepBuilderFactory.get("getGroupRoomsStep")
                                 .tasklet(((contribution, chunkContext) -> {
                                     log.info("called deleteWithdrawFailMemberStep()");
                                     memberWithdrawService.withdrawFailedMembers();
                                     return RepeatStatus.FINISHED;
                                 }))
                                 .build();
    }

    @Bean
    public Step memberWithdrawStep(ItemReader<MemberEntity> memberReader, ItemProcessor<MemberEntity, MemberEntity> memberProcessor,
                                   ItemWriter<MemberEntity> memberWriter) {
        return stepBuilderFactory.get("memberStep")
                                 .<MemberEntity, MemberEntity>chunk(10)
                                 .reader(memberReader)
                                 .processor(memberProcessor)
                                 .writer(memberWriter)
                                 .build();
    }

    @Bean
    public ItemReader<MemberEntity> memberWithdrawItemReader(MemberRepository memberRepository) {
        JpaPagingItemReader<MemberEntity> reader = new JpaPagingItemReader<>();
        reader.setEntityManagerFactory(entityManagerFactory);
        reader.setPageSize(10);
        reader.setQueryString("SELECT m FROM MemberEntity m WHERE m.status = :status");
        reader.setParameterValues(Collections.singletonMap("status", MemberStatus.SEPARATE));

        return reader;
    }

    /*
    * Todo: 별도의 트랜잭션이 열리지 않도록 수정해야 함 -> processor에서 매번 DB 반영하지 않고, ItemWriter에서 한번에 반영되어지도록
    */
    @Bean
    public ItemProcessor<MemberEntity, MemberEntity> memberWithdrawItemProcessor() {
        return memberEntity -> {
            memberEntity.delete();
            memberWithdrawService.deleteAssociations(memberEntity);
            return memberEntity;
        };
    }

    @Bean
    public ItemWriter<MemberEntity> memberWithdrawItemWriter(MemberRepository memberRepository) {
        return memberRepository::saveAll;
    }
}
