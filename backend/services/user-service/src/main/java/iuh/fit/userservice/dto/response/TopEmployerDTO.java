package iuh.fit.userservice.dto.response;

import lombok.Data;

@Data
public class TopEmployerDTO {
    private String companyId;
    private String companyName;
    private String companyLogo;
    private String email;
    private Long jobCount;
    private Long viewCount;
    private Integer rank;
}
