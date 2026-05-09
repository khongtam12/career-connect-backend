package iuh.fit.paymentservice.dto.request;

import iuh.fit.paymentservice.model.Method;
import lombok.Data;


@Data
public class InvoiceCreateRequest {
    private String employerId;
    private String employerEmail;
    private Method paymentMethod;
    private Long amount;
    private String companyId;
    private String bankCode;
    private String packageId;
    private int durationDays;
}
