package gov.epa.exp_data_gathering.parse.RIFM_2026_08_12;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import com.google.gson.JsonObject;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.BiodegradationPropertyValues;
import gov.epa.exp_data_gathering.parse.ChemicalNameFixer;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.exp_data_gathering.parse.EstimateParser.Estimate;

public class RecordRIFM_2026_08_12 {
	public String RIFM;
	public String Test_substance_CASRN;
	public String Commercial_Name;
	public String Chemical_Nature_Name;
	public String Other_Name;
	public String Is_test_substance_a_mixture;
	public String Test_guideline;
	public String Test_date;
	public String Test_lab_name;
	public String GLP_compliance;
	public String Test_appropriate_for_chemical;
	public String Test_chemical_concentration;
	public String Test_chemical_conc_meets_guideline;
	public String Inoculum_concentration;
	public String Inoculum_conc_meets_guideline;
	public String Inoculum_source_s;
	public String Inoculum_source_s_meets_guideline;
	public String Used_toxicity_control;
	public String Toxicity_control_deg;
	public String Toxicity_control_deg_meets_guideline;
	public String pH;
	public String pH_meets_guideline;
	public String Blank_mg_O2_L_at_28d;
	public String Blank_meets_guideline_limit;
	public String Pos_control_deg;
	public String Pos_control_meets_deg_guideline;
	public String Replicate_variability;
	public String Rep_var_meets_guideline;
	public String Test_chemical_deg_at_28d_by_BOD_or_ThOD;
	public String Method_used_to_measure_the_respective_deg_at_28d;
	public String Addl_test_chemical_deg_at_28d_by_other_method_s;
	public String Addl_method_used_to_measure_the_respective_deg_at_28d;
	public String Are_biodeg_results_mixed_difficult_to_interpret;
	public String Meets_deg_pass_level;
	public String Pass_w_in_10d_window;
	public String Readily_biodegradable;
	public String Additional_data_included;
	public String Describe_additional_data;
	public String Primary_quality_score_auto;
	public String Secondary_quality_score_auto;
	public String Tertiary_quality_score_auto;
	public String Overall_quality_score_auto;
	public String Overall_quality_acceptable_auto;
	public String Reported_purity;
	public String Stereoisomers_present;
	public String Structural_isomers_present;
	public String Purity_specific_comments;
	public String Additional_QC_comments;
	public String EPA_reviewer;
	public String EPA_review_date;
	public String EPA_reviewer_self_QC_complete;
	public String EPA_QCer;
	public String QC_Date;
	public String EPA_QCer_comments;
	public String File_name;
	public String Date_finalized_by_initial_EPA_reviewer;
	public String RIFM_Batch_Number;
	public static final String[] fieldNames = {"RIFM","Test_substance_CASRN","Commercial_Name","Chemical_Nature_Name","Other_Name","Is_test_substance_a_mixture","Test_guideline","Test_date","Test_lab_name","GLP_compliance","Test_appropriate_for_chemical","Test_chemical_concentration","Test_chemical_conc_meets_guideline","Inoculum_concentration","Inoculum_conc_meets_guideline","Inoculum_source_s","Inoculum_source_s_meets_guideline","Used_toxicity_control","Toxicity_control_deg","Toxicity_control_deg_meets_guideline","pH","pH_meets_guideline","Blank_mg_O2_L_at_28d","Blank_meets_guideline_limit","Pos_control_deg","Pos_control_meets_deg_guideline","Replicate_variability","Rep_var_meets_guideline","Test_chemical_deg_at_28d_by_BOD_or_ThOD","Method_used_to_measure_the_respective_deg_at_28d","Addl_test_chemical_deg_at_28d_by_other_method_s","Addl_method_used_to_measure_the_respective_deg_at_28d","Are_biodeg_results_mixed_difficult_to_interpret","Meets_deg_pass_level","Pass_w_in_10d_window","Readily_biodegradable","Additional_data_included","Describe_additional_data","Primary_quality_score_auto","Secondary_quality_score_auto","Tertiary_quality_score_auto","Overall_quality_score_auto","Overall_quality_acceptable_auto","Reported_purity","Stereoisomers_present","Structural_isomers_present","Purity_specific_comments","Additional_QC_comments","EPA_reviewer","EPA_review_date","EPA_reviewer_self_QC_complete","EPA_QCer","QC_Date","EPA_QCer_comments","File_name","Date_finalized_by_initial_EPA_reviewer","RIFM_Batch_Number"};

	public static final String lastUpdated = "2026_08_12";
	public static final String sourceName = "RIFM_2026_08_12"; // TODO Consider creating ExperimentalConstants.strSourceRIFM_2026_08_12 instead.

	private static final String fileName = "RIFM_EPAdataset4Toddmodel_08122026.xlsx";

