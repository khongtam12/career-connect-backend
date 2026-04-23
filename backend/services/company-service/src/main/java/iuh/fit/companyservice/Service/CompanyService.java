package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.client.EmployerClient;
import iuh.fit.companyservice.dto.request.EmployerCompanyRequest;
import iuh.fit.companyservice.dto.response.EmployerResponse;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.repository.CompanyRepository;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final S3Service s3Service;
    private final EmployerClient employerClient;


    public CompanyService(CompanyRepository companyRepository, S3Service s3Service,  EmployerClient employerClient) {
        this.companyRepository = companyRepository;
        this.s3Service = s3Service;
        this.employerClient = employerClient;
    }
    public Company saveCompany(Company company,String employerId) {
        if (company.getCompanyId() == null || company.getCompanyId().isBlank()) {
            company.setCompanyId(IdGenerator.generatorIdCompanny());
            company.setCreatedAt(LocalDateTime.now());
        }
        Company savedCompany = companyRepository.save(company);
        EmployerCompanyRequest request = new EmployerCompanyRequest();
        request.setCompanyId(company.getCompanyId());
        request.setEmployerId(employerId);
        employerClient.saveEmployerCompany(request);
        return savedCompany;
    }
    public Company getCompanyById(String id) {

        return companyRepository.findById(id).orElse(null);
    }
    public void updateCompanyLogo(String companyId, String newLogoUrl) {

        Company company = companyRepository.findById(companyId).orElseThrow();

        String oldLogo = company.getLogo();

        company.setLogo(newLogoUrl);
        companyRepository.save(company);

        // xóa async để tránh delay API
        if (oldLogo != null && oldLogo.contains("/")) {
            String key = oldLogo.substring(oldLogo.lastIndexOf("/") + 1);
            s3Service.deleteFile(key);
        }
    }
}
