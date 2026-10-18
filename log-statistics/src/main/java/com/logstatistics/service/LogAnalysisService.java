package com.logstatistics.service;

import com.logstatistics.domain.LogRecord;
import com.logstatistics.parser.LogLineParser;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 웹 로그를 파싱하고 STEP1 요구사항 3가지 통계를 산출한다.
 *
 * <p>집계 기준
 * <ul>
 *   <li>APIKEY 통계 : apikey 파라미터가 존재하는 요청만 집계 (상태코드 10 은 apikey 가 없으므로 제외)</li>
 *   <li>API Service ID 통계 : 상태코드와 무관하게 URL 에 서비스 ID 가 식별되는 모든 요청을 집계</li>
 *   <li>웹브라우저 비율 : 상태코드와 무관한 전체 요청 기준 비율</li>
 * </ul>
 */
@Service
public class LogAnalysisService {

    private static final int TOP_SERVICE_COUNT = 3;

    private final LogLineParser lineParser = new LogLineParser();

    public List<LogRecord> parseLines(List<String> lines) {
        return lines.stream()
                .map(lineParser::parse)
                .filter(Objects::nonNull)
                .toList();
    }

    public LogStatisticsResult analyze(List<String> lines) {
        List<LogRecord> records = parseLines(lines);

        List<ApiKeyCount> apiKeyCounts = countDescending(
                records.stream().map(LogRecord::apiKey).filter(Objects::nonNull),
                ApiKeyCount::new
        );

        List<ServiceCallCount> serviceCounts = countDescending(
                records.stream().map(LogRecord::serviceId).filter(Objects::nonNull),
                ServiceCallCount::new
        );

        ApiKeyCount mostCalledApiKey = apiKeyCounts.isEmpty() ? null : apiKeyCounts.get(0);
        List<ServiceCallCount> topServiceCalls = serviceCounts.stream()
                .limit(TOP_SERVICE_COUNT)
                .toList();

        List<BrowserRatio> browserRatios = calculateBrowserRatios(records);

        return new LogStatisticsResult(records.size(), mostCalledApiKey, topServiceCalls, serviceCounts, browserRatios);
    }

    private <T> List<T> countDescending(java.util.stream.Stream<String> values, java.util.function.BiFunction<String, Long, T> factory) {
        Map<String, Long> counts = values.collect(Collectors.groupingBy(v -> v, Collectors.counting()));
        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(entry -> factory.apply(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<BrowserRatio> calculateBrowserRatios(List<LogRecord> records) {
        long total = records.size();
        Map<String, Long> counts = records.stream()
                .map(LogRecord::browser)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(v -> v, Collectors.counting()));

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing(Map.Entry::getKey))
                .map(entry -> new BrowserRatio(
                        entry.getKey(),
                        entry.getValue(),
                        total == 0 ? 0.0 : entry.getValue() * 100.0 / total))
                .toList();
    }
}
