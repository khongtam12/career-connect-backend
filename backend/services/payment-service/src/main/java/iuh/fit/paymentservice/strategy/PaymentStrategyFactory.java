package iuh.fit.paymentservice.strategy;

import iuh.fit.paymentservice.model.Method;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStrategyFactory {

    private final VNPayStrategy vnPayStrategy;
    private final MomoStrategy momoStrategy;

    public PaymentStrategy getStrategy(Method method) {
        return switch (method) {
            case VNPAY -> vnPayStrategy;
            default -> throw new RuntimeException("Unsupported method");
        };
    }
}