	private static final transient UnitConverter unitConverter = new UnitConverter("data/density.txt");

	public static Vector<JsonObject> parseRIFM_2026_08_12RecordsFromExcel() {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		esr.headerRowNum=2;
		esr.lastUpdated=lastUpdated;
		Vector<JsonObject> records = esr.parseRecordsFromExcel(3); // TODO Chemical name index guessed from header. Is this accurate?
		return records;
	}

	public ExperimentalRecord toExperimentalRecord() {
		ExperimentalRecord er=new ExperimentalRecord();
		
		er.source_name=sourceName;
//		er.document_name=Reference;

		er.casrn=this.Test_substance_CASRN;
		er.chemical_name=ChemicalNameFixer.fixName(Chemical_Nature_Name);
//		er.smiles=SMILES;
		
//		System.out.println(er.chemical_name+"\t"+er.casrn);
		

		Estimate estimate = new Estimate();
		
		er.property_name=ParseRIFM_2026_08_12.property;
		
		if(ParseRIFM_2026_08_12.property.equals(ExperimentalConstants.strRBIODEG) || ParseRIFM_2026_08_12.property.equals(ExperimentalConstants.strPercentageBiodegradation)) {
		
			estimate.point=Double.parseDouble(Test_chemical_deg_at_28d_by_BOD_or_ThOD);	

			Double duration=28.0;
			String outputMode=null;
			if(ParseRIFM_2026_08_12.property.equals(ExperimentalConstants.strRBIODEG)) {
				outputMode=ExperimentalConstants.str_binary;
			} else if (ParseRIFM_2026_08_12.property.equals(ExperimentalConstants.strPercentageBiodegradation)) {
				outputMode=ExperimentalConstants.str_continuous;
			}
			BiodegradationPropertyValues.setPropertyValues(er, outputMode, estimate, duration);
			
		} else if(ParseRIFM_2026_08_12.property.equals(ExperimentalConstants.strRBIODEG_10_day)) {
			
			if(Pass_w_in_10d_window.equals("Y")) {
				er.property_value_point_estimate_original=1.0;
			} else if(Pass_w_in_10d_window.equals("N")) {
				er.property_value_point_estimate_original=0.0;
			}
			er.property_value_units_original = ExperimentalConstants.str_binary;
			er.property_value_units_final=ExperimentalConstants.str_binary;
			er.property_value_point_estimate_final=er.property_value_point_estimate_original;
		
		} else if(ParseRIFM_2026_08_12.property.equals(ExperimentalConstants.strRBIODEG_multiclass)) {

			if(Pass_w_in_10d_window.equals("Y")) {
				er.property_value_point_estimate_original=2.0;
				er.property_value_string="Pass_w_in_10d_window=Y";

				if(!Meets_deg_pass_level.equals("Y")) {
					System.out.println(this.Test_substance_CASRN+"\tpasses 10 day window, fails pass level");//shouldnt happen
				}
				
			} else if(Meets_deg_pass_level.equals("Y")) {
				er.property_value_point_estimate_original=1.0;
				er.property_value_string="Pass_w_in_10d_window=N, Meets_deg_pass_level=Y";
				
			} else if (Meets_deg_pass_level.equals("N")) {
				er.property_value_point_estimate_original=0.0;
				er.property_value_string="Meets_deg_pass_level=N";
			} else {
				System.out.println("Invalid original record:\n"+JsonUtilities.gsonPretty.toJson(this)+"\n");
			}
			er.property_value_point_estimate_final=er.property_value_point_estimate_original;			
			er.property_value_units_original = ExperimentalConstants.str_multiclass;
			er.property_value_units_final=er.property_value_units_original;

		} else if(ParseRIFM_2026_08_12.property.equals(ExperimentalConstants.strRBIODEG_10_day2)) {

			if(Pass_w_in_10d_window.equals("Y")) {
				er.property_value_point_estimate_original=1.0;
				er.property_value_string="Pass_w_in_10d_window=Y";

			} else if(Meets_deg_pass_level.equals("Y")) {
				er.property_value_point_estimate_original=0.0;
				er.property_value_string="Pass_w_in_10d_window=N, Meets_deg_pass_level=Y";
				
			} else {
				er.keep = false;
				er.updateReason("Pass_w_in_10d_window="+Pass_w_in_10d_window+", Meets_deg_pass_level="+Meets_deg_pass_level);
			}
			er.property_value_point_estimate_final=er.property_value_point_estimate_original;			
			er.property_value_units_original = ExperimentalConstants.str_binary;
			er.property_value_units_final=er.property_value_units_original;

		}
		
		setParameters(er);
		
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

//		if(Duration.contains("days")) {
//			String strDuration=Duration.replace(" days", "");
//			pv.value_point_estimate=Double.parseDouble(strDuration);
//			pv.unit.abbreviation="days";
//			pv.unit.name = "DAYS";
//			er.parameter_values.add(pv);
//		} else {
//			System.out.println("Different duration units:"+Duration+" for CAS="+CAS);
//		}

		pv.value_point_estimate=28.0;
		pv.unit.abbreviation="days";
		pv.unit.name = "DAYS";
		er.parameter_values.add(pv);
		
		er.experimental_parameters.put("Test guideline","301F");
		
		
//		mg/L ThOD
		
		addParameterTestChemicalConcentration(er);
		addParameterInoculumConcentration(er);
		addParameterpH(er);
		addMeasurementMethod(er);
		addReliability(er);
		addInoculumSource(er);
		
		if(!er.property_name.equals(ExperimentalConstants.strRBIODEG_10_day)) {
			addPass10day(er);	
		}
		
		
	}

