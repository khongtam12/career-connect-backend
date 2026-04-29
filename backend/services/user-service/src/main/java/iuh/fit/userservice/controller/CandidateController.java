package iuh.fit.userservice.controller;

import iuh.fit.userservice.dto.response.CandidateSummaryResponse;
import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.service.CandidateService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/candidate")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @GetMapping("/{candidateId}")
    public CandidateSummaryResponse getCandidateById(@PathVariable String candidateId) {
        CandidateSummaryResponse candidate = candidateService.findById(candidateId);
        return  candidate;
    }
}
