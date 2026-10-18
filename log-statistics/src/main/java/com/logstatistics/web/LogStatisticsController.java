package com.logstatistics.web;

import com.logstatistics.excel.ExcelReportGenerator;
import com.logstatistics.service.LogAnalysisService;
import com.logstatistics.service.LogStatisticsResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * 로그 분석/엑셀 리포트 확인용 API.
 *
 * <p>샘플 로그(src/main/resources/sample-logs/access.log)로 바로 확인하거나,
 * 직접 가지고 있는 로그 파일을 업로드해서 확인할 수 있다.
 */
@RestController
@RequestMapping("/api/log-statistics")
public class LogStatisticsController {

    private static final Path OUTPUT_DIR = Path.of("output");

    private final LogAnalysisService logAnalysisService;
    private final ExcelReportGenerator excelReportGenerator;

    @Value("classpath:sample-logs/access.log")
    private Resource sampleLogResource;

    public LogStatisticsController(LogAnalysisService logAnalysisService, ExcelReportGenerator excelReportGenerator) {
        this.logAnalysisService = logAnalysisService;
        this.excelReportGenerator = excelReportGenerator;
    }

    @GetMapping("/sample-summary")
    public LogStatisticsResult sampleSummary() throws IOException {
        return analyze(sampleLogResource.getInputStream());
    }

    @PostMapping("/summary")
    public LogStatisticsResult summary(@RequestParam("file") MultipartFile file) throws IOException {
        return analyze(file.getInputStream());
    }

    @GetMapping("/sample-report")
    public ResponseEntity<byte[]> sampleReport() throws IOException {
        LogStatisticsResult result = analyze(sampleLogResource.getInputStream());
        return buildExcelResponse(result, "sample-log-statistics-report.xlsx");
    }

    @PostMapping("/report")
    public ResponseEntity<byte[]> report(@RequestParam("file") MultipartFile file) throws IOException {
        LogStatisticsResult result = analyze(file.getInputStream());
        return buildExcelResponse(result, "log-statistics-report.xlsx");
    }

    private LogStatisticsResult analyze(InputStream inputStream) throws IOException {
        return logAnalysisService.analyze(readLines(inputStream));
    }

    private List<String> readLines(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().toList();
        }
    }

    private ResponseEntity<byte[]> buildExcelResponse(LogStatisticsResult result, String filename) throws IOException {
        byte[] excelBytes = excelReportGenerator.generate(result);

        Files.createDirectories(OUTPUT_DIR);
        Files.write(OUTPUT_DIR.resolve(filename), excelBytes);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }
}
