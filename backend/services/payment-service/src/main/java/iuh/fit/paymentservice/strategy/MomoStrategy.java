package iuh.fit.paymentservice.strategy;

import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.StatusPayment;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class MomoStrategy implements PaymentStrategy {




    @Override
    public Object pay(Payment payment, HttpServletRequest request) {
        return null;
    }
}