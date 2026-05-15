package iuh.fit.paymentservice.controller;

import iuh.fit.paymentservice.config.VnpayConfig;
import iuh.fit.paymentservice.dto.request.InvoiceCreateRequest;
import iuh.fit.paymentservice.dto.response.VNPayResponse;
import iuh.fit.paymentservice.event.OrderPaymentSuccessEvent;
import iuh.fit.paymentservice.event.PaymentSuccessEvent;
import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.PaymentItem;
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
                if (payment.getStatus() == StatusPayment.SUCCEEDED) {
                    log.info("Payment {} already processed successfully, skipping duplicate callback", payment.getPaymentId());
                    return redirectToFrontend("success");
                }

                payment.setStatus(StatusPayment.SUCCEEDED);
                payment.setPaidAt(LocalDateTime.now());
                paymentService.save(payment);

                if (payment.getItems() != null && !payment.getItems().isEmpty()) {
                    for (PaymentItem item : payment.getItems()) {
                        PaymentSuccessEvent event = PaymentSuccessEvent.builder()
                                .paymentId(payment.getPaymentId())
                                .companyId(payment.getCompanyId())
                                .employerEmail(payment.getEmployerEmail())
                                .packageId(item.getPackageId())
                                .packageName(item.getPackageName())
                                .amount(item.getAmount())
                                .durationDays(item.getDurationDays())
                                .paidAt(payment.getPaidAt())
                                .jobPostLimit(item.getJobPostLimit())
                                .packageLabel(item.getPackageName())
                                .packageCategory(item.getPackageCategory())
                                .packageType(item.getPackageType())
                                .quantity(item.getQuantity())
                                .build();

                        log.info("Sending PaymentSuccessEvent for payment {} package {}", payment.getPaymentId(), item.getPackageId());
                        kafkaTemplate.send("payment-success", event);
                    }

                    OrderPaymentSuccessEvent orderEvent = OrderPaymentSuccessEvent.builder()
                            .paymentId(payment.getPaymentId())
                            .companyId(payment.getCompanyId())
                            .employerEmail(payment.getEmployerEmail())
                            .totalAmount(payment.getAmount())
                            .paidAt(payment.getPaidAt())
                            .items(payment.getItems().stream()
                                    .map(item -> OrderPaymentSuccessEvent.ItemEvent.builder()
                                            .packageId(item.getPackageId())
                                            .packageName(item.getPackageName())
                                            .durationDays(item.getDurationDays())
                                            .quantity(item.getQuantity())
                                            .amount(item.getAmount())
                                            .build())
                                    .toList())
                            .build();
                    log.info("Sending OrderPaymentSuccessEvent for payment {}", payment.getPaymentId());
                    kafkaTemplate.send("payment-order-success", orderEvent);
                }

                return redirectToFrontend("success");
            }

            payment.setStatus(StatusPayment.FAILED);
            paymentService.save(payment);
            return redirectToFrontend("failed");
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
