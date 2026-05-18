package iuh.fit.paymentservice.service;

import iuh.fit.paymentservice.dto.response.JobPackageResponseDTO;
import iuh.fit.paymentservice.dto.request.JobPackageUpdateRequest;
import iuh.fit.paymentservice.mapper.response.JobPackageMapper;
import iuh.fit.paymentservice.model.JobPackage;
import iuh.fit.paymentservice.repository.JobPackageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobPackageService {
    private final JobPackageRepository jobPackageRepository;
    private  final JobPackageMapper jobPackageMapper;

    public JobPackageService(JobPackageRepository jobPackageRepository, JobPackageMapper jobPackageMapper) {
        this.jobPackageRepository = jobPackageRepository;
        this.jobPackageMapper = jobPackageMapper;
    }

    public List<JobPackageResponseDTO> getAllJobPackage(){
        return jobPackageRepository.findAll()
                .stream()
                .map(jobPackageMapper::toDTO)
                .toList();

    }

    public JobPackageResponseDTO getJobPackageById(String packageId) {
        JobPackage jobPackage = jobPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Job package not found"));
        return jobPackageMapper.toDTO(jobPackage);
    }

    public JobPackageResponseDTO updateJobPackage(String packageId, JobPackageUpdateRequest request) {
        JobPackage jobPackage = jobPackageRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Job package not found"));

        if (request.getName() != null) {
            jobPackage.setName(request.getName().trim());
        }
        if (request.getDescription() != null) {
            jobPackage.setDescription(request.getDescription().trim());
        }
        if (request.getPrice() != null) {
            jobPackage.setPrice(request.getPrice());
        }

        jobPackage.setOldPrice(request.getOldPrice());

        if (request.getDurationDays() != null) {
            jobPackage.setDurationDays(request.getDurationDays());
        }
        if (request.getJobPostLimit() != null) {
            jobPackage.setJobPostLimit(request.getJobPostLimit());
        }
        if (request.getCategory() != null) {
            jobPackage.setCategory(request.getCategory());
        }
        if (request.getType() != null) {
            jobPackage.setType(request.getType());
        }
        if (request.getBadge() != null) {
            jobPackage.setBadge(request.getBadge().trim());
        }
        if (request.getBadgeColor() != null) {
            jobPackage.setBadgeColor(request.getBadgeColor().trim());
        }
        if (request.getImageUrl() != null) {
            jobPackage.setImageUrl(request.getImageUrl().trim());
        }
        if (request.getShowDetails() != null) {
            jobPackage.setShowDetails(request.getShowDetails());
        }
        if (request.getActive() != null) {
            jobPackage.setActive(request.getActive());
        }
        if (request.getAllowedBoxTypes() != null) {
            jobPackage.setAllowedBoxTypes(request.getAllowedBoxTypes());
        }

        JobPackage updated = jobPackageRepository.save(jobPackage);
        return jobPackageMapper.toDTO(updated);
    }






}
