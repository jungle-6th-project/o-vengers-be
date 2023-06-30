package jungle.ovengers.config.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

@Slf4j
@Configuration
public class FirebaseConfig {
    private static final String FCM_ADMIN_SDK_PATH = "o-vengers-firebase-adminsdk-gpcsd-dc4c8a5963.json";

    @Bean
    public FirebaseApp init() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(new ClassPathResource(FCM_ADMIN_SDK_PATH)
                                                                                   .getInputStream())
                                                               .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));
        FirebaseOptions options = FirebaseOptions.builder()
                                                 .setCredentials(googleCredentials)
                                                 .build();

        return FirebaseApp.initializeApp(options);
    }
}
