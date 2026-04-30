package iuh.fit.companyservice.repository;

import iuh.fit.companyservice.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import iuh.fit.companyservice.model.ApprovalStatus;

@Repository
public interface CompanyRepository extends JpaRepository<Company,String> {
    Page<Company> findByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
}
