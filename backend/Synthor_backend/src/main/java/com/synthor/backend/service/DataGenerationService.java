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

            // --- English/Default-context data (uses defaultFaker) ---
            case "username" -> defaultFaker.name().username();
            case "password" -> defaultFaker.internet().password();
            case "email_address" -> generateCustomEmail(); // Use custom email generator
            case "domain_name" -> defaultFaker.internet().domainName();
            case "url" -> defaultFaker.internet().url();
            case "app_name" -> defaultFaker.app().name();
            case "app_version" -> defaultFaker.app().version();
            case "mac_address" -> defaultFaker.internet().macAddress();
            case "ip_v4_address" -> defaultFaker.internet().ipV4Address();
            case "ip_v6_address" -> defaultFaker.internet().ipV6Address();
            case "user_agent" -> defaultFaker.internet().userAgent();
            case "credit_card_type" -> defaultFaker.business().creditCardType();
            case "device_model" -> defaultFaker.device().modelName();
            case "device_brand" -> defaultFaker.device().manufacturer();
            case "device_os" -> defaultFaker.device().platform();
            case "avatar" -> defaultFaker.avatar().image();

            // --- Locale-neutral data (can use either faker) ---
            case "phone" -> koreanFaker.phoneNumber().cellPhone(); // Korean phone format is better
            case "postal_code" -> koreanFaker.address().zipCode(); // Korean postal code format
            case "latitude" -> defaultFaker.address().latitude();
            case "longitude" -> defaultFaker.address().longitude();
            case "product_price" -> defaultFaker.commerce().price();
            case "credit_card_number" -> defaultFaker.finance().creditCard();
            case "bank_routing_number" -> defaultFaker.number().digits(9);
            case "iban" -> defaultFaker.finance().iban();
            case "swift_bic" -> defaultFaker.finance().bic();
            case "currency" -> defaultFaker.currency().name();
            case "datetime" -> defaultFaker.date().past(365, TimeUnit.DAYS).toString();
            case "time" -> DateTimeFormatter.ofPattern("HH:mm:ss").format(defaultFaker.date().birthday().toInstant().atZone(ZoneId.systemDefault()).toLocalTime());
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
