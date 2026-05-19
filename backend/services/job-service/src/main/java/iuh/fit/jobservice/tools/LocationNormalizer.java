package iuh.fit.jobservice.tools;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class LocationNormalizer {

    private static final String[] ALLOWED_PROVINCES = {
        "Tuyen Quang", "Lao Cai", "Thai Nguyen", "Phu Tho", "Bac Ninh", 
        "Hung Yen", "Hai Phong", "Ninh Binh", "Quang Tri", "Da Nang", 
        "Quang Ngai", "Gia Lai", "Khanh Hoa", "Lam Dong", "Dak Lak", 
        "HCM", "Dong Nai", "Tay Ninh", "Can Tho", "Vinh Long", 
        "Dong Thap", "Ca Mau", "An Giang", "Ha Noi", "Hue", 
        "Lai Chau", "Dien Bien", "Son La", "Lang Son", "Quang Ninh", 
        "Thanh Hoa", "Nghe An", "Ha Tinh", "Cao Bang"
    };

    private static final Map<String, String> CUSTOM_MAPPINGS = new HashMap<>();
    private static final Map<String, String> NORMALIZED_PROVINCE_MAP = new HashMap<>();
    private static final Map<String, String> NO_SPACE_PROVINCE_MAP = new HashMap<>();

    static {
        // Build basic normalized maps
        for (String province : ALLOWED_PROVINCES) {
            String norm = normalizeString(province);
            NORMALIZED_PROVINCE_MAP.put(norm, province);
            NO_SPACE_PROVINCE_MAP.put(stripWhitespace(norm), province);
        }

        // Custom Mappings for cities, abbreviations, and common alternatives
        CUSTOM_MAPPINGS.put("hcm", "HCM");
        CUSTOM_MAPPINGS.put("tp hcm", "HCM");
        CUSTOM_MAPPINGS.put("tphcm", "HCM");
        CUSTOM_MAPPINGS.put("ho chi minh", "HCM");
        CUSTOM_MAPPINGS.put("sai gon", "HCM");
        CUSTOM_MAPPINGS.put("tp ho chi minh", "HCM");
        CUSTOM_MAPPINGS.put("tpho chi minh", "HCM");
        CUSTOM_MAPPINGS.put("saigon", "HCM");
        
        CUSTOM_MAPPINGS.put("ha noi", "Ha Noi");
        CUSTOM_MAPPINGS.put("hn", "Ha Noi");
        CUSTOM_MAPPINGS.put("tp ha noi", "Ha Noi");
        CUSTOM_MAPPINGS.put("tphanoi", "Ha Noi");
        CUSTOM_MAPPINGS.put("thu do ha noi", "Ha Noi");
        
        CUSTOM_MAPPINGS.put("da nang", "Da Nang");
        CUSTOM_MAPPINGS.put("dn", "Da Nang");
        CUSTOM_MAPPINGS.put("tp da nang", "Da Nang");
        CUSTOM_MAPPINGS.put("danang", "Da Nang");
        
        CUSTOM_MAPPINGS.put("hue", "Hue");
        CUSTOM_MAPPINGS.put("thua thien hue", "Hue");
        CUSTOM_MAPPINGS.put("tp hue", "Hue");
        CUSTOM_MAPPINGS.put("thuathienhue", "Hue");
        
        CUSTOM_MAPPINGS.put("hai phong", "Hai Phong");
        CUSTOM_MAPPINGS.put("hp", "Hai Phong");
        CUSTOM_MAPPINGS.put("tp hai phong", "Hai Phong");
        CUSTOM_MAPPINGS.put("haiphong", "Hai Phong");

        CUSTOM_MAPPINGS.put("can tho", "Can Tho");
        CUSTOM_MAPPINGS.put("ct", "Can Tho");
        CUSTOM_MAPPINGS.put("tp can tho", "Can Tho");
        CUSTOM_MAPPINGS.put("cantho", "Can Tho");

        CUSTOM_MAPPINGS.put("nha trang", "Khanh Hoa");
        CUSTOM_MAPPINGS.put("nhatrang", "Khanh Hoa");
        CUSTOM_MAPPINGS.put("da lat", "Lam Dong");
        CUSTOM_MAPPINGS.put("dalat", "Lam Dong");
        CUSTOM_MAPPINGS.put("buon ma thuot", "Dak Lak");
        CUSTOM_MAPPINGS.put("bmt", "Dak Lak");
        CUSTOM_MAPPINGS.put("buonmathuot", "Dak Lak");
        CUSTOM_MAPPINGS.put("dak lak", "Dak Lak");
        CUSTOM_MAPPINGS.put("daklak", "Dak Lak");
        CUSTOM_MAPPINGS.put("dac lac", "Dak Lak");
        CUSTOM_MAPPINGS.put("daclac", "Dak Lak");
    }

    public static String removeAccents(String src) {
        if (src == null) {
            return null;
        }
        String nfdNormalizedString = Normalizer.normalize(src, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        String temp = pattern.matcher(nfdNormalizedString).replaceAll("");
        return temp.replace('đ', 'd').replace('Đ', 'D');
    }

    public static String normalizeString(String src) {
        if (src == null) {
            return "";
        }
        String noAccents = removeAccents(src);
        String lower = noAccents.toLowerCase().trim();
        // Remove common prefixes
        lower = lower.replaceAll("^(tinh|thanh pho|tp\\.?|thu do)\\s+", "");
        return lower.trim();
    }

    public static String stripWhitespace(String s) {
        if (s == null) {
            return "";
        }
        return s.replaceAll("\\s+", "");
    }

    /**
     * Normalizes the input location to one of the 34 allowed provinces.
     * If no match is found, returns the original input.
     */
    public static String normalizeLocation(String location) {
        if (location == null || location.isBlank()) {
            return null;
        }
        
        String cleaned = normalizeString(location);
        
        // 1. Check direct custom mappings
        if (CUSTOM_MAPPINGS.containsKey(cleaned)) {
            return CUSTOM_MAPPINGS.get(cleaned);
        }
        
        // 2. Check direct normalized name
        if (NORMALIZED_PROVINCE_MAP.containsKey(cleaned)) {
            return NORMALIZED_PROVINCE_MAP.get(cleaned);
        }
        
        // 3. Check no-space custom mapping check
        String noSpaceCleaned = stripWhitespace(cleaned);
        if (CUSTOM_MAPPINGS.containsKey(noSpaceCleaned)) {
            return CUSTOM_MAPPINGS.get(noSpaceCleaned);
        }
        
        // 4. Check no-space standard province check
        if (NO_SPACE_PROVINCE_MAP.containsKey(noSpaceCleaned)) {
            return NO_SPACE_PROVINCE_MAP.get(noSpaceCleaned);
        }
        
        // 5. Containment match (e.g. user typed a longer string or sub-string)
        if (cleaned.length() >= 3) {
            for (Map.Entry<String, String> entry : NORMALIZED_PROVINCE_MAP.entrySet()) {
                if (cleaned.contains(entry.getKey()) || entry.getKey().contains(cleaned)) {
                    return entry.getValue();
                }
            }
            
            // Substring search on space-stripped values
            for (Map.Entry<String, String> entry : NO_SPACE_PROVINCE_MAP.entrySet()) {
                if (noSpaceCleaned.contains(entry.getKey()) || entry.getKey().contains(noSpaceCleaned)) {
                    return entry.getValue();
                }
            }
        }
        
        // Fallback: return original if nothing matched
        return location;
    }
}
