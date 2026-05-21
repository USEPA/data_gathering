package gov.epa.exp_data_gathering.parse.RIFM_2026_01;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import com.google.gson.JsonObject;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.BiodegradationPropertyValues;
import gov.epa.exp_data_gathering.parse.ChemicalNameFixer;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.EstimateParser.Estimate;

public class RecordRIFM_2026_01 {
	public String SMILES;
	public String CAS;
	public String SRC_comments;
	public String MW;
	public String Chemical_name;
	public String Predicted_Log_Kow;
	public String Predicted_Water_Solubility_WSKow_mg_L;
	public String Predicted_Vapor_Pressure_mmHg_at_25_deg_C;
	public String Predicted_HLC_VP_WSOL_Method_atm_m3_mol_at_25_deg_C;
	public String BioWin5_MITI_Linear_Model_Prediction;
	public String BioWin6_MITI_Non_Linear_Model_Prediction;
	public String Test_guideline;
	public String Reviewed_Data_Results;
	public String Duration;
	public String Unit;
	public String Test_organisms_species;
	public String Year;
	public String Reference_source;
	public String Reference;
	public static final String[] fieldNames = {"SMILES","CAS","SRC_comments","MW","Chemical_name","Predicted_Log_Kow","Predicted_Water_Solubility_WSKow_mg_L","Predicted_Vapor_Pressure_mmHg_at_25_deg_C","Predicted_HLC_VP_WSOL_Method_atm_m3_mol_at_25_deg_C","BioWin5_MITI_Linear_Model_Prediction","BioWin6_MITI_Non_Linear_Model_Prediction","Test_guideline","Reviewed_Data_Results","Duration","Unit","Test_organisms_species","Year","Reference_source","Reference"};

	public static final String lastUpdated = "2026-03-20";
	public static final String sourceName = "RIFM_2026_01"; // TODO Consider creating ExperimentalConstants.strSourceRIFM_2026_01 instead.


	private static final String fileName = "Biodegradation data_Summary_January 2026.xlsx";

	/**
	 * Output mode for property value transformation: "BINARY" or "CONTINUOUS"
	 * Set by ParseRIFM_2026_01.setOutputMode() during initialization
	 */
	public static String outputMode = ExperimentalConstants.str_binary;

//	private static final transient UnitConverter unitConverter = new UnitConverter("data/density.txt");


	public static Vector<JsonObject> parseRIFM_2026_01RecordsFromExcel() {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		Vector<JsonObject> records = esr.parseRecordsFromExcel(4); // TODO Chemical name index guessed from header. Is this accurate?
		return records;
	}
	
	
	
	public ExperimentalRecord toExperimentalRecord() {
		ExperimentalRecord er=new ExperimentalRecord();
		
		er.source_name=sourceName;
		er.document_name=Reference;

		er.casrn=CAS.replace(".","");
		er.chemical_name=ChemicalNameFixer.fixName(Chemical_name);
		er.smiles=SMILES;
		
//		System.out.println(er.chemical_name+"\t"+er.casrn);
		setParameters(er);

		if(SRC_comments!=null&& !SRC_comments.isBlank()) {
			er.updateNote(SRC_comments);
		}
		
		Estimate estimate = getPropertyValueEstimate();
		Double duration=Double.parseDouble(this.Duration.replace(" days",""));//works without try/catch
		BiodegradationPropertyValues.setPropertyValues(er, outputMode, estimate, duration);
		
		return er;
	}


	private void setParameters(ExperimentalRecord er) {
		
		er.experimental_parameters=new Hashtable<>();
		er.parameter_values=new ArrayList<>();
				
		if (!Test_guideline.contains("301F")) {
			er.keep=false;
			er.reason="Wrong guideline";
			er.experimental_parameters.put("Test guideline", Test_guideline);
		} else {
			//Standardize guideline name:
			er.experimental_parameters.put("Test guideline", "OECD Guideline 301 F (Ready Biodegradability)");
		}
		
		String parameterName="Observation duration";
		ParameterValue pv=new ParameterValue();
		pv.parameter.name=parameterName;

		if(Duration.contains("days")) {
			String strDuration=Duration.replace(" days", "");
			pv.value_point_estimate=Double.parseDouble(strDuration);
			pv.unit.abbreviation="days";
			pv.unit.name = "DAYS";
			er.parameter_values.add(pv);
		} else {
			System.out.println("Different duration units:"+Duration+" for CAS="+CAS);
		}
	}


	private Estimate getPropertyValueEstimate() {
		Estimate estimate=new Estimate();
		
		if (Reviewed_Data_Results.contains("<")) {
			estimate.max=Double.parseDouble(Reviewed_Data_Results.replace("<", "").trim());
		} else if (Reviewed_Data_Results.contains("\u00B1")) {
			String [] vals = Reviewed_Data_Results.split("\u00B1");
			double mean = Double.parseDouble(vals[0].trim());
			double plusminus = Double.parseDouble(vals[1].trim());
			estimate.min = mean - plusminus;
			estimate.max = mean + plusminus;
		} else if (Reviewed_Data_Results.contains("-") && Reviewed_Data_Results.indexOf("-")>0) {
			String [] vals = Reviewed_Data_Results.split("-");
			estimate.min = Double.parseDouble(vals[0]);
			estimate.max = Double.parseDouble(vals[1]);
		} else {
			estimate.point=Double.parseDouble(Reviewed_Data_Results);
		}
		return estimate;
		
	}

	

	
}