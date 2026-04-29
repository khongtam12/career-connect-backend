package iuh.fit.paymentservice.util;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IdGenerator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");





    public static String generatorIdPayment() {
        return "PM" + UUID.randomUUID();
    }


}