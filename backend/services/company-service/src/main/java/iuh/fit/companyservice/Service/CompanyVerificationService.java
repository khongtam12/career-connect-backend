package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.dto.request.VerifyCompanyDTO;
import iuh.fit.companyservice.model.CompanyVerification;
import iuh.fit.companyservice.repository.CompanyVerificationRepository;
import iuh.fit.companyservice.util.IdGenerator;
import org.springframework.stereotype.Service;

@Service
public class CompanyVerificationService {
private final CompanyVerificationRepository companyVerificationRepository;


    public CompanyVerificationService(CompanyVerificationRepository companyVerificationRepository) {
        this.companyVerificationRepository = companyVerificationRepository;
    }
    public CompanyVerification save(CompanyVerification companyVerification) {
        companyVerificationRepository.findByCompanyCompanyId(
                companyVerification.getCompany().getCompanyId()
        ).ifPresent(existing -> {
            companyVerification.setVerificationId(existing.getVerificationId());
            companyVerification.setVerifiedAt(existing.getVerifiedAt());
            companyVerification.setVerifiedBy(existing.getVerifiedBy());
        });

        if (companyVerification.getVerificationId() == null || companyVerification.getVerificationId().isBlank()) {
            companyVerification.setVerificationId(IdGenerator.generatorIdCompannyVerified());
        }

        return companyVerificationRepository.save(companyVerification);

    }
}
