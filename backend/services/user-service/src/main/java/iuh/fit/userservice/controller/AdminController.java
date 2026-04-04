package iuh.fit.userservice.controller;

import iuh.fit.userservice.model.Admin;
import iuh.fit.userservice.service.AdminService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public List<Admin> findAll(){
        return adminService.findAll();

    }
}
