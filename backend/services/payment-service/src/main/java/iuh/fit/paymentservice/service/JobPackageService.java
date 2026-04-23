package iuh.fit.paymentservice.service;

import iuh.fit.paymentservice.dto.response.JobPackageResponseDTO;
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






}
