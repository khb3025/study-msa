package com.logstatistics.parser;

import com.logstatistics.domain.LogRecord;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class LogLineParserTest {

    private final LogLineParser parser = new LogLineParser();

    @Test
    void 정상_라인을_파싱한다() {
        LogRecord record = parser.parse("[200][http://apis.kokoa.com/search/blog?apikey=ab12][Chrome][10:00:00]");

        assertThat(record.statusCode()).isEqualTo(200);
        assertThat(record.serviceId()).isEqualTo("blog");
        assertThat(record.apiKey()).isEqualTo("ab12");
        assertThat(record.browser()).isEqualTo("Chrome");
        assertThat(record.calledAt()).isEqualTo(LocalTime.of(10, 0, 0));
    }

    @Test
    void apikey_파라미터가_없으면_apiKey는_null이다() {
        LogRecord record = parser.parse("[10][http://apis.kokoa.com/search/image][IE][10:04:00]");

        assertThat(record.statusCode()).isEqualTo(10);
        assertThat(record.serviceId()).isEqualTo("image");
        assertThat(record.apiKey()).isNull();
    }

    @Test
    void 형식이_맞지_않는_라인은_null을_반환한다() {
        assertThat(parser.parse("이상한 로그 라인")).isNull();
        assertThat(parser.parse("")).isNull();
        assertThat(parser.parse(null)).isNull();
    }
}
