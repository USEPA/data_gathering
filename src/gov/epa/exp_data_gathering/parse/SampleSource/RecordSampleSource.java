package gov.epa.exp_data_gathering.parse.SampleSource;

import java.util.Vector;

import com.google.gson.JsonObject;

import gov.epa.exp_data_gathering.parse.ExcelSourceReader;


public class RecordSampleSource {
	public String Name;
	public String reason_not_extracted;
	public String test_substance_name;
	public String other_test_substance_name;
	public String CASRN;
	public String property;
	public String property_value;
	public String property_value_min;
	public String property_value_max;
	public String property_value_units;
	public String property_measurement_conditions;
	public String comments;
	public String property_value_method;
	public String CR_Notes;
	public String Keep;
	public String NAME_FINAL;
	public static final String[] fieldNames = {"Name","reason_not_extracted","test_substance_name","other_test_substance_name","CASRN","property","property_value","property_value_min","property_value_max","property_value_units","property_measurement_conditions","comments","property_value_method","CR_Notes","Keep","NAME_FINAL"};

	public static final String lastUpdated = "01/24/2024";
	public static final String sourceName = "SampleSource"; // TODO Consider creating ExperimentalConstants.strSourceSampleSource instead.

	private static final String fileName = "sample data.xlsx";

	public static Vector<JsonObject> parseSampleSourceRecordsFromExcel() {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		Vector<JsonObject> records = esr.parseRecordsFromExcel(0); // TODO Chemical name index guessed from header. Is this accurate?
		return records;
	}
}