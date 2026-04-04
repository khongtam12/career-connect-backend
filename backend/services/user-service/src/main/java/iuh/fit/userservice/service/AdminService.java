package iuh.fit.userservice.service;

import iuh.fit.userservice.model.Admin;
import iuh.fit.userservice.repository.AdminRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService  {
    private final AdminRepository adminRepository;

    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }


    public  List<Admin> findAll(){
        return adminRepository.findAll();
    };
}
