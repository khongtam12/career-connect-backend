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
import java.util.List;
import java.util.stream.IntStream;

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
            final LocalDate today = LocalDate.now();
            final String encodedPassword = passwordEncoder.encode("123456");

            List<Candidate> candidates = IntStream.rangeClosed(1, 5)
                    .mapToObj(i -> {
                        Candidate candidate = new Candidate();
                        candidate.setCandidateId(String.format("CAN%03d", i));
                        candidate.setEmail(String.format("candidate%02d@test.com", i));
                        candidate.setPassword(encodedPassword);
                        candidate.setFullName(String.format("Candidate %02d", i));
                        candidate.setPhone(String.format("0900000%03d", i));
                        candidate.setAvatar("https://via.placeholder.com/120?text=CAND");
                        candidate.setCreatedAt(today);
                        candidate.setUpdatedAt(today);
                        candidate.setStatus(Status.ACTIVE);
                        candidate.setDateOfBirth(today.minusYears(22 + i));
                        candidate.setAddress("Ho Chi Minh City");
                        candidate.setExperienceYear(i);
                        candidate.setCurrentJobTitle("Software Engineer");
                        candidate.setExpectedSalary(12000000 + (i * 2000000L));
                        return candidate;
                    })
                    .toList();

            candidates.forEach(candidate -> {
                if (candidateRepository.findByEmail(candidate.getEmail()).isEmpty()) {
                    candidateRepository.save(candidate);
                    log.info("Successfully created candidate account: {}", candidate.getEmail());
                }
            });

            List<Employer> employers = IntStream.rangeClosed(1, 20)
                    .mapToObj(i -> {
                        Employer employer = new Employer();
                        employer.setEmployerId(String.format("EMP%03d", i));
                        employer.setEmail(String.format("employer%02d@test.com", i));
                        employer.setPassword(encodedPassword);
                        employer.setFullName(String.format("Employer %02d", i));
                        employer.setPhone(String.format("0910000%03d", i));
                        employer.setAvatar("https://via.placeholder.com/120?text=EMP");
                        employer.setCreatedAt(today);
                        employer.setUpdatedAt(today);
                        employer.setStatus(Status.ACTIVE);
                        employer.setPosition("HR Manager");
                        employer.setCompanyId(String.format("COMP%03d", i));
                        return employer;
                    })
                    .toList();

            employers.forEach(employer -> {
                if (employerRepository.findByEmail(employer.getEmail()).isEmpty()) {
                    employerRepository.save(employer);
                    log.info("Successfully created employer account: {}", employer.getEmail());
                }
            });

            List<Admin> admins = IntStream.rangeClosed(1, 2)
                    .mapToObj(i -> {
                        Admin admin = new Admin();
                        admin.setAdminId(String.format("ADM%03d", i));
                        admin.setEmail(String.format("admin%02d@test.com", i));
                        admin.setPassword(encodedPassword);
                        admin.setFullName(String.format("System Admin %02d", i));
                        admin.setPhone(String.format("0980000%03d", i));
                        admin.setAvatar("https://via.placeholder.com/120?text=ADM");
                        admin.setCreatedAt(today);
                        admin.setUpdatedAt(today);
                        admin.setStatus(Status.ACTIVE);
                        return admin;
                    })
                    .toList();

            admins.forEach(admin -> {
                if (adminRepository.findByEmail(admin.getEmail()).isEmpty()) {
                    adminRepository.save(admin);
                    log.info("Successfully created admin account: {}", admin.getEmail());
                }
            });
        };
    }
}
