package iuh.fit.paymentservice.config;

import iuh.fit.paymentservice.util.VnpayUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.text.SimpleDateFormat;
import java.util.*;

@Configuration
@Getter
@PropertySource(value = "file:.env", ignoreResourceNotFound = true)
public class VnpayConfig {
    @Value("${PAY_URL}")
    private String vnpayUrl;
    @Value("${VNP_RETURNURL}")
    private String vnpReturnUrl;
    @Value("${TMN_CODE}")
    private String vnpTmCode;
    @Value("${SECRET_KEY}")
    private String secretKey;
    @Value("${VERSION}")
    private String vnp_Version;
    @Value("${COMMAND}")
    private String vnp_Command;
    @Value("${ORDER_TYPE}")
    private String orderType;

    public Map<String, String> getVnpayConfig() {
        Map<String, String> vnpParamsMap = new TreeMap<>();
        vnpParamsMap.put("vnp_Version", vnp_Version);
        vnpParamsMap.put("vnp_Command", vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", vnpTmCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_OrderType", orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", vnpReturnUrl);

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnpParamsMap.put("vnp_CreateDate", formatter.format(calendar.getTime()));
        calendar.add(Calendar.MINUTE, 10);
        vnpParamsMap.put("vnp_ExpireDate", formatter.format(calendar.getTime()));
        return vnpParamsMap;

    }

}
