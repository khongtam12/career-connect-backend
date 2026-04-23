package iuh.fit.userservice.config;

import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.model.Status;
import iuh.fit.userservice.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            String testEmail = "candidate@test.com";
            if (candidateRepository.findByEmail(testEmail).isEmpty()) {
                Candidate candidate = new Candidate();
                candidate.setCandidateId(UUID.randomUUID().toString());
                candidate.setEmail(testEmail);
                candidate.setPassword(passwordEncoder.encode("123456"));
                candidate.setFullName("Test Candidate");
                candidate.setStatus(Status.ACTIVE);
                candidate.setCreatedAt(LocalDate.now());
                candidate.setUpdatedAt(LocalDate.now());
                
                candidateRepository.save(candidate);
                log.info("Successfully created test candidate account: " + testEmail);
            } else {
                log.info("Test candidate account already exists: " + testEmail);
            }
        };
    }
}
