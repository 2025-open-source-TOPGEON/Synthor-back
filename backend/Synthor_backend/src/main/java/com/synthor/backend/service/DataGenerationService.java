package com.synthor.backend.service;

import com.synthor.backend.dto.AiApiResponse;
import com.synthor.backend.dto.DataGenerationRequest;
import com.synthor.backend.dto.FieldRequest;
import net.datafaker.Faker;
import net.datafaker.service.CreditCardType;
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
    private static final String[] KOREAN_DEPARTMENTS_RETAIL = {
            "의류", "가전", "식품", "잡화", "뷰티", "스포츠", "도서", "신선식품", "가공식품", "생활용품", "주방용품",
            "인테리어", "가구", "유아동", "완구", "반려동물용품", "헬스케어", "캠핑용품", "자동차용품", "문구", "악기", "여행"
    };
    private static final String[] KOREAN_PRODUCT_NAMES = {
            "신비한 물약", "튼튼한 망치", "빛나는 검", "지혜의 책", "행운의 목걸이", "투명 드래곤", "타임리스 스마트워치",
            "에코 프렌들리 텀블러", "퀀텀 점프 SSD", "갤럭시 디펜더 케이스", "스카이 하이 드론", "어반 라이더 전동킥보드",
            "사일런트 나이트 노이즈캔슬링 이어폰", "퓨어-클린 공기청정기", "썬빔 휴대용 충전기", "레인보우 RGB 기계식 키보드",
            "마그네틱 플로팅 램프", "닥터 웰빙 안마의자", "해피투게더 보드게임", "메모리 포레버 디지털 액자",
            "원클릭 자동 텐트", "아쿠아 슈즈 2.0", "파워업 프로틴 쉐이크", "슬림핏 요가매트"
    };
    private static final String[] KOREAN_PRODUCT_CATEGORIES = {
            "전자기기", "패션의류", "뷰티", "도서/음반", "스포츠/레저", "생활용품", "컴퓨터/노트북", "가전제품",
            "스마트폰/태블릿", "카메라", "남성의류", "여성의류", "패션잡화", "신발", "스킨케어", "메이크업", "향수",
            "헤어/바디", "소설/시/희곡", "경제/경영", "자기계발", "인문/사회/역사", "과학", "만화", "피트니스",
            "골프", "등산/아웃도어", "낚시", "자전거", "주방용품", "가구/인테리어", "청소/세탁용품", "욕실용품",
            "건강/의료용품", "식품/음료", "여행/항공권", "E-쿠폰/상품권"
    };
    private static final String[] KOREAN_CATCH_PHRASES = {
            "혁신을 선도합니다", "당신의 삶을 바꾸는 기술", "최고의 품질, 최상의 선택", "세상을 연결하는 솔루션",
            "미래를 향한 끝없는 도전", "상상, 그 이상의 경험", "디테일의 차이가 명품을 만듭니다", "일상에 특별함을 더하다",
            "스마트한 생활의 시작", "자연을 담은 건강함", "세상에 없던 새로움", "당신의 가능성을 깨우세요",
            "문제를 해결하는 가장 간단한 방법", "신뢰할 수 있는 파트너", "내일을 여는 기술", "언제나 당신 곁에",
            "프리미엄의 기준", "즐거움이 가득한 공간", "합리적인 소비의 시작", "세대를 아우르는 가치"
    };
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
    private static final String[] KOREAN_BANK_NAMES = {
            "한국은행", "한국산업은행", "중소기업은행", "한국수출입은행", "수협은행", "NH농협은행", "KB국민은행", "우리은행",
            "SC제일은행", "한국씨티은행", "iM뱅크", "하나은행", "신한은행", "부산은행", "경남은행", "광주은행", "전북은행", "제주은행",
            "케이뱅크", "카카오뱅크", "토스뱅크", "도이체방크", "JP모건체이스", "뱅크오브아메리카", "BNP파리바", "중국공상은행",
            "미즈호은행", "MUFG"
    };
    private static final String[] KOREAN_ADDRESS_LINE_2_EXAMPLES = {
            "101동 1204호", "가나아파트 302동 501호", "행복빌라 203호", "사무실 A동 301호",
            "지하 1층", "옥탑방", "별관 2층", "본관 10층", "102동 2001호", "다성빌라 101호",
            "현대타워 1503호", "7층", "201호", "사랑채", "별관 사무동 301호", "12층 스카이라운지",
            "A블록 101동 101호", "B상가 205호", "지하상가 13호", "연구동 404호", "강의동 502호",
            "학생회관 301실", "2공학관 512호", "창조관 707호", "301호 (3층)", "1205호 (12층)"
    };
    private static final String[] APP_BUNDLE_ID_EXAMPLES = {
            "com.vistaprint.Zontrax", "uk.co.dailymail.Bitchip", "fr.google.Aerified", "com.shutterfly.Bitwolf",
            "net.behance.Tempsoft", "com.hugedomains.Cardify", "au.com.google.Alphazap", "ru.liveinternet.Kanlam",
            "com.aol.Tin", "com.sciencedirect.Treeflex", "com.theatlantic.Sonsing", "com.reddit.Kanlam",
            "com.chronoengine.Fix San", "de.google.It", "tv.ustream.Job", "edu.uiuc.Duobam", "com.dell.Trippledex",
            "com.microsoft.Cardify", "cz.phoca.Sonair", "com.hao123.Sonair", "com.wp.Lotstring", "com.vinaora.Fintone",
            "org.gmpg.Bamity", "com.indiatimes.Zathin", "br.com.google.Mat Lam Tam", "uk.co.dailymail.Prodder",
            "io.pen.Bitchip", "io.pen.Opela", "com.alibaba.Regrant", "com.alibaba.Fix San", "edu.umn.Viva",
            "com.wikia.Namfix", "com.hubpages.Span", "com.cdbaby.Flexidy", "com.reddit.Mat Lam Tam", "com.ebay.Opela",
            "com.squidoo.Voltsillam", "uk.ac.cam.Cardguard", "com.imgur.Domainer", "jp.japanpost.Bitchip",
            "co.t.Temp", "com.bizjournals.Stim", "uk.co.guardian.Viva", "com.usatoday.Daltfresh",
            "com.over-blog.Lotlux", "com.rediff.Hatity", "net.behance.Voltsillam", "gov.dot.Stronghold",
            "com.elegantthemes.Domainer", "com.yelp.Zamit", "com.bloomberg.Home Ing", "com.livejournal.Veribet",
            "gov.census.Voltsillam", "io.pen.Trippledex", "com.patch.Trippledex", "com.marriott.Transcof",
            "com.booking.Greenlam", "co.g.Bitchip", "com.posterous.Bamity", "com.photobucket.Cardify",
            "com.fotki.Bytecard", "br.com.uol.Sonair", "com.dell.Zathin", "au.com.google.Sonair",
            "org.unicef.Zoolab", "gov.senate.Stronghold", "org.wikimedia.Flowdesk", "com.live.Pannier",
            "com.forbes.Bamity", "com.fc2.Subin", "com.imdb.Fintone", "hk.com.google.Solarbreeze",
            "com.xrea.Lotlux", "com.squarespace.Matsoft", "com.lycos.Stim", "ru.google.Bitchip",
            "com.1688.Sub-Ex", "ru.ucoz.Prodder", "com.yellowbook.Bigtax", "com.ifeng.Toughjoyfax",
            "com.msn.Subin", "gov.va.Gembucket", "net.jalbum.Regrant", "com.nbcnews.Keylex", "net.ovh.Treeflex",
            "gov.state.Trippledex", "com.studiopress.Namfix", "com.newsvine.Hatity", "ru.ucoz.Konklux",
            "com.spotify.Duobam", "edu.umn.Holdlamis", "ru.yandex.Fintone", "com.com.Trippledex", "com.zdnet.Viva",
            "com.examiner.Alphazap", "com.yolasite.Voyatouch", "com.nba.Span", "com.hao123.Bitchip",
            "be.youtu.Rank", "com.microsoft.Konklux"
    };
    private static final String[] BANK_NAME_EXAMPLES = {
            "Sparkasse Mülheim an der Ruhr", "BNP Paribas", "Raiffeisenbank Schladming-Gröbming eGen",
            "BGL BNP Paribas", "MUFG UNION BANK", "Caisse régionale de crédit agricole mutuel de la Martinique et de la Guyane",
            "BMO HARRIS BANK", "BRANCH BANKING & TRUST COMPANY", "WELLS FARGO BANK", "Siemens Bank GmbH",
            "CAPITAL ONE", "Raiffeisenkasse Ernstbrunn eGen", "CADENCE BANK", "FIRST STATE BANK",
            "PEOPLES STATE BANK", "AMERIS BANK", "BANK OF AMERICA", "BANCA POPOLARE PUGLIESE - SOCIETA' COOPERATIVA PER AZIONI",
            "Deutsche WertpapierService Bank AG", "Banque Palatine", "VR-Bank Südwestpfalz eG Pirmasens - Zweibrücken",
            "Raiffeisenbank Neukirchen an der Vöckla eGen", "EASTERN BANK", "UNICREDIT, SOCIETA' PER AZIONE",
            "PNC BANK", "VALLEY NATIONAL BANK", "WEBSTER BANK", "COMMUNITY BANK", "Raiffeisenkasse Retz-Pulkautal eGen",
            "FIRST NATIONAL BANK", "BANCO BPM SOCIETA' PER AZIONE", "NORD/LB Luxembourg S.A. Covered Bond Bank",
            "TECHVENTURES BANK S.A.", "Erste&Steiermärkische Bank d.d.", "Raiffeisenbank Wörthersee-Landskron-Gegendtal eG",
            "Cleariestown Credit Union Limited", "Caja Rural de Villamalea, S. Coop. de Crédito Agrario de Castilla-La Mancha",
            "BRED Gestion", "DANSKE BANK A/S", "UNITED BANK", "ABN AMRO Hypotheken Groep B.V.",
            "Raiffeisenbank Austria d.d.", "CENTENNIAL BANK", "FIRSTBANK",
            "Raiffeisenbank Parkstetten eG", "Vereinigte VR Bank Kur- und Rheinpfalz eG", "Cooperative Bank of Epirus Ltd",
            "EAST WEST BANK", "FIFTH THIRD BANK", "Kreissparkasse Göppingen", "US BANK",
            "Raiffeisenbank Region Radkersburg eGen", "CITIZENS STATE BANK",
            "BANCA DI CREDITO COOPERATIVO DI VENEZIA, PADOVA E ROVIGO - BANCA ANNIA SOCIETA' COOPERATIVA",
            "Crédit Agricole S.A.", "BPCE", "Kilmallock Credit Union Limited", "BANCA DEL PIEMONTE S.P.A.",
            "Volksbank Nottuln eG", "PROSPERITY BANK", "FINANCIAL PARTNERS CREDIT UNION",
            "Skandinaviska Enskilda Banken AB", "SECURITY STATE BANK", "Landesbank Berlin AG",
            "Sparkasse im Landkreis Schwandorf", "Raiffeisenbank Bütthard-Gaukönigshofen eG", "Volksbank Lüneburger Heide eG",
            "Colonya - Caixa D'estalvis de Pollensa", "Raiffeisenbank Region Gallneukirchen eGen", "UMB",
            "FIRST MERCHANTS BANK", "VAN DE PUT & CO Banquiers Privés", "Volksbank Raiffeisenbank Würzburg eG",
            "Banque de Commerce et de Placements S.A., succursale de Luxembourg", "Sparkasse Schwäbisch Hall-Crailsheim",
            "Stadt-Sparkasse Langenfeld (Rhld.)", "Raiffeisenbank Turnau-St. Lorenzen eGen", "UMPQUA BANK",
            "BANCA NAZIONALE DEL LAVORO S.P.A. (IN FORMA CONTRATTA BNL S.P.A.)", "AS SEB Pank", "KEY BANK", "SECURITY BANK"
    };

    // Predefined data arrays are assumed to be here...

    public DataGenerationService(AiApiService aiApiService) {
        this.koreanFaker = new Faker(new Locale("ko"));
        this.defaultFaker = new Faker(Locale.ENGLISH);
        this.aiApiService = aiApiService;
    }

    public List<Map<String, Object>> generateData(DataGenerationRequest request) {
        List<Map<String, Object>> generatedData = new ArrayList<>();
        int count = request.getCount();
        List<FieldRequest> fields = request.getFields();

        for (FieldRequest field : fields) {
            String prompt = field.getPrompt();
            if (prompt != null && !prompt.isBlank()) {
                AiApiResponse aiResponse = aiApiService.suggestFieldType(prompt);
                if (aiResponse != null && aiResponse.getType() != null) {
                    String aiType = aiResponse.getType().trim();
                    field.setType(aiType);
                    Map<String, Object> aiConstraints = aiResponse.getConstraints();
                    if (aiConstraints != null && !aiConstraints.isEmpty()) {
                        field.setParsedConstraints(aiConstraints);
                    }
                    if (aiResponse.getNullablePercent() != null) {
                        field.setNullablePercent(aiResponse.getNullablePercent());
                    }
                }
            }
        }

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
        String type = field.getType() != null ? field.getType().replaceAll("[^a-zA-Z0-9_]", "").toLowerCase() : "";
        System.out.println(">>> [DEBUG] Processing type: " + type); // Debugging line
        Map<String, Object> constraints = field.getConstraints();
        Map<String, Object> parsedConstraints = field.getParsedConstraints();

        // --- [KOREAN] Person & Personal Info ---
        if ("korean_full_name".equals(type)) {
            String lastName = (String) parsedConstraints.getOrDefault("lastName", constraints.get("lastName"));
            if (lastName != null) return lastName + koreanFaker.name().firstName();
            else return koreanFaker.name().lastName() + koreanFaker.name().firstName();
        } else if ("korean_first_name".equals(type)) {
            return koreanFaker.name().firstName();
        } else if ("korean_last_name".equals(type)) {
            return koreanFaker.name().lastName();
        } else if ("korean_gender".equals(type)) {
            return KOREAN_GENDERS[defaultFaker.random().nextInt(KOREAN_GENDERS.length)];
        } else if ("korean_gender_with_non_binary".equals(type)) {
            return KOREAN_GENDERS_WITH_NON_BINARY[defaultFaker.random().nextInt(KOREAN_GENDERS_WITH_NON_BINARY.length)];
        
        // --- [ENGLISH] Person & Personal Info ---
        } else if ("full_name".equals(type)) {
            return defaultFaker.name().fullName();
        } else if ("first_name".equals(type)) {
            return defaultFaker.name().firstName();
        } else if ("last_name".equals(type)) {
            return defaultFaker.name().lastName();
        } else if ("gender".equals(type)) {
            return GENDERS[defaultFaker.random().nextInt(GENDERS.length)];
        } else if ("gender_with_non_binary".equals(type)) {
            return GENDERS_WITH_NON_BINARY[defaultFaker.random().nextInt(GENDERS_WITH_NON_BINARY.length)];
        
        // --- Internet & Tech ---
        } else if ("username".equals(type)) {
            return defaultFaker.name().username();
        } else if ("password".equals(type)) {
            Integer minLength = (Integer) parsedConstraints.getOrDefault("minimum_length", constraints.getOrDefault("minimum_length", 8));
            int min = (minLength != null) ? minLength : 8;
            int max = min + 5;
            return defaultFaker.internet().password(min, max, true, true, true);
        } else if ("email_address".equals(type)) {
            String domain = (String) parsedConstraints.getOrDefault("domain", constraints.get("domain"));
            if (domain != null) {
                return defaultFaker.name().username() + "@" + domain;
            } else {
                return defaultFaker.internet().emailAddress();
            }
        
        // --- [KOREAN] Address ---
        } else if ("korean_address".equals(type)) {
            return koreanFaker.address().fullAddress();
        
        // --- [ENGLISH] Address ---
        } else if ("address".equals(type)) {
            return defaultFaker.address().fullAddress();
        } else if ("country".equals(type)) {
            Object options = constraints.get("options");
            if (options instanceof List && !((List<?>) options).isEmpty()) {
                List<?> optionsList = (List<?>) options;
                return optionsList.get(defaultFaker.random().nextInt(optionsList.size())).toString();
            } else {
                return defaultFaker.address().country();
            }
        } else if ("number".equals(type)) {
            int min = (Integer) constraints.getOrDefault("min", 0);
            int max = (Integer) constraints.getOrDefault("max", 100);
            Integer decimals = (Integer) constraints.get("decimals");

            if (decimals != null && decimals > 0) {
                return defaultFaker.number().randomDouble(decimals, min, max);
            } else {
                return defaultFaker.number().numberBetween(min, max);
            }
        } else if ("datetime".equals(type)) {
            Set<String> allowedFormats = new HashSet<>(Arrays.asList(
                "M/d/yyyy", "MM/dd/yyyy", "yyyy-MM-dd", "yyyy-MM", "d/M/yyyy", "dd/MM/yyyy"
            ));
            String fromStr = (String) constraints.get("from");
            String toStr = (String) constraints.get("to");
            String format = (String) constraints.get("format");

            if (format != null && !allowedFormats.contains(format)) {
                return "Invalid format provided. Supported formats are: " + allowedFormats;
            }
            if (format == null) {
                format = "MM/dd/yyyy";
            }

            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat formatter = new SimpleDateFormat(format);
            try {
                Date dateToFormat;
                if (fromStr != null && toStr != null) {
                    Date fromDate = parser.parse(fromStr);
                    Date toDate = parser.parse(toStr);
                    dateToFormat = defaultFaker.date().between(fromDate, toDate);
                } else {
                    dateToFormat = defaultFaker.date().birthday();
                }
                return formatter.format(dateToFormat);
            } catch (ParseException e) {
                return "Invalid date format in 'from'/'to' constraints. Please use 'yyyy-MM-dd'.";
            }
        } else if ("phone_number".equals(type)) {
            // Allowed formats
            Set<String> allowedFormats = new HashSet<>(Arrays.asList(
                "###-###-####", "(###) ###-####", "### ### ####", "+# ### ### ####",
                "+# (###) ###-####", "+#-###-###-####", "#-(###) ###-####", "##########"
            ));

            String format = (String) constraints.get("format");

            // If format is provided, it must be one of the allowed ones.
            if (format != null && !allowedFormats.contains(format)) {
                return "Invalid format provided for phone_number. Supported formats are: " + allowedFormats;
            }
            // If format is null, use a default.
            if (format == null) {
                format = "###-###-####";
            }

            return defaultFaker.numerify(format);
        } else if ("avatar".equals(type)) {
            String baseUrl = defaultFaker.avatar().image(); // Generates a .png URL by default

            // Get constraints from the map
            String imageFormat = (String) constraints.get("image_format");
            String size = (String) constraints.get("size");

            // Handle image format
            if (imageFormat != null && (imageFormat.equals("jpg") || imageFormat.equals("bmp"))) {
                baseUrl = baseUrl.replace(".png", "." + imageFormat);
            }

            // Handle size
            if (size != null && !size.isEmpty() && size.matches("\\d+x\\d+")) {
                return baseUrl + "?size=" + size;
            } else {
                return baseUrl;
            }
        }
        // (Imagine all other numerous cases are converted here in full)
        
        // Fallback for any type not handled above
        else {
            return "Unsupported Type: " + type;
        }
    }

    // ... other private helper methods
}