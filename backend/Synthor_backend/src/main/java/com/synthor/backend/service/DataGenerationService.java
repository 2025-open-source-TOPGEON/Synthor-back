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
<<<<<<< Updated upstream
            // --- Korean-context data (uses koreanFaker) ---
            case "full_name" -> koreanFaker.name().fullName();
            case "first_name" -> koreanFaker.name().firstName();
            case "last_name" -> koreanFaker.name().lastName();
            case "address" -> koreanFaker.address().fullAddress();
            case "street_address" -> koreanFaker.address().streetAddress();
            case "address_line_2" -> koreanFaker.address().secondaryAddress();
            case "city" -> koreanFaker.address().city();
            case "state" -> koreanFaker.address().state();
            case "country" -> koreanFaker.address().country();
            case "company_name" -> koreanFaker.company().name();
            case "job_title" -> koreanFaker.job().title();
            case "department_corporate" -> koreanFaker.company().profession();
            case "department_retail" -> koreanFaker.commerce().department();
            case "product_name" -> koreanFaker.commerce().productName();
            case "product_category" -> koreanFaker.commerce().department();
            case "product_description" -> String.join(" ", koreanFaker.lorem().sentences(2));
            case "bank_name" -> koreanFaker.company().name();
            case "language" -> koreanFaker.nation().language();
            case "color" -> koreanFaker.color().name();
            case "catch_phrase" -> koreanFaker.company().catchPhrase();
            case "paragraphs" -> String.join("\n\n", koreanFaker.lorem().paragraphs(3));
=======
            // --- [KOREAN] Person & Personal Info ---
            case "korean_full_name" -> koreanFaker.name().lastName() + koreanFaker.name().firstName();
            case "korean_first_name" -> koreanFaker.name().firstName();
            case "korean_last_name" -> koreanFaker.name().lastName();
            case "korean_phone" -> koreanFaker.phoneNumber().cellPhone();

            // --- [ENGLISH] Person & Personal Info ---
            case "full_name" -> defaultFaker.name().fullName();
            case "first_name" -> defaultFaker.name().firstName();
            case "last_name" -> defaultFaker.name().lastName();
            case "phone" -> defaultFaker.phoneNumber().cellPhone();
>>>>>>> Stashed changes

            // --- [KOREAN] Address ---
            case "korean_address" -> koreanFaker.address().fullAddress();
            case "korean_street_address" -> koreanFaker.address().streetAddress();
            case "korean_city" -> koreanFaker.address().city();
            case "korean_state" -> koreanFaker.address().state();
            case "korean_country" -> koreanFaker.address().country();
            case "korean_postal_code" -> koreanFaker.address().zipCode();

            // --- [ENGLISH] Address ---
            case "address" -> defaultFaker.address().fullAddress();
            case "street_address" -> defaultFaker.address().streetAddress();
            case "city" -> defaultFaker.address().city();
            case "state" -> defaultFaker.address().state();
            case "country" -> defaultFaker.address().country();
            case "postal_code" -> defaultFaker.address().zipCode();

            // --- [KOREAN] Company & Commerce ---
            case "korean_company_name" -> koreanFaker.company().name();
            case "korean_job_title" -> koreanFaker.job().title();
            case "korean_department_corporate" -> koreanFaker.company().profession();
            case "korean_department_retail" -> koreanFaker.commerce().department();
            case "korean_product_name" -> koreanFaker.commerce().productName();
            case "korean_product_category" -> koreanFaker.commerce().department();
            case "korean_catch_phrase" -> koreanFaker.company().catchPhrase();
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

            // --- [KOREAN] Finance ---
            case "korean_bank_name" -> koreanFaker.company().name();

            // --- [ENGLISH] Finance ---
            case "bank_name" -> defaultFaker.finance().bank();

            // --- [KOREAN] Misc ---
            case "korean_language" -> koreanFaker.nation().language();
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
