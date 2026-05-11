package gov.epa.exp_data_gathering.parse.Kodithala;


import java.util.Vector;

import com.google.gson.JsonObject;

import gov.epa.exp_data_gathering.parse.ExcelSourceReader;

public class RecordKodithala {
	public String Compound_name;
	public String Type_of_alcohol;
	public String Observed_PII;
	public static final String[] fieldNames = {"Compound_name","Type_of_alcohol","Observed_PII"};

	public static final String lastUpdated = "04/19/2021";
	public static final String sourceName = "Kodithala"; // TODO Consider creating ExperimentalConstants.strSourceKodithala instead.

	private static final String fileName = "Kodithala.xlsx";

	public static Vector<JsonObject> parseKodithalaRecordsFromExcel() {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		Vector<JsonObject> records = esr.parseRecordsFromExcel(0); // TODO Chemical name index guessed from header. Is this accurate?
		return records;
	}
}
