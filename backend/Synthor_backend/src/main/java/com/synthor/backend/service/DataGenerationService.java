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
            "Raiffeisenbank Austria d.d.", "AB \"Fjord Bank\"", "CENTENNIAL BANK", "FIRSTBANK",
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
                    fieldValue = generateValueByType(field);
                }
                row.put(fieldName, fieldValue);
            }
            generatedData.add(row);
        }

        return generatedData;
    }

    private Object generateValueByType(FieldRequest field) {
        String type = field.getType();
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
            case "korean_bank_name" -> KOREAN_BANK_NAMES[defaultFaker.random().nextInt(KOREAN_BANK_NAMES.length)];

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
            case "password" -> {
                Integer minLength = field.getMinimumLength();
                Integer upper = field.getUpper();
                int min = (minLength != null) ? minLength : 8;
                int max = min + 5;
                boolean includeUppercase = (upper != null && upper > 0);

                yield defaultFaker.internet().password(min, max, includeUppercase);
            }
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
            case "app_bundle_id" -> APP_BUNDLE_ID_EXAMPLES[defaultFaker.random().nextInt(APP_BUNDLE_ID_EXAMPLES.length)];
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
            case "bank_name" -> BANK_NAME_EXAMPLES[defaultFaker.random().nextInt(BANK_NAME_EXAMPLES.length)];
            case "bank_routing_number" -> generateBankRoutingNumber();

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

    private String generateBankRoutingNumber() {
        int firstDigit = defaultFaker.random().nextInt(4); // 0, 1, 2, 3
        String remainingDigits = defaultFaker.number().digits(8);
        return firstDigit + remainingDigits;
    }
}
