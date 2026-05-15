package iuh.fit.companyservice.mapper.request;

import iuh.fit.companyservice.dto.request.CompanyDTO;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanySubscription;
import iuh.fit.companyservice.model.StatusCompany;

import java.time.LocalDateTime;

public class CompanyMapper {
    public static Company toConvertCompany(CompanyDTO dto){
        Company company = new Company();
        company.setCompanyId(normalize(dto.getCompanyId()));
        company.setLogo(normalize(dto.getLogo()));
        company.setName(normalize(dto.getName()));
        company.setTaxCode(normalize(dto.getTaxCode()));
        company.setWebsite(normalize(dto.getWebsite()));
        company.setEmail(normalize(dto.getEmail()));
        company.setPhone(normalize(dto.getPhone()));
        company.setAddress(normalize(dto.getAddress()));
        company.setDescription(normalize(dto.getDescription()));
        company.setCompanySize(dto.getCompanySize() == null ? 0 : dto.getCompanySize());
        company.setFoundedYear(dto.getFoundedYear() == null ? 0 : dto.getFoundedYear());
        company.setStatusCompany(StatusCompany.PENDING);

        return company;
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
