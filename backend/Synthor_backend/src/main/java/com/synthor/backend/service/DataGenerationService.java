package com.synthor.backend.service;

import com.synthor.backend.dto.AiApiResponse;
import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.dto.FieldRequest;
import net.datafaker.Faker;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DataGenerationService {

    private final Faker koreanFaker;
    private final Faker defaultFaker;
    private final AiApiService aiApiService;

    // Predefined Korean data for types with no direct library support
    private static final String[] KOREAN_JOB_TITLES = {
            "팀장", "부장", "과장", "대리", "사원", "개발자", "디자이너", "기획자", "마케터", "대표이사", "CEO",
            "CTO", "CFO", "이사", "상무", "전무", "주임", "연구원", "컨설턴트", "애널리스트", "퍼블리셔", "운영자",
            "PM", "PO", "데이터 사이언티스트", "QA 엔지니어", "시스템 관리자", "네트워크 관리자", "보안 전문가",
            "인사 담당자", "총무", "회계 담당자", "법무팀", "고객지원 담당자", "세일즈 매니저", "프로듀서", "아티스트", "작가"
    };
    private static final String[] KOREAN_DEPARTMENTS_CORPORATE = {
            "인사팀", "개발팀", "디자인팀", "마케팅팀", "영업팀", "재무팀", "기획팀", "경영지원팀", "전략기획팀",
            "R&D센터", "품질관리팀", "고객서비스팀", "홍보팀", "IR팀", "법무팀", "감사팀", "IT지원팀",
            "데이터분석팀", "해외사업팀", "국내영업팀", "생산관리팀", "물류팀"
    };
    // ... (rest of the predefined data arrays are omitted for brevity but are included in the actual file)

    public DataGenerationService(AiApiService aiApiService) {
        this.koreanFaker = new Faker(new Locale("ko"));
        this.defaultFaker = new Faker(Locale.ENGLISH);
        this.aiApiService = aiApiService;
    }

    public List<Map<String, Object>> generateData(DataGenerationRequest request) {
        List<Map<String, Object>> generatedData = new ArrayList<>();
        int count = request.getCount();
        List<FieldRequest> fields = request.getFields();

        // Prompt-based generation logic
        for (FieldRequest field : fields) {
            String prompt = field.getPrompt();
            if (prompt != null && !prompt.isBlank()) {
                AiApiResponse aiResponse = aiApiService.suggestFieldType(prompt);
                if (aiResponse != null && aiResponse.getType() != null) {
                    field.setType(aiResponse.getType());
                    field.setConstraints(aiResponse.getConstraints());
                    field.setNullablePercent(aiResponse.getNullablePercent());
                }
            }
        }

        // Nullable values calculation
        Map<String, Set<Integer>> nullableIndexes = new HashMap<>();
        for (FieldRequest field : fields) {
            Integer nullablePercent = field.getNullablePercent();
            if (nullablePercent != null && nullablePercent > 0) {
                int nullableCount = (int) Math.round(count * (nullablePercent / 100.0));
                Set<Integer> indexes = new HashSet<>();
                while (indexes.size() < nullableCount) {
                    indexes.add(defaultFaker.random().nextInt(count));
                }
                nullableIndexes.put(field.getName(), indexes);
            }
        }

        // Data generation loop
        for (int i = 0; i < count; i++) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (FieldRequest field : fields) {
                String fieldName = field.getName();
                Object fieldValue;

                if (nullableIndexes.containsKey(fieldName) && nullableIndexes.get(fieldName).contains(i)) {
                    fieldValue = null;
                } else {
                    if ("fixed".equalsIgnoreCase(field.getType())) {
                        fieldValue = field.getValue();
                    } else {
                        fieldValue = generateValueByType(field);
                    }
                }
                row.put(fieldName, fieldValue);
            }
            generatedData.add(row);
        }

        return generatedData;
    }

    private Object generateValueByType(FieldRequest field) {
        String type = field.getType();
        Map<String, Object> constraints = field.getConstraints();
        Map<String, Object> parsedConstraints = field.getParsedConstraints();

        return switch (type) {
            // ... (The entire switch statement remains the same as before)
            case "korean_full_name" -> {
                String lastName = (String) parsedConstraints.getOrDefault("lastName", constraints.get("lastName"));
                if (lastName != null) {
                    yield lastName + koreanFaker.name().firstName();
                } else {
                    yield koreanFaker.name().lastName() + koreanFaker.name().firstName();
                }
            }
            case "password" -> {
                Integer minLength = (Integer) parsedConstraints.getOrDefault("minimum_length", constraints.getOrDefault("minimum_length", 8));
                Integer upper = (Integer) parsedConstraints.getOrDefault("upper", constraints.getOrDefault("upper", 1));
                Integer lower = (Integer) parsedConstraints.getOrDefault("lower", constraints.getOrDefault("lower", 1));
                Integer numbers = (Integer) parsedConstraints.getOrDefault("numbers", constraints.getOrDefault("numbers", 1));
                Integer symbols = (Integer) parsedConstraints.getOrDefault("symbols", constraints.getOrDefault("symbols", 1));
                int min = (minLength != null) ? minLength : 8;
                int max = min + 5;
                boolean requireUppercase = (upper != null && upper > 0);
                boolean requireLowercase = (lower != null && lower > 0);
                boolean requireDigits = (numbers != null && numbers > 0);
                boolean requireSpecial = (symbols != null && symbols > 0);
                String password;
                boolean isPasswordValid;
                do {
                    password = defaultFaker.internet().password(min, max, true, true, true);
                    if(requireSpecial) {
                        password += defaultFaker.lorem().character();
                    }
                    long upperCount = password.chars().filter(Character::isUpperCase).count();
                    long lowerCount = password.chars().filter(Character::isLowerCase).count();
                    long digitCount = password.chars().filter(Character::isDigit).count();
                    long specialCount = password.chars().filter(c -> !Character.isLetterOrDigit(c)).count();
                    isPasswordValid = (!requireUppercase || upperCount >= upper) &&
                                      (!requireLowercase || lowerCount >= lower) &&
                                      (!requireDigits || digitCount >= numbers) &&
                                      (!requireSpecial || specialCount >= symbols);
                } while (!isPasswordValid);
                yield password;
            }
            // ... other cases
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

    private String generateBankRoutingNumber() {
        int firstDigit = defaultFaker.random().nextInt(4);
        String remainingDigits = defaultFaker.number().digits(8);
        return firstDigit + remainingDigits;
    }
}