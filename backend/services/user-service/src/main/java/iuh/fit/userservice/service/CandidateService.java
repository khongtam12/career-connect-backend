package iuh.fit.userservice.service;

import iuh.fit.userservice.dto.request.CandidateProfileUpdateRequest;
import iuh.fit.userservice.dto.response.CandidateProfileResponse;
import iuh.fit.userservice.dto.response.CandidateSummaryResponse;
import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.repository.CandidateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

    // ---- Backward-compatible method used by existing code ----
    public CandidateSummaryResponse findById(String id) {
        Candidate candidate = candidateRepository.findById(id).orElse(null);

        if (candidate == null) {
            throw new RuntimeException("Candidate not found: " + id);
        }
        return new CandidateSummaryResponse(
                candidate.getCandidateId(),
                candidate.getFullName(),
                String.valueOf(candidate.getExperienceYear()),
                candidate.getDateOfBirth() != null ? candidate.getDateOfBirth().toString() : null,
                candidate.getEmail(),
                candidate.getAvatar()
        );
    }

    // ---- New profile management methods ----

    public CandidateProfileResponse getProfile(String id) {
        Candidate c = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + id));
        return toProfileResponse(c);
    }

    @Transactional
    public CandidateProfileResponse updateProfile(String id, CandidateProfileUpdateRequest dto) {
        Candidate c = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found: " + id));

        c.setFullName(dto.getFullName());
        if (dto.getPhone() != null) c.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) c.setAvatar(dto.getAvatar());
        if (dto.getDateOfBirth() != null) c.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getAddress() != null) c.setAddress(dto.getAddress());
        if (dto.getCurrentJobTitle() != null) c.setCurrentJobTitle(dto.getCurrentJobTitle());
        c.setExperienceYear(dto.getExperienceYear());
        c.setExpectedSalary(dto.getExpectedSalary());
        c.setUpdatedAt(LocalDate.now());

        return toProfileResponse(candidateRepository.save(c));
    }

    private CandidateProfileResponse toProfileResponse(Candidate c) {
        return new CandidateProfileResponse(
                c.getCandidateId(),
                c.getEmail(),
                c.getFullName(),
                c.getPhone(),
                c.getAvatar(),
                c.getDateOfBirth(),
                c.getAddress(),
                c.getExperienceYear(),
                c.getCurrentJobTitle(),
                c.getExpectedSalary(),
                c.getStatus() != null ? c.getStatus().name() : null,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}
