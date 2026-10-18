package com.logstatistics.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class LogAnalysisServiceTest {

    private final LogAnalysisService service = new LogAnalysisService();

    // apikey: ab12 x5, cd34 x3, ef56 x1 (10번 라인은 apikey 없음)
    // serviceId: blog x3, news x3, image x2, book x1, vclip x1
    // browser: Chrome x5, Firefox x2, IE x1, Safari x1, Opera x1
    private static final List<String> LOG_LINES = List.of(
            "[200][http://apis.kokoa.com/search/blog?apikey=ab12][Chrome][10:00:00]",
            "[200][http://apis.kokoa.com/search/blog?apikey=ab12][Chrome][10:01:00]",
            "[200][http://apis.kokoa.com/search/news?apikey=cd34][Firefox][10:02:00]",
            "[404][http://apis.kokoa.com/search/news?apikey=ab12][Chrome][10:03:00]",
            "[10][http://apis.kokoa.com/search/image][IE][10:04:00]",
            "[200][http://apis.kokoa.com/search/image?apikey=cd34][Safari][10:05:00]",
            "[200][http://apis.kokoa.com/search/book?apikey=ef56][Chrome][10:06:00]",
            "[200][http://apis.kokoa.com/search/blog?apikey=ab12][Opera][10:07:00]",
            "[404][http://apis.kokoa.com/search/vclip?apikey=cd34][Firefox][10:08:00]",
            "[200][http://apis.kokoa.com/search/news?apikey=ab12][Chrome][10:09:00]"
    );

    @Test
    void 최다호출_APIKEY를_찾는다() {
        LogStatisticsResult result = service.analyze(LOG_LINES);

        assertThat(result.totalRequests()).isEqualTo(10);
        assertThat(result.mostCalledApiKey().apiKey()).isEqualTo("ab12");
        assertThat(result.mostCalledApiKey().count()).isEqualTo(5);
    }

    @Test
    void 호출_횟수_상위_3개_서비스ID를_내림차순으로_구한다() {
        LogStatisticsResult result = service.analyze(LOG_LINES);

        assertThat(result.topServiceCalls()).containsExactly(
                new ServiceCallCount("blog", 3),
                new ServiceCallCount("news", 3),
                new ServiceCallCount("image", 2)
        );
    }

    @Test
    void 웹브라우저별_사용_비율을_계산한다() {
        LogStatisticsResult result = service.analyze(LOG_LINES);

        assertThat(result.browserRatios()).hasSize(5);
        BrowserRatio chrome = result.browserRatios().get(0);
        assertThat(chrome.browser()).isEqualTo("Chrome");
        assertThat(chrome.count()).isEqualTo(5);
        assertThat(chrome.percentage()).isCloseTo(50.0, within(0.001));

        double totalPercentage = result.browserRatios().stream()
                .mapToDouble(BrowserRatio::percentage)
                .sum();
        assertThat(totalPercentage).isCloseTo(100.0, within(0.001));
    }
}
