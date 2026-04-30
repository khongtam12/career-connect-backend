package iuh.fit.userservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterPageDTO {
    private List<RecruiterResponseDTO> content;
    private int page;
    private int size;
    private long totalElements;
}
