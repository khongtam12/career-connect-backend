package iuh.fit.userservice.service;

import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.model.Status;
import iuh.fit.userservice.repository.CandidateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class AdminCandidateService {

    private final CandidateRepository candidateRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminCandidateService(CandidateRepository candidateRepository, PasswordEncoder passwordEncoder) {
        this.candidateRepository = candidateRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<Candidate> getCandidates(String keyword, Status status, Pageable pageable) {
        String statusStr = status != null ? status.name() : null;
        return candidateRepository.searchCandidates(keyword, statusStr, pageable);
    }

    @Transactional
    public Candidate updateStatus(String candidateId, Status status) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
        candidate.setStatus(status);
        return candidateRepository.save(candidate);
    }

    @Transactional
    public Candidate createCandidate(Candidate request) {
        if (candidateRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + request.getEmail());
        }
        
        Candidate candidate = new Candidate();
        // Generate an ID similar to C-UUID or just UUID if they don't have a specific sequence
        candidate.setCandidateId("CD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        candidate.setEmail(request.getEmail());
        candidate.setFullName(request.getFullName());
        candidate.setPhone(request.getPhone());
        
        String rawPassword = (request.getPassword() != null && !request.getPassword().trim().isEmpty()) 
                             ? request.getPassword() : "123456";
        candidate.setPassword(passwordEncoder.encode(rawPassword));
        
        // Additional fields
        candidate.setDateOfBirth(request.getDateOfBirth());
        candidate.setAddress(request.getAddress());
        candidate.setExperienceYear(request.getExperienceYear());
        candidate.setCurrentJobTitle(request.getCurrentJobTitle());
        candidate.setExpectedSalary(request.getExpectedSalary());
        candidate.setAvatar(request.getAvatar());
        
        candidate.setStatus(Status.ACTIVE);
        candidate.setCreatedAt(LocalDate.now());
        
        return candidateRepository.save(candidate);
    }

    @Transactional
    public Candidate updateCandidate(String candidateId, Candidate request) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
        
        candidate.setFullName(request.getFullName());
        candidate.setPhone(request.getPhone());
        candidate.setAvatar(request.getAvatar());
        candidate.setDateOfBirth(request.getDateOfBirth());
        candidate.setAddress(request.getAddress());
        candidate.setExperienceYear(request.getExperienceYear());
        candidate.setCurrentJobTitle(request.getCurrentJobTitle());
        candidate.setExpectedSalary(request.getExpectedSalary());
        
        // Don't update email and password here
        
        return candidateRepository.save(candidate);
    }

    @Transactional
    public Candidate resetPassword(String candidateId, String newPassword) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found with id: " + candidateId));
        
        String rawPassword = (newPassword != null && !newPassword.trim().isEmpty()) 
                             ? newPassword : "123456";
        candidate.setPassword(passwordEncoder.encode(rawPassword));
        return candidateRepository.save(candidate);
    }

    public java.util.Map<String, Object> getCandidateStats() {
        long total = candidateRepository.count();
        long active = candidateRepository.countByStatus(Status.ACTIVE);
        long banned = candidateRepository.countByStatus(Status.BANNED);
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("banned", banned);
        stats.put("newToday", 0); // Placeholder, can be implemented with a query by date
        return stats;
    }
}
