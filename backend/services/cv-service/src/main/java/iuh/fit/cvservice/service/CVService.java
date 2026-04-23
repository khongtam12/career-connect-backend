package iuh.fit.cvservice.service;

import iuh.fit.cvservice.model.CV;
import iuh.fit.cvservice.repository.CVRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CVService {

    private final CVRepository cvRepository;

    @Transactional(readOnly = true)
    public List<CV> getCVsByUserId(UUID userId) {
        List<CV> cvs = cvRepository.findByUserId(userId);
        cvs.forEach(cv -> {
            cv.getSkills().size();
            cv.getExperiences().size();
            cv.getEducations().size();
            cv.getProjects().size();
            cv.getCertificates().size();
        });
        return cvs;
    }

    public CV getCVById(UUID id) {
        return cvRepository.findById(id).orElseThrow(() -> new RuntimeException("CV not found"));
    }

    @Transactional
    public CV saveCV(CV cv) {
        CV savedCV;
        if (cv.getId() == null) {
            cv.setUpdatedAt(LocalDateTime.now());
            // For new CV, ensure relationships are set
            if (cv.getSkills() != null) cv.getSkills().forEach(s -> s.setCv(cv));
            if (cv.getExperiences() != null) cv.getExperiences().forEach(e -> e.setCv(cv));
            if (cv.getEducations() != null) cv.getEducations().forEach(e -> e.setCv(cv));
            if (cv.getProjects() != null) cv.getProjects().forEach(p -> p.setCv(cv));
            if (cv.getCertificates() != null) cv.getCertificates().forEach(c -> c.setCv(cv));
            savedCV = cvRepository.save(cv);
        } else {
            CV existingCV = getCVById(cv.getId());
            // Update basic info
            existingCV.setName(cv.getName());
            existingCV.setTemplateId(cv.getTemplateId());
            existingCV.setAvatarUrl(cv.getAvatarUrl());
            existingCV.setStatus(cv.getStatus());
            existingCV.setUpdatedAt(LocalDateTime.now());
            
            // Personal Info
            existingCV.setFullName(cv.getFullName());
            existingCV.setEmail(cv.getEmail());
            existingCV.setPhone(cv.getPhone());
            existingCV.setAddress(cv.getAddress());
            existingCV.setDob(cv.getDob());
            existingCV.setJobTitle(cv.getJobTitle());
            existingCV.setLinkedin(cv.getLinkedin());
            existingCV.setSummary(cv.getSummary());

            // Handle sub-sections (Set is better for orphan removal)
            existingCV.getSkills().clear();
            if (cv.getSkills() != null) {
                cv.getSkills().forEach(s -> s.setCv(existingCV));
                existingCV.getSkills().addAll(cv.getSkills());
            }

            existingCV.getExperiences().clear();
            if (cv.getExperiences() != null) {
                cv.getExperiences().forEach(e -> e.setCv(existingCV));
                existingCV.getExperiences().addAll(cv.getExperiences());
            }

            existingCV.getEducations().clear();
            if (cv.getEducations() != null) {
                cv.getEducations().forEach(e -> e.setCv(existingCV));
                existingCV.getEducations().addAll(cv.getEducations());
            }

            existingCV.getProjects().clear();
            if (cv.getProjects() != null) {
                cv.getProjects().forEach(p -> p.setCv(existingCV));
                existingCV.getProjects().addAll(cv.getProjects());
            }

            existingCV.getCertificates().clear();
            if (cv.getCertificates() != null) {
                cv.getCertificates().forEach(c -> c.setCv(existingCV));
                existingCV.getCertificates().addAll(cv.getCertificates());
            }

            savedCV = cvRepository.save(existingCV);
        }

        // Initialize collections for the JSON response
        if (savedCV.getSkills() != null) savedCV.getSkills().size();
        if (savedCV.getExperiences() != null) savedCV.getExperiences().size();
        if (savedCV.getEducations() != null) savedCV.getEducations().size();
        if (savedCV.getProjects() != null) savedCV.getProjects().size();
        if (savedCV.getCertificates() != null) savedCV.getCertificates().size();

        return savedCV;
    }

    @Transactional
    public void deleteCV(UUID id) {
        cvRepository.deleteById(id);
    }
}
