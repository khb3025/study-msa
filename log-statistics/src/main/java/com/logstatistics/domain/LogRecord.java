package com.logstatistics.domain;

import java.time.LocalTime;

/**
 * 로그 한 줄을 파싱한 결과.
 * serviceId, apiKey 는 URL 형식이 비정상이거나 apikey 파라미터가 없으면 null 일 수 있다.
 */
public record LogRecord(
        int statusCode,
        String serviceId,
        String apiKey,
        String browser,
        LocalTime calledAt
) {
}
