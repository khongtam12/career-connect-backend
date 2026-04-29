package iuh.fit.userservice.service;

import iuh.fit.userservice.dto.response.CandidateSummaryResponse;
import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.repository.CandidateRepository;
import org.springframework.stereotype.Service;

@Service
public class CandidateService {
    private final CandidateRepository candidateRepository;

    public CandidateService(CandidateRepository candidateRepository) {
        this.candidateRepository = candidateRepository;
    }

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
                candidate.getEmail()
        );
    }
}
