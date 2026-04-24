package iuh.fit.userservice.mapper;


import iuh.fit.userservice.dto.response.UserDTO;
import iuh.fit.userservice.model.Admin;
import iuh.fit.userservice.model.Candidate;
import iuh.fit.userservice.model.Employer;

public class UserMapper {
    public static UserDTO fromAdmin(Admin admin){
        return new UserDTO(admin.getAdminId(),
                            admin.getEmail(),
                            admin.getFullName(),
                            admin.getPhone(),
                            admin.getAvatar(),
                            "ADMIN",
                            null
                );
    }
    public static UserDTO fromCandidate(Candidate candidate){
        return new UserDTO(candidate.getCandidateId(),
                candidate.getEmail(),
                candidate.getFullName(),
                candidate.getPhone(),
                candidate.getAvatar(),
                "CANDIDATE",
                null
        );
    }
    public static UserDTO fromEmployer(Employer employer){
        return new UserDTO(employer.getEmployerId(),
                employer.getEmail(),
                employer.getFullName(),
                employer.getPhone(),
                employer.getAvatar(),
                "EMPLOYER",
                employer.getCompanyId()
        );
    }
}
