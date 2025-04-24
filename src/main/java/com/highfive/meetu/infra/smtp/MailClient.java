package com.highfive.meetu.infra.smtp;

/**
 * Low‑level API for sending a plain‑text email.
 */
public interface MailClient {
    /**
     * @param to      받는 사람 이메일
     * @param subject 메일 제목
     * @param body    메일 본문
     */
    void send(String to, String subject, String body);

    /**
     * @param to      받는 사람 이메일
     * @param subject 메일 제목
     * @param body    메일 본문
     * @param isHtml  HTML 여부
     */
    void send(String to, String subject, String body, boolean isHtml);
}
