package iuh.fit.paymentservice.controller;

import iuh.fit.paymentservice.Client.CompanyClient;
import iuh.fit.paymentservice.config.VnpayConfig;
import iuh.fit.paymentservice.dto.request.CompanySubscriptionRequest;
import iuh.fit.paymentservice.dto.request.InvoiceCreateRequest;
import iuh.fit.paymentservice.dto.response.VNPayResponse;
import iuh.fit.paymentservice.event.PaymentSuccessEvent;
import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.StatusPayment;
import iuh.fit.paymentservice.service.PaymentService;
import iuh.fit.paymentservice.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@RestController
@RequestMapping("/api/v1/package/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final PaymentService paymentService;
    private final VnpayConfig vnPayConfig;
    private final CompanyClient companyClient;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${FRONTEND_URL}")
    private String frontendUrl;

    @PostMapping("/vnpay")
    public ResponseEntity<?> pay(
            @RequestBody InvoiceCreateRequest request,
            HttpServletRequest httpRequest) {
        try {
            VNPayResponse response = paymentService.createVnPayPayment(request, httpRequest);

            if (response == null || response.getPaymentUrl() == null || response.getCode() == null) {
                log.error("VNPay response invalid");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Loi tao URL thanh toan VNPAY");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Loi tao VNPay URL: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Loi tao URL thanh toan VNPAY");
        }
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<?> vnpayCallback(@RequestParam Map<String, String> params) {
        log.info("VNPay callback params: {}", params);

        String vnpSecureHash = params.get("vnp_SecureHash");

        Map<String, String> verifyParams = new TreeMap<>(params);
        verifyParams.remove("vnp_SecureHash");
        verifyParams.remove("vnp_SecureHashType");

        String queryUrl = VnpayUtil.getPaymentURL(verifyParams, false);
        String calculatedHash = VnpayUtil.hmacSHA512(vnPayConfig.getSecretKey(), queryUrl);

        if (!calculatedHash.equals(vnpSecureHash)) {
            log.error("Invalid signature. Expected: {}, Actual: {}", calculatedHash, vnpSecureHash);
            return redirectToFrontend("failed");
        }

        String txnRef = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");

        if (txnRef == null) {
            return redirectToFrontend("failed");
        }

        try {
            Payment payment = paymentService.findByTransactionCode(txnRef);

            if ("00".equals(responseCode)) {
                payment.setStatus(StatusPayment.SUCCEEDED);
                payment.setPaidAt(LocalDateTime.now());
                paymentService.save(payment);

                String email = payment.getEmployerEmail();
                PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                        .paymentId(payment.getPaymentId())
                        .companyId(payment.getCompanyId())
                        .employerEmail(email)
                        .packageId(payment.getJobPackage().getPackageId())
                        .packageName(payment.getJobPackage().getName())
                        .amount(payment.getAmount())
                        .packageLabel(payment.getJobPackage().getBadge() != null
                                ? payment.getJobPackage().getBadge()
                                : payment.getJobPackage().getName())
                        .jobPostLimit(payment.getJobPackage().getJobPostLimit())
                        .durationDays(payment.getDurationDays())
                        .paidAt(LocalDateTime.now())
                        .build();
                System.out.println(event.getEmployerEmail());
                log.info("Bootstrap: {}", bootstrapServers);
                kafkaTemplate.send("payment-success", event);
                return redirectToFrontend("success");

            } else {
                payment.setStatus(StatusPayment.FAILED);
            }

            paymentService.save(payment);

            return redirectToFrontend("00".equals(responseCode) ? "success" : "failed");
        } catch (Exception e) {
            log.error("Callback error: {}", e.getMessage(), e);
            return redirectToFrontend("failed");
        }
    }

    private ResponseEntity<Void> redirectToFrontend(String paymentStatus) {
        String redirectUrl = UriComponentsBuilder.fromHttpUrl(frontendUrl)
                .path("/employer")
                .queryParam("paymentStatus", paymentStatus)
                .build()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();
    }
}
