package com.chorewebpage.config;

import com.chorewebpage.model.Chore;
import com.chorewebpage.model.Frequency;
import com.chorewebpage.model.Kid;
import com.chorewebpage.repository.ChoreRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    CommandLineRunner seedChores(ChoreRepository repository) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }
            LocalDateTime now = LocalDateTime.now();
            List<Chore> chores = List.of(
                    new Chore("Dishes", Frequency.DAILY, Kid.ONE, now.plusDays(1)),
                    new Chore("Trash", Frequency.EVERY_THREE_DAYS, Kid.TWO, now.plusDays(3)),
                    new Chore("Counters", Frequency.DAILY, Kid.TWO, now.plusDays(1)),
                    new Chore("Sweep", Frequency.EVERY_THREE_DAYS, Kid.ONE, now.plusDays(3)));
            repository.saveAll(chores);
            log.info("Seeded default chores: {}", chores.size());
        };
    }
}
