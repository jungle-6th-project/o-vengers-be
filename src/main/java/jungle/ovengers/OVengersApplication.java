package jungle.ovengers;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableBatchProcessing
@EnableCaching
@EnableAsync
@EnableJpaAuditing
@SpringBootApplication
public class OVengersApplication {

    public static void main(String[] args) {
        SpringApplication.run(OVengersApplication.class, args);
    }

}