	private void addParameterTestChemicalConcentration(ExperimentalRecord er) {
		
		ParameterValue pv=new ParameterValue();
		pv.parameter.name="Test chemical concentration";
		
		if(this.Test_chemical_concentration.contains("mgThOD/L") || this.Test_chemical_concentration.contains("mg/L ThOD") || this.Test_chemical_concentration.contains("mg ThOD/L")) {
			pv.unit.abbreviation="mg/L ThOD";
			pv.unit.name="MG_L_THOD";
			String tcc = Test_chemical_concentration
			        .replace("mgThOD/L", "")
			        .replace("mg/L ThOD", "")
			        .replace("mg ThOD/L", "")
			        .trim();
//			String tcc=Test_chemical_concentration.replace("mgThOD/L", "".replace("mg/L ThOD","")).trim();
			pv.value_point_estimate=Double.parseDouble(tcc);
			er.parameter_values.add(pv);
		} else if(this.Test_chemical_concentration.contains("mg/L")) {
			pv.unit.abbreviation="mg/L";
			pv.unit.name="MG_L";
			pv.value_point_estimate=Double.parseDouble(Test_chemical_concentration.replace("mg/L", "").trim());
			er.parameter_values.add(pv);
		
		} else {
			System.out.println(this.Test_chemical_concentration);
		}
	}

	
	private void addParameterpH(ExperimentalRecord er) {

		ParameterValue pv=new ParameterValue();
		pv.parameter.name="pH";

		if(this.pH.contains("-")) {
			String []vals=pH.split("-");
			pv.value_min=Double.parseDouble(vals[0]);
			pv.value_max=Double.parseDouble(vals[1]);
			pv.unit.abbreviation="Log units";
			pv.unit.name="LOG_UNITS";
			er.parameter_values.add(pv);
		} else {
			System.out.println("pH="+this.pH);
		}
		
	}

	
	
	private void addReliability(ExperimentalRecord er) {
		if(Overall_quality_acceptable_auto!=null) {
			er.experimental_parameters.put("Reliability", Overall_quality_acceptable_auto);
		}
	}
	
	private void addPass10day(ExperimentalRecord er) {
		if(Pass_w_in_10d_window!=null) {
			er.experimental_parameters.put("Pass in 10 day window", Pass_w_in_10d_window);
		}
	}
	
	
	private void addInoculumSource(ExperimentalRecord er) {
		if(Inoculum_source_s!=null) {
			er.experimental_parameters.put("Inoculum source", Inoculum_source_s);
		}
	}

	private void addMeasurementMethod(ExperimentalRecord er) {
		if(Method_used_to_measure_the_respective_deg_at_28d!=null) {
			er.experimental_parameters.put("Measurement method", Method_used_to_measure_the_respective_deg_at_28d);
		}
	}
	
	private void addParameterInoculumConcentration(ExperimentalRecord er) {
		
		ParameterValue pv=new ParameterValue();
		pv.parameter.name="Inoculum concentration";
		
		
		if(this.Inoculum_concentration.equals("10 mL/L") || this.Inoculum_concentration.equals("10mL effluent/L")) {
			pv.unit.abbreviation="mL/L";
			pv.unit.name="ML_L";
			pv.value_point_estimate=10.0;
			er.parameter_values.add(pv);
			
		} else if(this.Inoculum_concentration.equals("10E7-10E8 cells/L")) {
			pv.unit.abbreviation="cells/L";
			pv.unit.name="CELLS_L";
			pv.value_min=1e7;
			pv.value_max=1e8;
			er.parameter_values.add(pv);
			
		} else if(this.Inoculum_concentration.contains("mg/L")) {
			pv.unit.abbreviation="mg/L";
			pv.unit.name="MG_L";
			pv.value_point_estimate=Double.parseDouble(Inoculum_concentration.replace("mg/L", "").trim());
			er.parameter_values.add(pv);
		} else {
			System.out.println("Inoculum_concentration="+this.Inoculum_concentration);
		}
	}
}