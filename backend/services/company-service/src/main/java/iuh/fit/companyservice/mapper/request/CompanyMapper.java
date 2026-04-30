package iuh.fit.companyservice.mapper.request;

import iuh.fit.companyservice.dto.request.CompanyDTO;
import iuh.fit.companyservice.model.Company;
import iuh.fit.companyservice.model.CompanySubscription;
import iuh.fit.companyservice.model.StatusCompany;

import java.time.LocalDateTime;

public class CompanyMapper {
    public static Company toConvertCompany(CompanyDTO dto){
        Company company = new Company();
        company.setLogo(dto.getLogo());
        company.setName(dto.getName());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());
        company.setAddress(dto.getAddress());
        company.setDescription(dto.getDescription());
        company.setCompanySize(dto.getCompanySize());
        company.setStatusCompany(StatusCompany.PENDING);

        return company;
    }

}
