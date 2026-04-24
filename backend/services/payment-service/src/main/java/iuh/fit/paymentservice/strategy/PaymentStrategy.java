package iuh.fit.paymentservice.strategy;

import iuh.fit.paymentservice.model.Payment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Repository;


public interface PaymentStrategy {
    Object pay(Payment payment, HttpServletRequest request);

}
