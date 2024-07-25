package site.mymeetup.meetupserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
public class MeetupServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MeetupServerApplication.class, args);
    }

}
