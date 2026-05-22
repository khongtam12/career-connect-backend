package iuh.fit.jobservice.tools;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private static final Map<String, String> DISPLAY_LABELS = new HashMap<>();

    static {
        // Build basic normalized maps
        for (String province : ALLOWED_PROVINCES) {
            String norm = normalizeString(province);
            NORMALIZED_PROVINCE_MAP.put(norm, province);
            NO_SPACE_PROVINCE_MAP.put(stripWhitespace(norm), province);
        }

        DISPLAY_LABELS.put("Tuyen Quang", "Tỉnh Tuyên Quang");
        DISPLAY_LABELS.put("Lao Cai", "Tỉnh Lào Cai");
        DISPLAY_LABELS.put("Thai Nguyen", "Tỉnh Thái Nguyên");
        DISPLAY_LABELS.put("Phu Tho", "Tỉnh Phú Thọ");
        DISPLAY_LABELS.put("Bac Ninh", "Tỉnh Bắc Ninh");
        DISPLAY_LABELS.put("Hung Yen", "Tỉnh Hưng Yên");
        DISPLAY_LABELS.put("Hai Phong", "Thành phố Hải Phòng");
        DISPLAY_LABELS.put("Ninh Binh", "Tỉnh Ninh Bình");
        DISPLAY_LABELS.put("Quang Tri", "Tỉnh Quảng Trị");
        DISPLAY_LABELS.put("Da Nang", "Thành phố Đà Nẵng");
        DISPLAY_LABELS.put("Quang Ngai", "Tỉnh Quảng Ngãi");
        DISPLAY_LABELS.put("Gia Lai", "Tỉnh Gia Lai");
        DISPLAY_LABELS.put("Khanh Hoa", "Tỉnh Khánh Hòa");
        DISPLAY_LABELS.put("Lam Dong", "Tỉnh Lâm Đồng");
        DISPLAY_LABELS.put("Dak Lak", "Tỉnh Đắk Lắk");
        DISPLAY_LABELS.put("HCM", "Thành phố Hồ Chí Minh");
        DISPLAY_LABELS.put("Dong Nai", "Tỉnh Đồng Nai");
        DISPLAY_LABELS.put("Tay Ninh", "Tỉnh Tây Ninh");
        DISPLAY_LABELS.put("Can Tho", "Thành phố Cần Thơ");
        DISPLAY_LABELS.put("Vinh Long", "Tỉnh Vĩnh Long");
        DISPLAY_LABELS.put("Dong Thap", "Tỉnh Đồng Tháp");
        DISPLAY_LABELS.put("Ca Mau", "Tỉnh Cà Mau");
        DISPLAY_LABELS.put("An Giang", "Tỉnh An Giang");
        DISPLAY_LABELS.put("Ha Noi", "Thành phố Hà Nội");
        DISPLAY_LABELS.put("Hue", "Thành phố Huế");
        DISPLAY_LABELS.put("Lai Chau", "Tỉnh Lai Châu");
        DISPLAY_LABELS.put("Dien Bien", "Tỉnh Điện Biên");
        DISPLAY_LABELS.put("Son La", "Tỉnh Sơn La");
        DISPLAY_LABELS.put("Lang Son", "Tỉnh Lạng Sơn");
        DISPLAY_LABELS.put("Quang Ninh", "Tỉnh Quảng Ninh");
        DISPLAY_LABELS.put("Thanh Hoa", "Tỉnh Thanh Hóa");
        DISPLAY_LABELS.put("Nghe An", "Tỉnh Nghệ An");
        DISPLAY_LABELS.put("Ha Tinh", "Tỉnh Hà Tĩnh");
        DISPLAY_LABELS.put("Cao Bang", "Tỉnh Cao Bằng");

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

    public static boolean isRecognizedProvince(String location) {
        if (location == null || location.isBlank()) {
            return false;
        }

        String cleaned = normalizeString(location);
        String noSpaceCleaned = stripWhitespace(cleaned);
        return CUSTOM_MAPPINGS.containsKey(cleaned)
                || NORMALIZED_PROVINCE_MAP.containsKey(cleaned)
                || CUSTOM_MAPPINGS.containsKey(noSpaceCleaned)
                || NO_SPACE_PROVINCE_MAP.containsKey(noSpaceCleaned);
    }

    public static String toDisplayLabel(String location) {
        String normalized = normalizeLocation(location);
        if (normalized == null || normalized.isBlank()) {
            return null;
        }

        String display = DISPLAY_LABELS.get(normalized);
        return display != null ? display : normalized;
    }

    public static List<String> getSearchTerms(String location) {
        if (location == null || location.isBlank()) {
            return List.of();
        }

        Set<String> terms = new LinkedHashSet<>();
        String normalized = normalizeLocation(location);
        String display = toDisplayLabel(location);

        addTermVariants(terms, location);
        addTermVariants(terms, normalized);
        addTermVariants(terms, display);

        return new ArrayList<>(terms);
    }

    private static void addTermVariants(Set<String> terms, String value) {
        if (value == null || value.isBlank()) {
            return;
        }

        String cleaned = normalizeString(value);
        if (!cleaned.isBlank()) {
            terms.add(cleaned);
            String noSpace = stripWhitespace(cleaned);
            if (!noSpace.isBlank()) {
                terms.add(noSpace);
            }
        }

        String stripped = value.trim().toLowerCase();
        if (!stripped.isBlank()) {
            terms.add(stripped);
        }
    }
}
