package iuh.fit.paymentservice.strategy;

import iuh.fit.paymentservice.config.VnpayConfig;
import iuh.fit.paymentservice.dto.response.VNPayResponse;
import iuh.fit.paymentservice.model.Payment;
import iuh.fit.paymentservice.model.StatusPayment;
import iuh.fit.paymentservice.repository.PaymentRepository;
import iuh.fit.paymentservice.util.VnpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
@Slf4j
@Service
@RequiredArgsConstructor
public class VNPayStrategy implements PaymentStrategy {

    private final VnpayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;

    @Override
    public Object pay(Payment payment, HttpServletRequest request) {
        long amount=(long) (payment.getAmount()*100);

        Map<String,String> vnpParams = vnPayConfig.getVnpayConfig();
        vnpParams.put("vnp_IpAddr", VnpayUtil.getIpAddress(request));
        vnpParams.put("vnp_Amount",String.valueOf(amount));
        vnpParams.put("vnp_TxnRef", payment.getTransactionCode());
        vnpParams.put("vnp_OrderInfo", "Thanh toan don hang " + payment.getPaymentId());



        // tạo query url
        String queryUrl=VnpayUtil.getPaymentURL(vnpParams,true);
        String hashData=VnpayUtil.getPaymentURL(vnpParams,false);
        String vnpSecureHash=VnpayUtil.hmacSHA512(vnPayConfig.getSecretKey(),hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnpayUrl() + "?" + queryUrl;



        return VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }
}