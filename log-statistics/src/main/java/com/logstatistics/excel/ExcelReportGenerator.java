package com.logstatistics.excel;

import com.logstatistics.service.BrowserRatio;
import com.logstatistics.service.LogStatisticsResult;
import com.logstatistics.service.ServiceCallCount;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 로그 분석 결과를 엑셀(.xlsx) 파일로 만든다.
 * - "요약" : 최다호출 APIKEY, 호출 상위 3개 API Service ID
 * - "API 서비스 호출 통계" : 전체 API Service ID 별 호출 횟수 (내림차순)
 * - "브라우저 사용 비율" : 웹브라우저별 호출 횟수 및 사용 비율(%) (내림차순)
 */
@Component
public class ExcelReportGenerator {

    public byte[] generate(LogStatisticsResult result) throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle percentStyle = createPercentStyle(workbook);

            writeSummarySheet(workbook, headerStyle, result);
            writeServiceCallSheet(workbook, headerStyle, result);
            writeBrowserRatioSheet(workbook, headerStyle, percentStyle, result);

            workbook.write(out);
            return out.toByteArray();
        }
    }

    private void writeSummarySheet(XSSFWorkbook workbook, CellStyle headerStyle, LogStatisticsResult result) {
        Sheet sheet = workbook.createSheet("요약");
        int rowIdx = 0;

        Row titleRow = sheet.createRow(rowIdx++);
        titleRow.createCell(0).setCellValue("총 요청 수");
        titleRow.createCell(1).setCellValue(result.totalRequests());

        rowIdx++;
        Row apiKeyHeader = sheet.createRow(rowIdx++);
        apiKeyHeader.createCell(0).setCellValue("최다호출 APIKEY");
        apiKeyHeader.createCell(1).setCellValue("호출 횟수");
        applyHeaderStyle(apiKeyHeader, headerStyle);

        Row apiKeyRow = sheet.createRow(rowIdx++);
        if (result.mostCalledApiKey() != null) {
            apiKeyRow.createCell(0).setCellValue(result.mostCalledApiKey().apiKey());
            apiKeyRow.createCell(1).setCellValue(result.mostCalledApiKey().count());
        } else {
            apiKeyRow.createCell(0).setCellValue("데이터 없음");
        }

        rowIdx++;
        Row topServiceHeader = sheet.createRow(rowIdx++);
        topServiceHeader.createCell(0).setCellValue("순위");
        topServiceHeader.createCell(1).setCellValue("API Service ID");
        topServiceHeader.createCell(2).setCellValue("요청 수");
        applyHeaderStyle(topServiceHeader, headerStyle);

        int rank = 1;
        for (ServiceCallCount call : result.topServiceCalls()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(rank++);
            row.createCell(1).setCellValue(call.serviceId());
            row.createCell(2).setCellValue(call.count());
        }

        for (int col = 0; col <= 2; col++) {
            sheet.autoSizeColumn(col);
        }
    }

    private void writeServiceCallSheet(XSSFWorkbook workbook, CellStyle headerStyle, LogStatisticsResult result) {
        Sheet sheet = workbook.createSheet("API 서비스 호출 통계");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("API Service ID");
        header.createCell(1).setCellValue("호출 횟수");
        applyHeaderStyle(header, headerStyle);

        int rowIdx = 1;
        for (ServiceCallCount call : result.allServiceCalls()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(call.serviceId());
            row.createCell(1).setCellValue(call.count());
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void writeBrowserRatioSheet(XSSFWorkbook workbook, CellStyle headerStyle, CellStyle percentStyle, LogStatisticsResult result) {
        Sheet sheet = workbook.createSheet("브라우저 사용 비율");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("웹브라우저");
        header.createCell(1).setCellValue("호출 횟수");
        header.createCell(2).setCellValue("사용 비율");
        applyHeaderStyle(header, headerStyle);

        int rowIdx = 1;
        for (BrowserRatio ratio : result.browserRatios()) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(ratio.browser());
            row.createCell(1).setCellValue(ratio.count());
            Cell percentCell = row.createCell(2);
            percentCell.setCellValue(ratio.percentage() / 100.0);
            percentCell.setCellStyle(percentStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
    }

    private void applyHeaderStyle(Row row, CellStyle headerStyle) {
        for (Cell cell : row) {
            cell.setCellStyle(headerStyle);
        }
    }

    private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(boldFont);
        return style;
    }

    private CellStyle createPercentStyle(XSSFWorkbook workbook) {
        DataFormat format = workbook.createDataFormat();
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(format.getFormat("0.00%"));
        return style;
    }
}
