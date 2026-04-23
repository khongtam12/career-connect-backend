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

    public List<CV> getCVsByUserId(UUID userId) {
        return cvRepository.findByUserId(userId);
    }

    public CV getCVById(UUID id) {
        return cvRepository.findById(id).orElseThrow(() -> new RuntimeException("CV not found"));
    }

    @Transactional
    public CV saveCV(CV cv) {
        if (cv.getId() == null) {
            cv.setUpdatedAt(LocalDateTime.now());
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

            // Handle sub-sections (Orphan removal will handle deletion)
            // Clear and re-add to ensure data consistency
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

            return cvRepository.save(existingCV);
        }

        // For new CV, ensure relationships are set
        if (cv.getSkills() != null) cv.getSkills().forEach(s -> s.setCv(cv));
        if (cv.getExperiences() != null) cv.getExperiences().forEach(e -> e.setCv(cv));
        if (cv.getEducations() != null) cv.getEducations().forEach(e -> e.setCv(cv));
        if (cv.getProjects() != null) cv.getProjects().forEach(p -> p.setCv(cv));
        if (cv.getCertificates() != null) cv.getCertificates().forEach(c -> c.setCv(cv));

        return cvRepository.save(cv);
    }

    @Transactional
    public void deleteCV(UUID id) {
        cvRepository.deleteById(id);
    }
}
