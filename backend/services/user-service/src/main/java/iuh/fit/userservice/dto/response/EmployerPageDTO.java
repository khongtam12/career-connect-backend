package iuh.fit.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployerPageDTO {
    private List<EmployerResponseDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
