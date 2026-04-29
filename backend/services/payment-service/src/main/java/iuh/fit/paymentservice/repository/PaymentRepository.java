package iuh.fit.paymentservice.repository;

import iuh.fit.paymentservice.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTransactionCode(String txnRef);
}
