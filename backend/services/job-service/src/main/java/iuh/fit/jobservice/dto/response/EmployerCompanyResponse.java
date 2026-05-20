package iuh.fit.jobservice.dto.response;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployerCompanyResponse {
    private String employerId;
    private String companyId;
    private String email;
}
