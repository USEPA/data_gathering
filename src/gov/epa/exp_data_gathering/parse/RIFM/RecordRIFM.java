package gov.epa.exp_data_gathering.parse.RIFM;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import com.google.gson.JsonObject;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.ghs_data_gathering.GetData.ECHA_IUCLID.IUCLID_Document.Part2.MaterialsAndMethods.Guideline;

public class RecordRIFM {
	public String SMILES;
	public String CAS;
	public String SRC_comments;
	public String Test_guideline;
	public String MW;
	public String Chemical_name;
	public String Predicted_Log_Kow;
	public String Predicted_Water_Solubility_WSKow_mg_L;
	public String Predicted_Vapor_Pressure_mmHg_at_25_deg_C;
	public String Predicted_HLC_VP_WSOL_Method_atm_m3_mol_at_25_deg_C;
	public String BioWin5_MITI_Linear_Model_Prediction;
	public String BioWin6_MITI_Non_Linear_Model_Prediction;
	public String Reviewed_Data;
	public String Duration;
	public String Unit;
	public String Test_organisms_species;
	public String Year;
	public String Reference_source;
	public String Reference;
	public static final String[] fieldNames = {"SMILES","CAS","SRC_comments","Test_guideline","MW","Chemical_name","Predicted_Log_Kow","Predicted_Water_Solubility_WSKow_mg_L","Predicted_Vapor_Pressure_mmHg_at_25_deg_C","Predicted_HLC_VP_WSOL_Method_atm_m3_mol_at_25_deg_C","BioWin5_MITI_Linear_Model_Prediction","BioWin6_MITI_Non_Linear_Model_Prediction","Reviewed_Data","Duration","Unit","Test_organisms_species","Year","Reference_source","Reference"};

	public static final String lastUpdated = "null";
	public static final String sourceName = "RIFM"; // TODO Consider creating ExperimentalConstants.strSourceRIFM instead.

	private static final String fileName = "RIFM biodegradation data for EPA November 2023-tmm.xlsx";

	private static final transient UnitConverter unitConverter = new UnitConverter("data/density.txt");

	public static Vector<JsonObject> parseRIFMRecordsFromExcel() {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		esr.headerRowNum=2;
		
		Vector<JsonObject> records = esr.parseRecordsFromExcel(2); // TODO Chemical name index guessed from header. Is this accurate?
		return records;
	}

	public ExperimentalRecord toExperimentalRecord() {
		ExperimentalRecord er=new ExperimentalRecord();
		
		er.property_name=ExperimentalConstants.strRBIODEG;
		er.source_name=Reference_source;
		er.document_name=Reference;
		er.smiles=SMILES;
		er.casrn=CAS.replace(".","");
		er.chemical_name=Chemical_name;
		
		er.experimental_parameters=new Hashtable<>();
		er.parameter_values=new ArrayList<>();

		if(!SRC_comments.isBlank()) {
			er.updateNote(SRC_comments);
		}
		
		
		if(Reviewed_Data.isBlank()) {
			er.keep=false;
			er.reason="Missing degradation %";
			return er;
		} 

		er.property_value_point_estimate_original=Double.parseDouble(Reviewed_Data);
		er.property_value_units_original="%";
			
		if (Test_guideline.contains("301F")) {
			
			if(er.property_value_point_estimate_original>60) {
				er.property_value_point_estimate_final=1.0;
			} else {
				er.property_value_point_estimate_final=0.0;
			}
			er.property_value_units_final=ExperimentalConstants.str_binary;
			
		} else {
			System.out.println("Different guideline:"+Test_guideline);
			er.keep=false;
			er.reason="Wrong guideline";
		}
		
		er.experimental_parameters.put("Test guideline", Test_guideline);
		
		String parameterName="Observation duration";
		ParameterValue pv=new ParameterValue();
		pv.parameter.name=parameterName;

		if(Duration.contains("days")) {
			String strDuration=Duration.replace(" days", "");
			pv.value_point_estimate=Double.parseDouble(strDuration);
			pv.unit.abbreviation="days";
			er.parameter_values.add(pv);
		} else {
			System.out.println("Different duration units:"+Duration);
		}
		
		
		return er;
	}

}