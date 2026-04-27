package iuh.fit.userservice.config;

import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.model.Admin;
import iuh.fit.userservice.model.Employer;
import iuh.fit.userservice.model.Status;
import iuh.fit.userservice.repository.CandidateRepository;
import iuh.fit.userservice.repository.AdminRepository;
import iuh.fit.userservice.repository.EmployerRepository;
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
    private final AdminRepository adminRepository;
    private final EmployerRepository employerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Create Test Candidate
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
            }

            // Create Test Admin
            String adminEmail = "admin2@test.com";
            if (adminRepository.findByEmail(adminEmail).isEmpty()) {
                Admin admin = new Admin();
                admin.setAdminId(UUID.randomUUID().toString());
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setFullName("System Admin");
                adminRepository.save(admin);
                log.info("Successfully created test admin account: " + adminEmail);
            }

            // Create Test Employer
            String employerEmail = "employer2@test.com";
            if (employerRepository.findByEmail(employerEmail).isEmpty()) {
                Employer employer = new Employer();
                employer.setEmployerId(UUID.randomUUID().toString());
                employer.setEmail(employerEmail);
                employer.setPassword(passwordEncoder.encode("123456"));
                employer.setFullName("Test Employer");
                employer.setStatus(Status.ACTIVE);
                employerRepository.save(employer);
                log.info("Successfully created test employer account: " + employerEmail);
            }
        };
    }
}
