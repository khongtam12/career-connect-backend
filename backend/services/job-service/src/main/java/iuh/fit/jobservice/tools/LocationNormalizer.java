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

    private static final String[] PROVINCES = {
        "Thành phố Hà Nội", "Tỉnh Cao Bằng", "Tỉnh Tuyên Quang", "Tỉnh Điện Biên", 
        "Tỉnh Lai Châu", "Tỉnh Sơn La", "Tỉnh Lào Cai", "Tỉnh Thái Nguyên", 
        "Tỉnh Lạng Sơn", "Tỉnh Quảng Ninh", "Tỉnh Bắc Ninh", "Tỉnh Phú Thọ", 
        "Thành phố Hải Phòng", "Tỉnh Hưng Yên", "Tỉnh Ninh Bình", "Tỉnh Thanh Hóa", 
        "Tỉnh Nghệ An", "Tỉnh Hà Tĩnh", "Tỉnh Quảng Trị", "Thành phố Huế", 
        "Thành phố Đà Nẵng", "Tỉnh Quảng Ngãi", "Tỉnh Gia Lai", "Tỉnh Khánh Hòa", 
        "Tỉnh Đắk Lắk", "Tỉnh Lâm Đồng", "Tỉnh Đồng Nai", "Thành phố Hồ Chí Minh", 
        "Tỉnh Tây Ninh", "Tỉnh Đồng Tháp", "Tỉnh Vĩnh Long", "Tỉnh An Giang", 
        "Thành phố Cần Thơ", "Tỉnh Cà Mau"
    };

    private static final Map<String, String> CUSTOM_MAPPINGS = new HashMap<>();
    private static final Map<String, String> NORMALIZED_PROVINCE_MAP = new HashMap<>();
    private static final Map<String, String> NO_SPACE_PROVINCE_MAP = new HashMap<>();
    private static final Map<String, String> DISPLAY_LABELS = new HashMap<>();

    static {
        for (String province : PROVINCES) {
            String norm = normalizeString(province);
            NORMALIZED_PROVINCE_MAP.put(norm, province);
            NO_SPACE_PROVINCE_MAP.put(stripWhitespace(norm), province);
            DISPLAY_LABELS.put(province, province);
        }

        // Custom Mappings for cities, abbreviations, and common alternatives
        CUSTOM_MAPPINGS.put("hcm", "Thành phố Hồ Chí Minh");
        CUSTOM_MAPPINGS.put("tp hcm", "Thành phố Hồ Chí Minh");
        CUSTOM_MAPPINGS.put("tphcm", "Thành phố Hồ Chí Minh");
        CUSTOM_MAPPINGS.put("ho chi minh", "Thành phố Hồ Chí Minh");
        CUSTOM_MAPPINGS.put("sai gon", "Thành phố Hồ Chí Minh");
        CUSTOM_MAPPINGS.put("tp ho chi minh", "Thành phố Hồ Chí Minh");
        CUSTOM_MAPPINGS.put("tpho chi minh", "Thành phố Hồ Chí Minh");
        CUSTOM_MAPPINGS.put("saigon", "Thành phố Hồ Chí Minh");
        
        CUSTOM_MAPPINGS.put("ha noi", "Thành phố Hà Nội");
        CUSTOM_MAPPINGS.put("hn", "Thành phố Hà Nội");
        CUSTOM_MAPPINGS.put("tp ha noi", "Thành phố Hà Nội");
        CUSTOM_MAPPINGS.put("tphanoi", "Thành phố Hà Nội");
        CUSTOM_MAPPINGS.put("thu do ha noi", "Thành phố Hà Nội");
        
        CUSTOM_MAPPINGS.put("da nang", "Thành phố Đà Nẵng");
        CUSTOM_MAPPINGS.put("dn", "Thành phố Đà Nẵng");
        CUSTOM_MAPPINGS.put("tp da nang", "Thành phố Đà Nẵng");
        CUSTOM_MAPPINGS.put("danang", "Thành phố Đà Nẵng");
        
        CUSTOM_MAPPINGS.put("hue", "Thành phố Huế");
        CUSTOM_MAPPINGS.put("thua thien hue", "Thành phố Huế");
        CUSTOM_MAPPINGS.put("tp hue", "Thành phố Huế");
        CUSTOM_MAPPINGS.put("thuathienhue", "Thành phố Huế");
        
        CUSTOM_MAPPINGS.put("hai phong", "Thành phố Hải Phòng");
        CUSTOM_MAPPINGS.put("hp", "Thành phố Hải Phòng");
        CUSTOM_MAPPINGS.put("tp hai phong", "Thành phố Hải Phòng");
        CUSTOM_MAPPINGS.put("haiphong", "Thành phố Hải Phòng");

        CUSTOM_MAPPINGS.put("can tho", "Thành phố Cần Thơ");
        CUSTOM_MAPPINGS.put("ct", "Thành phố Cần Thơ");
        CUSTOM_MAPPINGS.put("tp can tho", "Thành phố Cần Thơ");
        CUSTOM_MAPPINGS.put("cantho", "Thành phố Cần Thơ");

        CUSTOM_MAPPINGS.put("nha trang", "Tỉnh Khánh Hòa");
        CUSTOM_MAPPINGS.put("nhatrang", "Tỉnh Khánh Hòa");
        CUSTOM_MAPPINGS.put("da lat", "Tỉnh Lâm Đồng");
        CUSTOM_MAPPINGS.put("dalat", "Tỉnh Lâm Đồng");
        CUSTOM_MAPPINGS.put("buon ma thuot", "Tỉnh Đắk Lắk");
        CUSTOM_MAPPINGS.put("bmt", "Tỉnh Đắk Lắk");
        CUSTOM_MAPPINGS.put("buonmathuot", "Tỉnh Đắk Lắk");
        CUSTOM_MAPPINGS.put("dak lak", "Tỉnh Đắk Lắk");
        CUSTOM_MAPPINGS.put("daklak", "Tỉnh Đắk Lắk");
        CUSTOM_MAPPINGS.put("dac lac", "Tỉnh Đắk Lắk");
        CUSTOM_MAPPINGS.put("daclac", "Tỉnh Đắk Lắk");
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
