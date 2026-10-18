package com.logstatistics.service;

import java.util.List;

/**
 * STEP1 요구사항 3가지를 모두 담은 분석 결과.
 *
 * @param mostCalledApiKey  1. 최다호출 APIKEY (호출 기록이 없으면 null)
 * @param topServiceCalls   2. 호출 횟수 상위 3개 API Service ID 와 요청 수
 * @param allServiceCalls   전체 API Service ID 별 호출 횟수 (내림차순, 엑셀 출력용)
 * @param browserRatios     3. 웹브라우저별 사용 비율 (내림차순)
 */
public record LogStatisticsResult(
        long totalRequests,
        ApiKeyCount mostCalledApiKey,
        List<ServiceCallCount> topServiceCalls,
        List<ServiceCallCount> allServiceCalls,
        List<BrowserRatio> browserRatios
) {
}
