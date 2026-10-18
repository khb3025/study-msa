package com.logstatistics.parser;

import com.logstatistics.domain.LogRecord;

import java.net.URI;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * "[상태코드][URL][웹브라우저][호출시간]" 형식의 로그 한 줄을 LogRecord 로 변환한다.
 */
public class LogLineParser {

    private static final Pattern LINE_PATTERN =
            Pattern.compile("^\\[(\\d{1,3})]\\[(.+?)]\\[(.+?)]\\[(.+?)]$");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    public LogRecord parse(String line) {
        if (line == null || line.isBlank()) {
            return null;
        }

        Matcher matcher = LINE_PATTERN.matcher(line.trim());
        if (!matcher.matches()) {
            return null;
        }

        int statusCode = Integer.parseInt(matcher.group(1));
        String url = matcher.group(2);
        String browser = matcher.group(3);
        LocalTime calledAt;
        try {
            calledAt = LocalTime.parse(matcher.group(4), TIME_FORMATTER);
        } catch (Exception e) {
            return null;
        }

        return new LogRecord(statusCode, extractServiceId(url), extractApiKey(url), browser, calledAt);
    }

    private String extractServiceId(String url) {
        try {
            String path = URI.create(url).getPath();
            if (path == null || path.isBlank()) {
                return null;
            }
            int lastSlash = path.lastIndexOf('/');
            String serviceId = lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
            return serviceId.isBlank() ? null : serviceId;
        } catch (Exception e) {
            return null;
        }
    }

    private String extractApiKey(String url) {
        try {
            String query = URI.create(url).getQuery();
            if (query == null || query.isBlank()) {
                return null;
            }
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=", 2);
                if (keyValue.length == 2 && "apikey".equals(keyValue[0])) {
                    return keyValue[1].isBlank() ? null : keyValue[1];
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
