package com.highfive.meetu.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.highfive.meetu.global.common.response.ResultData;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtErrorResponseUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 401 Unauthorized - 인증 실패 응답
     */
    public static void sendUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        ResultData<Void> result = ResultData.fail(message);
        String json = objectMapper.writeValueAsString(result);

        response.getWriter().write(json);
    }

    /**
     * 403 Forbidden - 권한 부족 응답
     */
    public static void sendForbidden(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        ResultData<Void> result = ResultData.fail(message);
        String json = objectMapper.writeValueAsString(result);

        response.getWriter().write(json);
    }
}
