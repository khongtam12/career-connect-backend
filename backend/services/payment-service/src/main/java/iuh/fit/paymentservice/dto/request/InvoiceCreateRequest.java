package iuh.fit.paymentservice.dto.request;

import iuh.fit.paymentservice.model.Method;
import lombok.Data;
import lombok.Getter;

import java.util.List;
@Data
@Getter
public class InvoiceCreateRequest {
    private String employerId;
    private Method paymentMethod;
    private Long amount;
    private String companyId;
    private String bankCode;
    private String packageId;
    private int durationDays;
}
