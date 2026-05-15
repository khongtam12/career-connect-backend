package iuh.fit.paymentservice.service;

import iuh.fit.paymentservice.dto.request.InvoiceCreateRequest;
import iuh.fit.paymentservice.dto.request.PaymentItemRequest;
import iuh.fit.paymentservice.dto.response.VNPayResponse;
import iuh.fit.paymentservice.model.JobPackage;
import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.PaymentItem;
import iuh.fit.paymentservice.model.StatusPayment;
import iuh.fit.paymentservice.repository.JobPackageRepository;
import iuh.fit.paymentservice.repository.PaymentRepository;
import iuh.fit.paymentservice.strategy.PaymentStrategy;
import iuh.fit.paymentservice.strategy.PaymentStrategyFactory;
import iuh.fit.paymentservice.util.IdGenerator;
import iuh.fit.paymentservice.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final JobPackageRepository jobPackageRepository;
    private final PaymentStrategyFactory factory;

    public VNPayResponse createVnPayPayment(InvoiceCreateRequest req, HttpServletRequest request) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new RuntimeException("Payment items are required");
        }

        String txnRef = VnpayUtil.getRandomNumer(8);
        List<PaymentItem> items = buildValidatedItems(req.getItems());
        long expectedTotalAmount = items.stream()
                .mapToLong(item -> Math.round(item.getAmount()))
                .sum();

        if (req.getAmount() == null || req.getAmount() != expectedTotalAmount) {
            throw new RuntimeException("Invalid payment amount");
        }

        Payment payment = Payment.builder()
                .paymentId(IdGenerator.generatorIdPayment())
                .companyId(req.getCompanyId())
                .transactionCode(txnRef)
                .amount(expectedTotalAmount)
                .method(req.getPaymentMethod())
                .employerEmail(req.getEmployerEmail())
                .status(StatusPayment.PENDING)
                .build();

        items.forEach(item -> item.setPayment(payment));
        payment.setItems(items);
        paymentRepository.save(payment);

        PaymentStrategy strategy = factory.getStrategy(req.getPaymentMethod());
        return (VNPayResponse) strategy.pay(payment, request);
    }

    public Payment findByTransactionCode(String txnRef) {
        return paymentRepository.findByTransactionCode(txnRef)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public void save(Payment payment) {
        paymentRepository.save(payment);
    }

    private List<PaymentItem> buildValidatedItems(List<PaymentItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(this::buildValidatedItem)
                .toList();
    }

    private PaymentItem buildValidatedItem(PaymentItemRequest itemRequest) {
        if (itemRequest.getPackageId() == null || itemRequest.getPackageId().isBlank()) {
            throw new RuntimeException("Package id is required");
        }
        if (itemRequest.getQuantity() <= 0) {
            throw new RuntimeException("Package quantity must be greater than 0");
        }
        if (itemRequest.getDurationDays() <= 0) {
            throw new RuntimeException("Package duration must be greater than 0");
        }

        JobPackage jobPackage = jobPackageRepository.findById(itemRequest.getPackageId())
                .orElseThrow(() -> new RuntimeException("Job package not found: " + itemRequest.getPackageId()));
        if (!jobPackage.isActive()) {
            throw new RuntimeException("Job package is inactive: " + itemRequest.getPackageId());
        }

        double unitPrice = calculatePrice(jobPackage, itemRequest.getDurationDays());
        long expectedAmount = Math.round(unitPrice * itemRequest.getQuantity());
        if (itemRequest.getAmount() == null || itemRequest.getAmount() != expectedAmount) {
            throw new RuntimeException("Invalid payment item amount for package " + itemRequest.getPackageId());
        }

        return PaymentItem.builder()
                .id(UUID.randomUUID().toString())
                .packageId(jobPackage.getPackageId())
                .packageName(jobPackage.getName())
                .amount(expectedAmount)
                .durationDays(itemRequest.getDurationDays())
                .quantity(itemRequest.getQuantity())
                .jobPostLimit(jobPackage.getJobPostLimit())
                .packageCategory(jobPackage.getCategory() != null ? jobPackage.getCategory().name() : null)
                .packageType(jobPackage.getType() != null ? jobPackage.getType().name() : null)
                .build();
    }

    private double calculatePrice(JobPackage jobPackage, int durationDays) {
        int baseDurationDays = Math.max(jobPackage.getDurationDays(), 1);
        return Math.round(jobPackage.getPrice() * ((double) durationDays / baseDurationDays));
    }
}
