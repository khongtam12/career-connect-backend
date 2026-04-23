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


}