package com.highfive.meetu.domain.auth.common.service.email;

import com.highfive.meetu.infra.smtp.MailClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

  private final MailClient mailClient;

  @Override
  public void sendPasswordResetCode(String toEmail, String code) {
    String subject = "[MeetU] 비밀번호 재설정 인증 코드 안내";
    String body = String.format(
        "안녕하세요,\n\n" +
            "비밀번호 재설정을 위한 인증 코드는 다음과 같습니다:\n\n" +
            "    %s\n\n" +
            "이 코드는 10분간 유효합니다.\n\n" +
            "감사합니다.\nMeetU 드림",
        code
    );
    mailClient.send(toEmail, subject, body);
  }
}
