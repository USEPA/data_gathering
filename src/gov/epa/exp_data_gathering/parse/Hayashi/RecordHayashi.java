package gov.epa.exp_data_gathering.parse.Hayashi;

import java.util.Vector;

import com.google.gson.JsonObject;

import gov.epa.exp_data_gathering.parse.ExcelSourceReader;


public class RecordHayashi {
	public String Chemical;
	public String Experimental_skin_irritation_score;
	public String MW;
	public static final String[] fieldNames = {"Chemical","Experimental_skin_irritation_score","MW"};

	public static final String lastUpdated = "04/19/2021";
	public static final String sourceName = "Hayashi"; // TODO Consider creating ExperimentalConstants.strSourceHayashi instead.

	private static final String fileName = "Hayashi.xlsx";

	public static Vector<JsonObject> parseHayashiRecordsFromExcel() {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		Vector<JsonObject> records = esr.parseRecordsFromExcel(0); // TODO Chemical name index guessed from header. Is this accurate?
		return records;
	}
}