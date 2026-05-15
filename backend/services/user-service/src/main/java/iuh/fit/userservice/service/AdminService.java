package iuh.fit.userservice.service;

import iuh.fit.userservice.dto.request.AdminCreateRequest;
import iuh.fit.userservice.dto.request.AdminProfileUpdateRequest;
import iuh.fit.userservice.dto.response.AdminResponse;
import iuh.fit.userservice.model.Admin;
import iuh.fit.userservice.model.Status;
import iuh.fit.userservice.repository.AdminRepository;
import iuh.fit.userservice.util.IdGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class AdminService {
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminService(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public List<AdminResponse> findAllDto() {
        return adminRepository.findAll().stream().map(this::toResponse).toList();
    }

    public AdminResponse getById(String id) {
        Admin a = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found: " + id));
        return toResponse(a);
    }

    @Transactional
    public AdminResponse updateProfile(String id, AdminProfileUpdateRequest dto) {
        Admin a = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Admin not found: " + id));
        a.setFullName(dto.getFullName());
        if (dto.getPhone() != null) a.setPhone(dto.getPhone());
        if (dto.getAvatar() != null) a.setAvatar(dto.getAvatar());
        a.setUpdatedAt(LocalDate.now());
        return toResponse(adminRepository.save(a));
    }

    @Transactional
    public AdminResponse createAdmin(AdminCreateRequest dto) {
        if (adminRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + dto.getEmail());
        }
        Admin a = new Admin();
        a.setAdminId(IdGenerator.generatorIdAdmin());
        a.setEmail(dto.getEmail());
        a.setPassword(passwordEncoder.encode(dto.getPassword()));
        a.setFullName(dto.getFullName());
        a.setPhone(dto.getPhone());
        a.setAvatar(dto.getAvatar());
        a.setStatus(Status.ACTIVE);
        a.setCreatedAt(LocalDate.now());
        a.setUpdatedAt(LocalDate.now());
        return toResponse(adminRepository.save(a));
    }

    private AdminResponse toResponse(Admin a) {
        return new AdminResponse(
                a.getAdminId(),
                a.getEmail(),
                a.getFullName(),
                a.getPhone(),
                a.getAvatar(),
                a.getStatus() != null ? a.getStatus().name() : null,
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
