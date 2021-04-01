package gov.epa.exp_data_gathering.parse;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import com.google.gson.JsonObject;

public class ExcelSourceReader {
	public String sourceName;
	public String lastUpdated;

	private String sourceFolderPath;
	private String fileName;
	private Sheet sheet;
	
	public ExcelSourceReader(String fileName, String sourceName) {
		this.sourceName = sourceName;
		this.fileName = fileName;
		sourceFolderPath = "data" + File.separator + "experimental" + File.separator + sourceName;
		
		String filePath = sourceFolderPath + File.separator + "excel files" + File.separator + fileName;
		this.lastUpdated = DownloadWebpageUtilities.getStringCreationDate(filePath); // TODO add lastUpdated as parameter instead?
		try {
			FileInputStream fis = new FileInputStream(new File(filePath));
			Workbook wb = WorkbookFactory.create(fis);
			sheet = wb.getSheetAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Writes records from a spreadsheet to JSON original records format consistent with field names of an existing Record[SourceName] class
	 * @param hmFieldNames	Matches column numbers to output fields of a Record[SourceName] class
	 * @param chemicalNameIndex		Column index containing chemical names (for special escape character treatment)
	 */
	public Vector<JsonObject> parseRecordsFromExcel(HashMap<Integer,String> hmFieldNames, int chemicalNameIndex) {
		Vector<JsonObject> records = new Vector<JsonObject>();
		try {
			int numRows = sheet.getLastRowNum();
			for (int i = 1; i < numRows; i++) {
				Row row = sheet.getRow(i);
				if (row==null) { continue; }
				JsonObject jo = new JsonObject();
				boolean hasAnyFields = false;
				for (int k:hmFieldNames.keySet()) {
					Cell cell = row.getCell(k);
					if (cell==null) { continue; }
					cell.setCellType(CELL_TYPE_STRING);
					String content = "";
					if (k==chemicalNameIndex) {
						content = StringEscapeUtils.escapeHtml4(row.getCell(k,MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue());
					} else {
						content = row.getCell(k,MissingCellPolicy.CREATE_NULL_AS_BLANK).getStringCellValue();
					}
					if (content!=null && !content.isBlank()) { hasAnyFields = true; }
					jo.addProperty(hmFieldNames.get(k), content);
				}
				if (hasAnyFields) { records.add(jo); }
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return records;
	}
	
	/**
	 * Writes records from a spreadsheet to JSON original records format assuming the generated Record[SourceName] template
	 * @param chemicalNameIndex		Column index containing chemical names (for special escape character treatment)
	 */
	public Vector<JsonObject> parseRecordsFromExcel(int chemicalNameIndex) {
		String[] fieldNames = getHeaders();
		HashMap<Integer,String> hm = generateDefaultMap(fieldNames, 0);
		return parseRecordsFromExcel(hm, chemicalNameIndex);
	}
	
	public HashMap<Integer,String> generateDefaultMap(String[] fieldNames, int offset) {
		HashMap<Integer,String> hmFieldNames = new HashMap<Integer,String>();
		for (int i = 0; i < fieldNames.length; i++) {
			hmFieldNames.put(i + offset, fieldNames[i]);
		}
		return hmFieldNames;
	}
	
	private String[] getHeaders() {
		Row headerRow = sheet.getRow(0);
		int numHeaders = headerRow.getLastCellNum();
		String[] headers = new String[numHeaders];
		for (int i = 0; i < numHeaders; i++) {
			Cell headerCell = headerRow.getCell(i, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			headerCell.setCellType(CELL_TYPE_STRING);
			String headerContent = headerCell.getStringCellValue().trim().replaceAll("[^\\p{Alnum}]+", "_");
			if (headerContent.equals("_")) {
				headers[i] = "blank" + i;
			} else {
				headers[i] = headerContent;
			}
		}
		return headers;
	}
	
	private void generateClassTemplate() {
		String[] fieldNames = getHeaders();
		int chemicalNameIndex = -1;
		for (int i = 0; i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			if (fieldName.toLowerCase().contains("name") || fieldName.toLowerCase().contains("chemical") || fieldName.toLowerCase().contains("material")) {
				chemicalNameIndex = i;
				break;
			}
		}
		StringBuilder sb = new StringBuilder("public class Record" + sourceName + " {\n");
		String fieldNamesString = "{";
		for (String fieldName:fieldNames) {
			sb.append("\tpublic String " + fieldName + ";\n");
			fieldNamesString += "\"" + fieldName + "\",";
		}
		fieldNamesString = fieldNamesString.substring(0, fieldNamesString.length() - 1) + "}"; // Trim trailing comma
		sb.append("\tpublic static final String[] fieldNames = " + fieldNamesString + ";\n\n");
		sb.append("\tpublic static final String lastUpdated = \"" + lastUpdated +"\";\n");
		sb.append("\tpublic static final String sourceName = \"" + sourceName +"\";");
		sb.append(" // TODO Consider creating ExperimentalConstants.strSource" + sourceName + " instead.\n\n");
		sb.append("\tprivate static final String fileName = \"" + fileName +"\";\n\n");
		sb.append("\tpublic static Vector<JsonObject> parse" + sourceName + "RecordsFromExcel() {\n");
		sb.append("\t\tExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);\n");
		sb.append("\t\tVector<JsonObject> records = esr.parseRecordsFromExcel(" + chemicalNameIndex + ");");
		sb.append(" // TODO Chemical name index guessed from header. Is this accurate?\n");
		sb.append("\t\treturn records;\n");
		sb.append("\t}\n}");
		
		String templateFilePath = sourceFolderPath + File.separator + "Record" + sourceName + "_ClassTemplate.txt";
		File file = new File(templateFilePath);
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Wrote Record" + sourceName + " template to " + templateFilePath);
	}

	public static void main(String[] args) {
		ExcelSourceReader esr = new ExcelSourceReader("Aqueous Solubility Nature Scientific Data with DTXSIDs.xlsx","AqSolDB2");
		esr.generateClassTemplate();
	}

}