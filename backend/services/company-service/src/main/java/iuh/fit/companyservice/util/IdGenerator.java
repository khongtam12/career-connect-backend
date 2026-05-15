package iuh.fit.companyservice.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IdGenerator {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmss");





    public static String generatorIdCompanny() {
        return "CMP" + "-" + UUID.randomUUID();
    }
    public static String generatorIdCompannyVerified() {
        return "VR" + "-" + UUID.randomUUID();
    }
    public static String generatorIdCompannySubscription() {
        return "CMPSB" + "-" + UUID.randomUUID();
    }

    public static String generatorIdCompanyMarketingEntitlement() {
        return "CMPME" + "-" + UUID.randomUUID();
    }

    public static String generatorIdCompanyMarketingAssignment() {
        return "CMPMA" + "-" + UUID.randomUUID();
    }


}
