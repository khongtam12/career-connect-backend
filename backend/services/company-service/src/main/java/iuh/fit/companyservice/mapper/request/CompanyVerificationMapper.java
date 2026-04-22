package iuh.fit.companyservice.mapper.request;

import iuh.fit.companyservice.dto.request.CompanyDTO;
import iuh.fit.companyservice.dto.request.VerifyCompanyDTO;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanySubscription;
import iuh.fit.companyservice.model.CompanyVerification;
import iuh.fit.companyservice.model.StatusVerification;

import java.time.LocalDateTime;

public class CompanyVerificationMapper {
    public static CompanyVerification toConvertCompanyVerification(VerifyCompanyDTO dto) {
        CompanyVerification c = new CompanyVerification();
        c.setCompany(new Company(dto.getCompanyId()));
        c.setSubmittedAt(LocalDateTime.now());
        c.setSubmittedTaxCode(dto.getSubmittedTaxCode());
        c.setBusinessLicense(dto.getBusinessLicense());
        c.setStatus(StatusVerification.PENDING);
        c.setNote(dto.getNote());
        return c;

    }
}
