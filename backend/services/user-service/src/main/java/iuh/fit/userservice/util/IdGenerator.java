package iuh.fit.userservice.util;

import java.util.UUID;

public class IdGenerator {
    public static String generatorIdRecruiter() {
        return "REC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
