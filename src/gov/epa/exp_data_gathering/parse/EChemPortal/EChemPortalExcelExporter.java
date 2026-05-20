package gov.epa.exp_data_gathering.parse.EChemPortal;

import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public class EChemPortalExcelExporter {

    public static class ColumnSpec {
        public final String header;
        public final Function<RecordEChemPortal, String> valueFn;
        public final boolean wrap;
        public final boolean hyperlink;

        public ColumnSpec(String header,
                          Function<RecordEChemPortal, String> valueFn,
                          boolean wrap,
                          boolean hyperlink) {
            this.header = header;
            this.valueFn = valueFn;
            this.wrap = wrap;
            this.hyperlink = hyperlink;
        }
        public static ColumnSpec of(String header, Function<RecordEChemPortal, String> valueFn) {
            return new ColumnSpec(header, valueFn, false, false);
        }
        public ColumnSpec asWrapped()   { return new ColumnSpec(header, valueFn, true,  hyperlink); }
        public ColumnSpec asHyperlink() { return new ColumnSpec(header, valueFn, wrap, true); }
    }

    public static List<ColumnSpec> defaultColumns() {
        List<ColumnSpec> cols = new ArrayList<>();
        cols.add(ColumnSpec.of("Substance Name", r -> r.substanceName));
        cols.add(ColumnSpec.of("Name type", r -> r.nameType));
        cols.add(ColumnSpec.of("Number", r -> r.number));
        cols.add(ColumnSpec.of("Number type", r -> r.numberType));
        cols.add(ColumnSpec.of("Member of category", r -> r.memberOfCategory == null ? null : String.valueOf(r.memberOfCategory)));
        cols.add(ColumnSpec.of("Section", r -> r.section));
        cols.add(new ColumnSpec("URL", r -> r.url, false, true));
        cols.add(ColumnSpec.of("Source", r -> r.source));
        cols.add(ColumnSpec.of("Reliability", r -> r.reliability));
        cols.add(ColumnSpec.of("Method", r -> r.method));
        cols.add(ColumnSpec.of("Type of information", r -> r.typeOfInformation));
        cols.add(ColumnSpec.of("Endpoint", r -> r.endpoint));
        cols.add(ColumnSpec.of("Test guideline qualifier", r -> r.testGuidelineQualifier));
        cols.add(ColumnSpec.of("Test guideline", r -> r.testGuideline));
        cols.add(ColumnSpec.of("GLP compliance", r -> r.GLP_compliance));
        cols.add(ColumnSpec.of("Oxygen conditions", r -> r.oxygenConditions));
        cols.add(ColumnSpec.of("Pressure",     r -> joinLines(r.pressure)).asWrapped());
        cols.add(ColumnSpec.of("Temperature",  r -> joinLines(r.temperature)).asWrapped());
        cols.add(ColumnSpec.of("pH",           r -> joinLines(r.pH)).asWrapped());
        cols.add(ColumnSpec.of("Degradation records", RecordEChemPortal::convertRecordsDegradationToString).asWrapped());
        // cols.add(ColumnSpec.of("Interpretation of results", r -> r.interpretationOfResults));
        cols.add(ColumnSpec.of("Binary score (degradation records)", r -> r.derivedbinaryBiodegradation+""));
        cols.add(ColumnSpec.of("Values", r -> joinLines(r.values)).asWrapped());
        cols.add(ColumnSpec.of("% Degradation at 28 days", r -> r.percentDegradation28days+""));

        return cols;
    }
    
    public static void addFlattenedRecordsSheet(Workbook wb, Map<String, Integer> htScoresByCAS) {
        Sheet sheet = wb.createSheet("Flattened records");

        // Header style (bold, light gray fill)
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        CellStyle headerStyle = wb.createCellStyle();
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Header row
        Row header = sheet.createRow(0);
        Cell h0 = header.createCell(0);
        h0.setCellValue("CAS Number");
        h0.setCellStyle(headerStyle);

        Cell h1 = header.createCell(1);
        h1.setCellValue("Flattened Score");
        h1.setCellStyle(headerStyle);

        // Freeze header
        sheet.createFreezePane(0, 1);

        // Write rows, sorted by CAS for deterministic output
        int rowIdx = 1;
        if (htScoresByCAS != null && !htScoresByCAS.isEmpty()) {
            List<Map.Entry<String, Integer>> entries = new ArrayList<>(htScoresByCAS.entrySet());
            entries.sort(Comparator.comparing(Map.Entry::getKey, Comparator.nullsLast(String::compareTo)));

            for (Map.Entry<String, Integer> e : entries) {
                Row row = sheet.createRow(rowIdx++);
                // CAS as text
                if (e.getKey() != null && !e.getKey().isBlank()) {
                    row.createCell(0).setCellValue(e.getKey());
                }
                // Score as number (leave blank if null)
                if (e.getValue() != null) {
                    row.createCell(1).setCellValue(e.getValue());
                }
            }
        }

        // Enable AutoFilter over header + data area
        int lastDataRow = Math.max(0, rowIdx - 1);
        sheet.setAutoFilter(new CellRangeAddress(0, lastDataRow, 0, 1));

        // Auto-size and clamp width to 50 characters
        final int MAX_COL_WIDTH = 50 * 256; // POI units (1/256 char)
        for (int c = 0; c <= 1; c++) {
            sheet.autoSizeColumn(c);
            int w = sheet.getColumnWidth(c);
            if (w > MAX_COL_WIDTH) {
                sheet.setColumnWidth(c, MAX_COL_WIDTH);
            }
        }
    }

    public static void writeExcel(List<RecordEChemPortal> records,
                                  Path outputXlsx,
                                  List<ColumnSpec> columns, 
                                  Hashtable<String, Integer> htScoresByCAS) throws IOException {
    	
        try (Workbook wb = new XSSFWorkbook()) {
        	
        	
        	addFlattenedRecordsSheet(wb, htScoresByCAS);
        	
            CreationHelper createHelper = wb.getCreationHelper();
            Sheet sheet = wb.createSheet("EChemPortal");

            // Styles
            Font headerFont = wb.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = wb.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            CellStyle wrapStyle = wb.createCellStyle();
            wrapStyle.setWrapText(true);
            wrapStyle.setVerticalAlignment(VerticalAlignment.TOP);

            CellStyle hyperlinkStyle = wb.createCellStyle();
            Font linkFont = wb.createFont();
            linkFont.setUnderline(Font.U_SINGLE);
            linkFont.setColor(IndexedColors.BLUE.getIndex());
            hyperlinkStyle.setFont(linkFont);

            // Layout rows
            Row subtotalRow = sheet.createRow(0); // dynamic SUBTOTALs
            sheet.createRow(1);                   // blank spacer
            Row headerRow = sheet.createRow(2);   // headers
            final int headerRowIdx = 2;

            // Header cells
            for (int c = 0; c < columns.size(); c++) {
                Cell cell = headerRow.createCell(c);
                cell.setCellValue(columns.get(c).header);
                cell.setCellStyle(headerStyle);
            }

            // Freeze panes
            sheet.createFreezePane(0, headerRowIdx + 1); // freeze rows 1..3

            // Data rows start at POI row 3 (Excel row 4)
            int rowIdx = headerRowIdx + 1;
            for (RecordEChemPortal rec : records) {
                Row row = sheet.createRow(rowIdx++);
                for (int c = 0; c < columns.size(); c++) {
                    ColumnSpec spec = columns.get(c);
                    String value = trimToNull(spec.valueFn.apply(rec)); // null if blank
                    if (value == null) {
                        // Leave cell truly empty so COUNTA + filters behave correctly
                        continue;
                    }
                    Cell cell = row.createCell(c);
                    cell.setCellValue(value);

                    if (spec.wrap) {
                        cell.setCellStyle(wrapStyle);
                    }
                    if (spec.hyperlink) {
                        Hyperlink link = createHelper.createHyperlink(HyperlinkType.URL);
                        link.setAddress(value);
                        cell.setHyperlink(link);
                        cell.setCellStyle(hyperlinkStyle);
                    }
                }
            }

            // SUBTOTAL(103, colRange) → dynamic non-blank counts (ignores filtered rows)
            int dataStartExcelRow = headerRowIdx + 2;          // POI 3 -> Excel 4
            int lastDataPoiRow    = Math.max(headerRowIdx + 1, rowIdx - 1); // at least header+1
            int lastDataExcelRow  = lastDataPoiRow + 1;
            for (int c = 0; c < columns.size(); c++) {
                Cell cell = subtotalRow.createCell(c);
                if (records.isEmpty()) {
                    cell.setCellValue(0);
                } else {
                    String colLetter = CellReference.convertNumToColString(c);
                    String range = String.format("%s%d:%s%d", colLetter, dataStartExcelRow, colLetter, lastDataExcelRow);
                    cell.setCellFormula("SUBTOTAL(103," + range + ")");
                }
            }

            // Enable AutoFilter on the header row and data area
            // Range: header row to last data row, first to last column
            CellRangeAddress filterRange = new CellRangeAddress(headerRowIdx, lastDataPoiRow, 0, columns.size() - 1);
            sheet.setAutoFilter(filterRange);

            // Auto-size and clamp width to 50 characters
            final int MAX_COL_WIDTH = 50 * 256;
            for (int c = 0; c < columns.size(); c++) {
                sheet.autoSizeColumn(c);
                int w = sheet.getColumnWidth(c);
                if (w > MAX_COL_WIDTH) {
                    sheet.setColumnWidth(c, MAX_COL_WIDTH);
                }
            }

            try (OutputStream os = Files.newOutputStream(outputXlsx)) {
                wb.write(os);
            }
        }
    }

    // Helpers
    private static String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
    private static String joinLines(Collection<String> values) {
        if (values == null || values.isEmpty()) return null;
        StringBuilder sb = new StringBuilder();
        for (String v : values) {
            if (v == null) continue;
            String t = v.trim();
            if (t.isEmpty()) continue;
            if (sb.length() > 0) sb.append('\n');
            sb.append(t);
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}