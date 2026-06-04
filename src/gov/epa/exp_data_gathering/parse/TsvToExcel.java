package gov.epa.exp_data_gathering.parse;

/**
* @author TMARTI02
*/


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.charset.Charset;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TsvToExcel {

    private static final int EXCEL_MAX_CHARS = 32767;
    private static final String TRUNC_SUFFIX = "… [TRUNCATED]";
    private static final int MAX_COL_WIDTH_CHARS = 50;

    // Numeric pattern: integers/decimals with optional sign and exponent.
    private static final Pattern NUMERIC_PATTERN =
            Pattern.compile("^[+-]?(?:\\d+(?:\\.\\d*)?|\\.\\d+)(?:[eE][+-]?\\d+)?$");

    /**
     * Convenience overload: detectNumerics defaults to true.
     */
    public static void tsvToExcel(String tsvPath, String xlsxPath, boolean stopAtFirstBlankRow) {
        tsvToExcel(tsvPath, xlsxPath, stopAtFirstBlankRow, true);
    }

    /**
     * Converts a TSV to Excel:
     * - Row 1: SUBTOTAL(3, ...) per column (COUNTA of non-blank cells; respects filters)
     * - Row 2: blank
     * - Row 3: sanitized header (filters applied)
     * - Row 4+: data
     *
     * If detectNumerics is false, all non-empty fields are written as strings.
     * Adds:
     * - Column A: auto-numbered row index (1..N for data rows)
     * - Freeze panes so the header row stays visible when scrolling
     */
    public static void tsvToExcel(String tsvPath, String xlsxPath, boolean stopAtFirstBlankRow, boolean detectNumerics) {
        try {
            Path inPath = Path.of(tsvPath);
            Path outPath = Path.of(xlsxPath);

            ReadResult read = tryReadTsvWithBOMAndFallbacks(inPath, stopAtFirstBlankRow);
            if (read == null) {
                System.err.println("Failed to convert TSV to Excel: could not decode input file " + tsvPath);
                return;
            }

            boolean[] numericCol = detectNumerics
                    ? inferNumericColumns(read.rows, read.maxCols)
                    : new boolean[read.maxCols]; // all false => write as strings

            try (XSSFWorkbook wb = new XSSFWorkbook()) {
                Sheet sheet = wb.createSheet("Sheet1");

                // Layout indices (0-based)
                final int subtotalRowIndex = 0;            // Row 1 in Excel
                final int blankRowIndex = 1;               // Row 2 in Excel
                final int headerRowIndex = 2;              // Row 3 in Excel
                final int firstDataRowIndex = headerRowIndex + 1; // Row 4 in Excel

                // Ensure the blank row exists
                sheet.createRow(blankRowIndex);

                // Header row (sanitized, length-limited)
                Row headerRow = sheet.createRow(headerRowIndex);

                // New first column header: auto-numbered "Row"
                Cell rowNumHeader = headerRow.createCell(0, CellType.STRING);
                rowNumHeader.setCellValue("Row");

                // Existing headers shifted by +1 column
                String[] rawHeader = read.rows.get(0);
                for (int c = 0; c < read.maxCols; c++) {
                    String original = (c < rawHeader.length) ? rawHeader[c] : null;
                    String sanitized = sanitizeHeaderLikeExcel(original, c);
                    Cell cell = headerRow.createCell(c + 1, CellType.STRING);
                    setCellStringLimited(cell, sanitized, headerRowIndex, c + 1, tsvPath);
                }

                // Data rows (leave cells truly blank for empty fields so SUBTOTAL(3, ...) works)
                for (int r = 1; r < read.rows.size(); r++) {
                    Row row = sheet.createRow(headerRowIndex + r);

                    // Auto-number column (first column): 1..N for data rows
                    row.createCell(0, CellType.NUMERIC).setCellValue(r);

                    String[] data = read.rows.get(r);
                    for (int c = 0; c < read.maxCols; c++) {
                        String original = data[c] == null ? "" : data[c];
                        String trimmed = original.trim();

                        // Keep cell blank if empty
                        if (trimmed.isEmpty()) {
                            continue;
                        }

                        int xlsxCol = c + 1; // shift by one to account for the auto-number column

                        if (detectNumerics && numericCol[c] && isNumeric(trimmed)) {
                            try {
                                double d = Double.parseDouble(trimmed);
                                if (Double.isFinite(d)) {
                                    Cell cell = row.createCell(xlsxCol, CellType.NUMERIC);
                                    cell.setCellValue(d);
                                } else {
                                    Cell cell = row.createCell(xlsxCol, CellType.STRING);
                                    setCellStringLimited(cell, original, headerRowIndex + r, xlsxCol, tsvPath);
                                }
                            } catch (NumberFormatException nfe) {
                                Cell cell = row.createCell(xlsxCol, CellType.STRING);
                                setCellStringLimited(cell, original, headerRowIndex + r, xlsxCol, tsvPath);
                            }
                        } else {
                            // Either detectNumerics is false or this column/value is not numeric -> store as string
                            Cell cell = row.createCell(xlsxCol, CellType.STRING);
                            setCellStringLimited(cell, original, headerRowIndex + r, xlsxCol, tsvPath);
                        }
                    }
                }

                // Subtotal row with SUBTOTAL(3, …) (COUNTA) per column, including the new auto-number column
                Row subtotalRow = sheet.createRow(subtotalRowIndex);
                int dataRowCount = Math.max(0, read.rows.size() - 1);
                int lastDataRowIndex = firstDataRowIndex + dataRowCount - 1; // inclusive (0-based)
                int totalCols = read.maxCols + 1; // +1 for the auto-number column

                for (int c = 0; c < totalCols; c++) {
                    if (dataRowCount > 0) {
                        String colLetter = CellReference.convertNumToColString(c);
                        int excelStart = firstDataRowIndex + 1; // Excel is 1-based
                        int excelEnd = lastDataRowIndex + 1;
                        String formula = "SUBTOTAL(3," + colLetter + excelStart + ":" + colLetter + excelEnd + ")";
                        subtotalRow.createCell(c, CellType.FORMULA).setCellFormula(formula);
                    } else {
                        subtotalRow.createCell(c, CellType.NUMERIC).setCellValue(0);
                    }
                }

                // AutoFilter on the header row covering all data (includes new first column)
                if (totalCols > 0) {
                    int rightCol = totalCols - 1;
                    int bottomRow = Math.max(headerRowIndex, lastDataRowIndex);
                    sheet.setAutoFilter(new CellRangeAddress(headerRowIndex, bottomRow, 0, rightCol));
                }

                // Freeze panes so the header row stays visible when scrolling
                // Note: With this layout, rows 1-3 (subtotal, blank, header) are frozen.
                sheet.createFreezePane(0, headerRowIndex + 1);

                // Auto-size columns then cap width to 50 characters
                int maxWidth = MAX_COL_WIDTH_CHARS * 256; // Excel width units
                for (int c = 0; c < totalCols; c++) {
                    sheet.autoSizeColumn(c);
                    int current = sheet.getColumnWidth(c);
                    if (current > maxWidth) {
                        sheet.setColumnWidth(c, maxWidth);
                    }
                }

                try (OutputStream out = Files.newOutputStream(outPath)) {
                    wb.write(out);
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to convert TSV to Excel (" + tsvPath + " -> " + xlsxPath + "): "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }
    // Sanitize headers like your Excel logic:
    // - "#" -> "RecordNumber"
    // - if first char is digit, prefix with word ("Zero".."Nine")
    // - replace non-alphanumeric runs with "_"
    // - trim leading/trailing "_"
    // - fallback to "field{index}" if empty
    private static String sanitizeHeaderLikeExcel(String header, int index) {
        String v = header == null ? "" : header;

        if ("#".equals(v)) {
            v = "Chemical_Number";
        }

        if (!v.isEmpty()) {
            char first = v.charAt(0);
            String prefix = switch (first) {
                case '0' -> "Zero";
                case '1' -> "One";
                case '2' -> "Two";
                case '3' -> "Three";
                case '4' -> "Four";
                case '5' -> "Five";
                case '6' -> "Six";
                case '7' -> "Seven";
                case '8' -> "Eight";
                case '9' -> "Nine";
                default -> null;
            };
            if (prefix != null) {
                v = prefix + v.substring(1);
            }
        }

        v = v.trim().replaceAll("[^\\p{Alnum}]+", "_");
        v = v.replaceAll("^_+", "").replaceAll("_+$", "");

        if (v.isEmpty() || "_".equals(v)) {
            v = "field" + index;
        }
        return v;
    }

    // Truncate to Excel's 32,767-char limit and warn.
    private static void setCellStringLimited(Cell cell, String value, int rowIndex, int colIndex, String sourcePath) {
        String v = value == null ? "" : value;
        if (v.length() <= EXCEL_MAX_CHARS) {
            cell.setCellValue(v);
            return;
        }
        int suffixLen = TRUNC_SUFFIX.length();
        int maxBody = Math.max(0, EXCEL_MAX_CHARS - suffixLen);
        String truncated = v.substring(0, maxBody) + TRUNC_SUFFIX;
        cell.setCellValue(truncated);
        System.err.println("Warning: Truncated oversized cell value at row " + (rowIndex + 1)
                + ", col " + (colIndex + 1) + " from file '" + sourcePath
                + "' to " + EXCEL_MAX_CHARS + " characters.");
    }

    // --- Reading and charset handling ---

    private static ReadResult tryReadTsvWithBOMAndFallbacks(Path inPath, boolean stopAtFirstBlankRow) {
        // 1) Try BOM-aware reader
        try (BufferedReader br = newBomAwareReader(inPath)) {
            return readRows(br, stopAtFirstBlankRow);
        } catch (CharacterCodingException e) {
            // Charset mismatch: try fallbacks
        } catch (IOException e) {
            System.err.println("I/O error while opening reader: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error while reading with BOM-aware reader: " + e.getMessage());
            return null;
        }

        // 2) Fallback charsets
        Charset[] fallbacks = new Charset[] {
                StandardCharsets.UTF_16LE,
                StandardCharsets.UTF_16BE,
                StandardCharsets.ISO_8859_1,
                Charset.forName("windows-1252")
        };

        for (Charset cs : fallbacks) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(Files.newInputStream(inPath), cs))) {
                ReadResult res = readRows(br, stopAtFirstBlankRow);
                System.err.println("Notice: Decoded " + inPath + " using fallback charset " + cs.name());
                return res;
            } catch (CharacterCodingException e) {
                // try next charset
            } catch (IOException e) {
                System.err.println("I/O error while trying charset " + cs.name() + ": " + e.getMessage());
                return null;
            } catch (Exception e) {
                System.err.println("Unexpected error with charset " + cs.name() + ": " + e.getMessage());
                return null;
            }
        }
        return null;
    }

    private static BufferedReader newBomAwareReader(Path p) throws IOException {
        InputStream is = Files.newInputStream(p);
        PushbackInputStream pb = new PushbackInputStream(is, 4);
        byte[] bom = new byte[4];
        int n = pb.read(bom, 0, bom.length);

        int unread = n;
        Charset cs = StandardCharsets.UTF_8; // default if no BOM
        int skip = 0;

        if (n >= 3 && (bom[0] & 0xFF) == 0xEF && (bom[1] & 0xFF) == 0xBB && (bom[2] & 0xFF) == 0xBF) {
            cs = StandardCharsets.UTF_8; skip = 3;
        } else if (n >= 2 && (bom[0] & 0xFF) == 0xFE && (bom[1] & 0xFF) == 0xFF) {
            cs = StandardCharsets.UTF_16BE; skip = 2;
        } else if (n >= 4 && (bom[0] & 0xFF) == 0x00 && (bom[1] & 0xFF) == 0x00
                && (bom[2] & 0xFF) == 0xFE && (bom[3] & 0xFF) == 0xFF) {
            cs = Charset.forName("UTF-32BE"); skip = 4;
        } else if (n >= 4 && (bom[0] & 0xFF) == 0xFF && (bom[1] & 0xFF) == 0xFE
                && (bom[2] & 0xFF) == 0x00 && (bom[3] & 0xFF) == 0x00) {
            cs = Charset.forName("UTF-32LE"); skip = 4;
        } else if (n >= 2 && (bom[0] & 0xFF) == 0xFF && (bom[1] & 0xFF) == 0xFE) {
            cs = StandardCharsets.UTF_16LE; skip = 2;
        } else {
            skip = 0; // no BOM
        }

        if (unread > skip) {
            pb.unread(bom, skip, unread - skip);
        }
        return new BufferedReader(new InputStreamReader(pb, cs));
    }

    private static ReadResult readRows(BufferedReader br, boolean stopAtFirstBlankRow) throws IOException {
        List<String[]> rows = new ArrayList<>();
        int maxCols = 0;

        String line = br.readLine();
        if (line == null) {
            rows.add(new String[0]); // empty sheet with no headers
            return new ReadResult(rows, 0);
        }

        // Header
        String[] header = splitTsvLine(line);
        if (header.length > 0 && header[0] != null) {
            header[0] = stripBom(header[0]); // in case a BOM slipped through
        }
        rows.add(header);
        maxCols = Math.max(maxCols, header.length);

        // Data rows
        while ((line = br.readLine()) != null) {
            String[] fields = splitTsvLine(line);
            if (stopAtFirstBlankRow && isBlankRow(fields)) {
                break;
            }
            rows.add(fields);
            if (fields.length > maxCols) {
                maxCols = fields.length;
            }
        }

        // Normalize to same number of columns (we'll still skip creating cells for empty data fields)
        for (int i = 0; i < rows.size(); i++) {
            String[] r = rows.get(i);
            if (r.length < maxCols) {
                String[] expanded = Arrays.copyOf(r, maxCols);
                for (int c = r.length; c < maxCols; c++) expanded[c] = "";
                rows.set(i, expanded);
            }
        }
        return new ReadResult(rows, maxCols);
    }

    // --- Helpers ---

    private static boolean[] inferNumericColumns(List<String[]> rows, int maxCols) {
        boolean[] numericCol = new boolean[maxCols];
        boolean[] hasAnyNonEmpty = new boolean[maxCols];
        Arrays.fill(numericCol, true);

        for (int c = 0; c < maxCols; c++) {
            for (int r = 1; r < rows.size(); r++) {
                String raw = rows.get(r)[c];
                String val = raw == null ? "" : raw.trim();
                if (!val.isEmpty()) {
                    hasAnyNonEmpty[c] = true;
                    if (!isNumeric(val)) {
                        numericCol[c] = false;
                        break;
                    }
                }
            }
            if (!hasAnyNonEmpty[c]) {
                numericCol[c] = false; // no data -> keep as string
            }
        }
        return numericCol;
    }

    private static String[] splitTsvLine(String line) {
        // Preserve trailing empty cells
        return line.split("\t", -1);
    }

    private static boolean isBlankRow(String[] fields) {
        if (fields == null || fields.length == 0) return true;
        for (String f : fields) {
            if (f != null && !f.trim().isEmpty()) return false;
        }
        return true;
    }

    private static boolean isNumeric(String s) {
        return NUMERIC_PATTERN.matcher(s).matches();
    }

    private static String stripBom(String s) {
        return (s != null && !s.isEmpty() && s.charAt(0) == '\uFEFF') ? s.substring(1) : s;
    }

    private static class ReadResult {
        final List<String[]> rows;
        final int maxCols;
        ReadResult(List<String[]> rows, int maxCols) {
            this.rows = rows;
            this.maxCols = maxCols;
        }
    }
}