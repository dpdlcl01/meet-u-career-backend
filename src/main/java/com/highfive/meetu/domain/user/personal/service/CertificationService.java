package com.highfive.meetu.domain.user.personal.service;

import com.highfive.meetu.domain.user.personal.type.CertificationVerifyResult;
import com.highfive.meetu.infra.smtp.MailClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 이메일 인증 코드 발송 서비스
 */
@Service
@RequiredArgsConstructor
public class CertificationService {
    private final MailClient mailClient;  // 메일 발송 클라이언트 (SMTP)
    private final StringRedisTemplate redisTemplate; // Redis
    private static final long CERTIFICATION_EXPIRATION_MINUTES = 5; // 인증 유효시간 5분

    /**
     * 이메일 인증코드 생성 후 메일 발송, Redis 저장
     *
     * @param name  사용자 이름
     * @param email 사용자 이메일
     */
    public void sendCertification(String name, String email) {
        String certificationCode = createCertificationCode();

        // 이메일 발송
        String subject = "Meet U 회원가입 인증 메일";
        String htmlContent = buildCertificationEmailContent(name, certificationCode);
        mailClient.send(email, subject, htmlContent, true);

        // Redis에 저장 (key = certification:{email}, value = code, TTL = 5분)
        redisTemplate.opsForValue().set(
            getCertificationKey(email),
            certificationCode,
            CERTIFICATION_EXPIRATION_MINUTES,
            TimeUnit.MINUTES
        );
    }

    /**
     * 인증코드 검증
     * @return "success", "expired", "invalid"
     */
    public CertificationVerifyResult verifyCertification(String email, String inputCode) {
        String key = getCertificationKey(email);
        String savedCode = redisTemplate.opsForValue().get(key);

        if (savedCode == null) {
            return CertificationVerifyResult.EXPIRED;  // 인증 시간 만료
        }

        if (savedCode.equals(inputCode)) {
            redisTemplate.delete(key);
            return CertificationVerifyResult.SUCCESS;  // 인증 성공
        }

        return CertificationVerifyResult.FAIL;  // 코드 불일치
    }


    /**
     * 6자리 숫자 인증 코드 생성
     *
     * @return 인증 코드 (문자열 형태)
     */
    public String createCertificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // 100000 ~ 999999 범위
        return String.valueOf(code);
    }

    /**
     * 이메일 본문 HTML 생성
     *
     * @param name 사용자 이름
     * @param code 인증 코드
     * @return HTML 문자열
     */
    private String buildCertificationEmailContent(String name, String code) {
        // 이름 중간 별표(*) 처리
        String maskName = maskName(name);

        return "<div style=\"padding:30px; text-align:center; font-family:'Arial', sans-serif;\">" +
            "<h2 style=\"margin-bottom:20px;\">이메일 인증코드 발급</h2>" +
            "<p style=\"margin-bottom:30px; font-size:14px; color:#555;\">" +
            maskName + "님의 이메일 인증을 위한 인증 코드를 발급하였습니다.</p>" +
            "<div style=\"font-size:36px; font-weight:bold; color:#1842a3;\">" + code + "</div>" +
            "<p style=\"margin-top:30px; font-size:12px; color:#999;\">" +
            "인증 코드를 인증창에 직접 입력하시거나 인증 코드를 복사 후 회원가입 페이지의 입력창에 붙여넣기 해주세요.</p>" +
            "</div>";
    }

    /**
     * 이름을 가운데 별표(*)로 마스킹
     *
     * @param name 사용자 이름
     * @return 마스킹된 이름
     */
    private String maskName(String name) {
        if (name == null || name.length() < 2) {
            return name; // 한 글자거나 null이면 그대로 반환
        }

        if (name.length() == 2) {
            // 이름이 2글자면 두 번째 글자만 * 처리
            return name.charAt(0) + "*";
        }

        // 이름이 3글자 이상이면 가운데 글자들을 *로 처리
        StringBuilder masked = new StringBuilder();
        masked.append(name.charAt(0)); // 첫 글자

        for (int i = 1; i < name.length() - 1; i++) {
            masked.append("*"); // 가운데 글자 별 처리
        }

        masked.append(name.charAt(name.length() - 1)); // 마지막 글자
        return masked.toString();
    }

    /**
     * Redis에 저장할 Key를 생성 (ex: certification:user@example.com)
     */
    private String getCertificationKey(String email) {
        return "certification:" + email;
    }

}
