package iuh.fit.paymentservice.repository;

import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.StatusPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTransactionCode(String txnRef);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.status = :status AND p.paidAt >= :from AND p.paidAt <= :to")
    double sumAmountByStatusAndPaidAtBetween(@Param("status") StatusPayment status, @Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    List<Payment> findByStatusAndPaidAtBetween(StatusPayment status, LocalDateTime from, LocalDateTime to);

    Page<Payment> findByStatusOrderByPaidAtDesc(StatusPayment status, Pageable pageable);
}
