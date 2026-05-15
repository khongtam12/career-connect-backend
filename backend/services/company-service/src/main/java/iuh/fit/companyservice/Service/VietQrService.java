package iuh.fit.companyservice.Service;

import iuh.fit.companyservice.dto.response.VietQrResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class VietQrService {

    @Value("${vietqr.api.url:https://api.vietqr.io/v2/business/}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public VietQrResponseDTO verifyTaxCode(String taxCode) {
        try {
            String url = apiUrl + taxCode;
            log.info("Calling VietQR API: {}", url);
            
            ResponseEntity<VietQrResponseDTO> response = restTemplate.getForEntity(url, VietQrResponseDTO.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling VietQR API: {}", e.getMessage());
            throw new RuntimeException("Không thể tra cứu thông tin thuế từ VietQR: " + e.getMessage());
        }
    }
}
