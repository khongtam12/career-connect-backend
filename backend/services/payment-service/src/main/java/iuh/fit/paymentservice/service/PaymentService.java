package iuh.fit.paymentservice.service;

import iuh.fit.paymentservice.dto.request.InvoiceCreateRequest;
import iuh.fit.paymentservice.dto.response.VNPayResponse;
import iuh.fit.paymentservice.model.JobPackage;
import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.StatusPayment;
import iuh.fit.paymentservice.repository.PaymentRepository;
import iuh.fit.paymentservice.strategy.PaymentStrategy;
import iuh.fit.paymentservice.strategy.PaymentStrategyFactory;
import iuh.fit.paymentservice.util.IdGenerator;
import iuh.fit.paymentservice.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.configuration.AbstractFileConfiguration;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory factory;
    public VNPayResponse createVnPayPayment(InvoiceCreateRequest req, HttpServletRequest request) {
        String txnRef = VnpayUtil.getRandomNumer(8);
        Payment payment = Payment.builder()
                .paymentId(IdGenerator.generatorIdPayment())
                .companyId(req.getCompanyId())
                .transactionCode(txnRef)
                .amount(req.getAmount())
                .durationDays(req.getDurationDays())
                .method(req.getPaymentMethod())
                .employerEmail(req.getEmployerEmail())
                .jobPackage(new JobPackage(req.getPackageId()))
                .status(StatusPayment.PENDING)
                .build();

        paymentRepository.save(payment);

        PaymentStrategy strategy = factory.getStrategy(req.getPaymentMethod());
        VNPayResponse response = (VNPayResponse) strategy.pay(payment, request);

        return response;

    }
    public Payment findByTransactionCode(String txnRef) {
        return paymentRepository.findByTransactionCode(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }
}
