package com.synthor.backend.service;

import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.dto.FieldRequest;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class DataGenerationService {

    // For locale-specific data (e.g., names, addresses in Korean)
    private final Faker koreanFaker = new Faker(new Locale("ko"));
    // For locale-neutral or English-based data (e.g., emails, domains)
    private final Faker defaultFaker = new Faker(Locale.ENGLISH);

    // Predefined Korean data for types with no direct library support
    private static final String[] KOREAN_JOB_TITLES = {"팀장", "부장", "과장", "대리", "사원", "개발자", "디자이너", "기획자", "마케터"};
    private static final String[] KOREAN_DEPARTMENTS_CORPORATE = {"인사팀", "개발팀", "디자인팀", "마케팅팀", "영업팀", "재무팀", "기획팀"};
    private static final String[] KOREAN_DEPARTMENTS_RETAIL = {"의류", "가전", "식품", "잡화", "뷰티", "스포츠", "도서"};
    private static final String[] KOREAN_PRODUCT_NAMES = {"신비한 물약", "튼튼한 망치", "빛나는 검", "지혜의 책", "행운의 목걸이", "투명 드래곤"};
    private static final String[] KOREAN_PRODUCT_CATEGORIES = {"전자기기", "패션의류", "뷰티", "도서/음반", "스포츠/레저", "생활용품"};
    private static final String[] KOREAN_CATCH_PHRASES = {"혁신을 선도합니다", "당신의 삶을 바꾸는 기술", "최고의 품질, 최상의 선택", "세상을 연결하는 솔루션", "미래를 향한 끝없는 도전"};
    private static final String[] GENDERS = {"Female", "Male"};
    private static final String[] KOREAN_GENDERS = {"여자", "남자"};
    private static final String[] GENDERS_WITH_NON_BINARY = {"Female", "Male", "Non-binary"};
    private static final String[] KOREAN_GENDERS_WITH_NON_BINARY = {"여자", "남자", "그 외"};
    private static final String[] ADDRESS_LINE_2_EXAMPLES = {
            "Apt 1306", "Suite 34", "Room 327", "17th Floor", "PO Box 12055", "Apt 1233",
            "Suite 44", "PO Box 88462", "Apt 340", "PO Box 41504", "Room 1262", "Room 312",
            "Room 1699", "Room 1980", "Suite 19", "PO Box 71509", "Apt 275", "Room 1554",
            "Apt 1016", "PO Box 36274", "PO Box 12632", "Suite 13", "PO Box 41496", "Suite 90",
            "Apt 1570", "Suite 63", "14th Floor", "Suite 26", "5th Floor", "Apt 112", "Apt 539",
            "PO Box 78946", "13th Floor", "4th Floor", "Room 1484", "Apt 1518", "Apt 178",
            "Apt 1199", "Room 27", "PO Box 7916", "Room 950", "8th Floor", "Apt 1420",
            "PO Box 50509", "Suite 92", "PO Box 97195", "Room 417", "PO Box 74379",
            "PO Box 93574", "Apt 392", "Room 403", "Apt 919",
            "Room 1109", "Room 1188", "Room 1215", "Room 97", "Room 889", "PO Box 20113",
            "Room 374", "11th Floor", "9th Floor", "Suite 43", "PO Box 51543", "Suite 57",
            "Room 770", "Apt 431", "PO Box 41967", "Room 189", "Suite 15", "PO Box 1326",
            "Room 991", "Apt 1616", "Room 982", "Apt 1019", "Room 1842", "Suite 52",
            "18th Floor", "Suite 11", "PO Box 68970",
            "Suite 85", "Suite 67", "Room 285", "Room 1340", "15th Floor", "Room 727",
            "PO Box 57575", "16th Floor", "Room 1373", "Room 1541",
            "PO Box 7326", "Apt 767", "Suite 70"
    };
    private static final String[] KOREAN_ADDRESS_LINE_2_EXAMPLES = {
            "101동 1204호", "가나아파트 302동 501호", "행복빌라 203호", "사무실 A동 301호",
            "지하 1층", "옥탑방", "별관 2층", "본관 10층", "102동 2001호", "다성빌라 101호",
            "현대타워 1503호", "7층", "201호", "사랑채", "별관 사무동 301호", "12층 스카이라운지",
            "A블록 101동 101호", "B상가 205호", "지하상가 13호", "연구동 404호", "강의동 502호",
            "학생회관 301실", "2공학관 512호", "창조관 707호", "301호 (3층)", "1205호 (12층)"
    };

    public List<Map<String, Object>> generateData(DataGenerationRequest request) {
        List<Map<String, Object>> generatedData = new ArrayList<>();
        int count = request.getCount();
        List<FieldRequest> fields = request.getFields();

        for (int i = 0; i < count; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (FieldRequest field : fields) {
                String fieldName = field.getName();
                Object fieldValue;

                if ("fixed".equalsIgnoreCase(field.getType())) {
                    fieldValue = field.getValue();
                } else {
                    fieldValue = generateValueByType(field.getType());
                }
                row.put(fieldName, fieldValue);
            }
            generatedData.add(row);
        }

        return generatedData;
    }

    private Object generateValueByType(String type) {
        return switch (type) {
            // --- [KOREAN] Person & Personal Info ---
            case "korean_full_name" -> koreanFaker.name().lastName() + koreanFaker.name().firstName();
            case "korean_first_name" -> koreanFaker.name().firstName();
            case "korean_last_name" -> koreanFaker.name().lastName();
            case "korean_gender" -> KOREAN_GENDERS[defaultFaker.random().nextInt(KOREAN_GENDERS.length)];
            case "korean_gender_with_non_binary" -> KOREAN_GENDERS_WITH_NON_BINARY[defaultFaker.random().nextInt(KOREAN_GENDERS_WITH_NON_BINARY.length)];
            case "korean_phone" -> koreanFaker.phoneNumber().cellPhone();
            case "korean_mobile_phone" -> "010-" + defaultFaker.number().numberBetween(1000, 10000) + "-" + defaultFaker.number().numberBetween(1000, 10000);

            // --- [ENGLISH] Person & Personal Info ---
            case "full_name" -> defaultFaker.name().fullName();
            case "first_name" -> defaultFaker.name().firstName();
            case "last_name" -> defaultFaker.name().lastName();
            case "gender" -> GENDERS[defaultFaker.random().nextInt(GENDERS.length)];
            case "gender_with_non_binary" -> GENDERS_WITH_NON_BINARY[defaultFaker.random().nextInt(GENDERS_WITH_NON_BINARY.length)];
            case "phone" -> defaultFaker.phoneNumber().cellPhone();

            // --- [KOREAN] Address ---
            case "korean_address" -> koreanFaker.address().fullAddress();
            case "korean_street_address" -> koreanFaker.address().streetAddress();
            case "korean_city" -> koreanFaker.address().city();
            case "korean_state" -> koreanFaker.address().state();
            case "korean_postal_code" -> koreanFaker.address().zipCode();
            case "korean_address_line_2" -> KOREAN_ADDRESS_LINE_2_EXAMPLES[defaultFaker.random().nextInt(KOREAN_ADDRESS_LINE_2_EXAMPLES.length)];

            // --- [ENGLISH] Address ---
            case "address" -> defaultFaker.address().fullAddress();
            case "street_address" -> defaultFaker.address().streetAddress();
            case "city" -> defaultFaker.address().city();
            case "state" -> defaultFaker.address().state();
            case "country" -> defaultFaker.address().country();
            case "postal_code" -> defaultFaker.address().zipCode();
            case "address_line_2" -> ADDRESS_LINE_2_EXAMPLES[defaultFaker.random().nextInt(ADDRESS_LINE_2_EXAMPLES.length)];

            // --- [KOREAN] Company & Commerce ---
            case "korean_company_name" -> koreanFaker.company().name();
            case "korean_job_title" -> KOREAN_JOB_TITLES[defaultFaker.random().nextInt(KOREAN_JOB_TITLES.length)];
            case "korean_department_corporate" -> KOREAN_DEPARTMENTS_CORPORATE[defaultFaker.random().nextInt(KOREAN_DEPARTMENTS_CORPORATE.length)];
            case "korean_department_retail" -> KOREAN_DEPARTMENTS_RETAIL[defaultFaker.random().nextInt(KOREAN_DEPARTMENTS_RETAIL.length)];
            case "korean_product_name" -> KOREAN_PRODUCT_NAMES[defaultFaker.random().nextInt(KOREAN_PRODUCT_NAMES.length)];
            case "korean_product_category" -> KOREAN_PRODUCT_CATEGORIES[defaultFaker.random().nextInt(KOREAN_PRODUCT_CATEGORIES.length)];
            case "korean_catch_phrase" -> KOREAN_CATCH_PHRASES[defaultFaker.random().nextInt(KOREAN_CATCH_PHRASES.length)];
            case "korean_product_description" -> String.join(" ", koreanFaker.lorem().sentences(2));

            // --- [ENGLISH] Company & Commerce ---
            case "company_name" -> defaultFaker.company().name();
            case "job_title" -> defaultFaker.job().title();
            case "department_corporate" -> defaultFaker.company().profession();
            case "department_retail" -> defaultFaker.commerce().department();
            case "product_name" -> defaultFaker.commerce().productName();
            case "product_category" -> defaultFaker.commerce().department();
            case "catch_phrase" -> defaultFaker.company().catchPhrase();
            case "product_description" -> String.join(" ", defaultFaker.lorem().sentences(2));

            // --- [KOREAN] Misc ---
            case "korean_color" -> koreanFaker.color().name();

            // --- [ENGLISH] Misc ---
            case "language" -> defaultFaker.nation().language();
            case "color" -> defaultFaker.color().name();

            // =======================================================================================
            // --- LOCALE-NEUTRAL / ENGLISH-DEFAULT TYPES (No Korean version needed) ---
            // =======================================================================================

            // --- Internet & Tech ---
            case "username" -> defaultFaker.name().username();
            case "password" -> defaultFaker.internet().password();
            case "email_address" -> generateCustomEmail();
            case "domain_name" -> defaultFaker.internet().domainName();
            case "url" -> defaultFaker.internet().url();
            case "mac_address" -> defaultFaker.internet().macAddress();
            case "ip_v4_address" -> defaultFaker.internet().ipV4Address();
            case "ip_v6_address" -> defaultFaker.internet().ipV6Address();
            case "user_agent" -> defaultFaker.internet().userAgent();
            case "avatar" -> defaultFaker.avatar().image();

            // --- App & Device ---
            case "app_name" -> defaultFaker.app().name();
            case "app_version" -> defaultFaker.app().version();
            case "device_model" -> defaultFaker.device().modelName();
            case "device_brand" -> defaultFaker.device().manufacturer();
            case "device_os" -> defaultFaker.device().platform();

            // --- Finance ---
            case "credit_card_number" -> defaultFaker.finance().creditCard();
            case "credit_card_type" -> defaultFaker.business().creditCardType();
            case "product_price" -> defaultFaker.commerce().price();
            case "currency" -> defaultFaker.currency().name();
            case "iban" -> defaultFaker.finance().iban();
            case "swift_bic" -> defaultFaker.finance().bic();

            // --- Misc ---
            case "paragraphs" -> String.join("\n\n", defaultFaker.lorem().paragraphs(3));
            case "datetime" -> defaultFaker.date().past(365, TimeUnit.DAYS).toString();
            case "time" -> DateTimeFormatter.ofPattern("HH:mm:ss").format(defaultFaker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
            case "latitude" -> defaultFaker.address().latitude();
            case "longitude" -> defaultFaker.address().longitude();
            case "number_between_1_100" -> defaultFaker.number().numberBetween(1, 101);

            default -> "Unsupported Type: " + type;
        };
    }

    private String generateCustomEmail() {
        String firstName = defaultFaker.name().firstName().toLowerCase();
        String lastName = defaultFaker.name().lastName().toLowerCase();
        String domain = defaultFaker.internet().domainName();

        int format = defaultFaker.random().nextInt(4);

        return switch (format) {
            case 0 -> firstName + "." + lastName + "@" + domain;
            case 1 -> lastName + "_" + firstName + "@" + domain;
            case 2 -> firstName.charAt(0) + lastName + "@" + domain;
            case 3 -> firstName + defaultFaker.number().digits(3) + "@" + domain;
            default -> firstName + lastName + "@" + domain;
        };
    }
}
