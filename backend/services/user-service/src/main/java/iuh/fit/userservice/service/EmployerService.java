package iuh.fit.userservice.service;

import iuh.fit.userservice.model.Employer;
import iuh.fit.userservice.repository.EmployerRepository;
import org.springframework.stereotype.Service;

@Service
public class EmployerService {

    private final EmployerRepository employerRepository;

    public EmployerService(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }
    public Employer save(Employer employer) {
        return employerRepository.save(employer);
    }
    public Employer findById(String id) {
        return employerRepository.findById(id).orElse(null);
    }
}
