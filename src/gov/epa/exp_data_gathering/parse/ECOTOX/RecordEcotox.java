package gov.epa.exp_data_gathering.parse.ECOTOX;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.WordUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.database.SQLite_Utilities;
import gov.epa.database.SqlUtilities;

import gov.epa.exp_data_gathering.parse.ChemicalNameFixer;
import gov.epa.exp_data_gathering.parse.BCFUtilities;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.UnitConverter;


/**
 * @author TMARTI02
 */
public class RecordEcotox {
	
//	public static String sourceName=ExperimentalConstants.strSourceEcotox_2024_12_12;
//	public static String databasePath = "data\\experimental\\ECOTOX_2024_12_12\\ecotox_ascii_12_12_2024.db";

	public static String sourceName=ExperimentalConstants.strSourceEcotox_2026_03_12;
	public static String databasePath = "data\\experimental\\ECOTOX_2026_03_12\\ecotox_ascii_03_12_2026.db";
	
	public String property_name;
	
	public String test_id;
	public String reference_number;
	public String test_cas;
	public String test_grade;
	public String test_grade_comments;
	public String test_formulation;
	public String test_formulation_comments;
	public String test_radiolabel;
	public String test_radiolabel_comments;
	public String test_purity_mean_op;
	public String test_purity_mean;
	public String test_purity_min_op;
	public String test_purity_min;
	public String test_purity_max_op;
	public String test_purity_max;
	public String test_purity_comments;
	public String test_characteristics;
	public String organism_habitat;
	public String organism_source;
	public String organism_source_comments;
	public String organism_lifestage;
	public String organism_lifestage_comments;
	public String organism_age_mean_op;
	public String organism_age_mean;
	public String organism_age_min_op;
	public String organism_age_min;
	public String organism_age_max_op;
	public String organism_age_max;
	public String organism_age_unit;
	public String organism_init_wt_mean_op;
	public String organism_init_wt_mean;
	public String organism_init_wt_min_op;
	public String organism_init_wt_min;
	public String organism_init_wt_max_op;
	public String organism_init_wt_max;
	public String organism_init_wt_unit;
	public String organism_length_mean_op;
	public String organism_length_mean;
	public String organism_length_min_op;
	public String organism_length_min;
	public String organism_length_max_op;
	public String organism_length_max;
	public String organism_length_type;
	public String organism_length_unit;
	public String organism_strain;
	public String organism_characteristics;
	public String organism_gender;
	public String experimental_design;
	public String study_duration_mean_op;
	public String study_duration_mean;
	public String study_duration_min_op;
	public String study_duration_min;
	public String study_duration_max_op;
	public String study_duration_max;
	public String study_duration_unit;
	public String study_duration_comments;
	public String exposure_duration_mean_op;
	public String exposure_duration_mean;
	public String exposure_duration_min_op;
	public String exposure_duration_min;
	public String exposure_duration_max_op;
	public String exposure_duration_max;
	public String exposure_duration_unit;
	public String exposure_duration_comments;
	public String study_type;
	
	public String code;
	public String description;
	
	public String study_type_comments;
	public String test_type;
	public String test_type_comments;
	public String test_location;
	public String test_location_comments;
	public String test_method;
	public String test_method_comments;
	public String exposure_type;
	public String exposure_type_comments;
	public String control_type;
	public String control_type_comments;
	public String media_type;
	public String media_type_comments;
	public String num_doses_mean_op;
	public String num_doses_mean;
	public String num_doses_min_op;
	public String num_doses_min;
	public String num_doses_max_op;
	public String num_doses_max;
	public String num_doses_comments;
	public String other_effect_comments;
	public String application_freq_mean_op;
	public String application_freq_mean;
	public String application_freq_min_op;
	public String application_freq_min;
	public String application_freq_max_op;
	public String application_freq_max;
	public String application_freq_unit;
	public String application_freq_comments;
	public String application_type;
	public String application_type_comments;
	public String application_rate;
	public String application_rate_unit;
	public String application_date;
	public String application_date_comments;
	public String application_season;
	public String application_season_comments;
	public String subhabitat;
	public String subhabitat_description;
	public String substrate;
	public String substrate_description;
	public String water_depth_mean_op;
	public String water_depth_mean;
	public String water_depth_min_op;
	public String water_depth_min;
	public String water_depth_max_op;
	public String water_depth_max;
	public String water_depth_unit;
	public String water_depth_comments;
	public String geographic_code;
	public String geographic_location;
	public String latitude;
	public String longitude;
	public String halflife_mean_op;
	public String halflife_mean;
	public String halflife_min_op;
	public String halflife_min;
	public String halflife_max_op;
	public String halflife_max;
	public String halflife_unit;
	public String halflife_comments;
	public String published_date;
	public String result_id;
	public String sample_size_mean_op;
	public String sample_size_mean;
	public String sample_size_min_op;
	public String sample_size_min;
	public String sample_size_max_op;
	public String sample_size_max;
	public String sample_size_unit;
	public String sample_size_comments;
	public String obs_duration_mean_op;
	public String obs_duration_mean;
	public String obs_duration_min_op;
	public String obs_duration_min;
	public String obs_duration_max_op;
	public String obs_duration_max;
	public String obs_duration_unit;
	public String obs_duration_comments;
	public String endpoint;
	public String endpoint_comments;
	public String trend;
	public String effect;
	public String effect_comments;
	public String measurement;
	public String measurement_comments;
	public String response_site;
	public String response_site_comments;
	public String effect_pct_mean_op;
	public String effect_pct_mean;
	public String effect_pct_min_op;
	public String effect_pct_min;
	public String effect_pct_max_op;
	public String effect_pct_max;
	public String effect_pct_comments;
	public String conc1_type;
	public String ion1;

	public String conc1_mean_op;
	public Double conc1_mean;

	public String conc1_min_op;
	public Double conc1_min;
	
	public String conc1_max_op;
	public Double conc1_max;
	
	public String conc1_unit;
	public String conc1_comments;
	public String conc2_type;

	public String ion2;
	
	public String conc2_mean_op;
	public Double conc2_mean;
	
	public String conc2_min_op;
	public Double conc2_min;
	
	public String conc2_max_op;	
	public Double conc2_max;
	
	public String conc2_unit;
	public String conc2_comments;
	
	public String conc3_type;
	public String ion3;
	public String conc3_mean_op;
	public Double conc3_mean;
	public String conc3_min_op;
	public String conc3_min;
	public String conc3_max_op;
	public String conc3_max;
	public String conc3_unit;
	public String conc3_comments;
	
	public String bcf1_mean_op;
	public Double bcf1_mean;

	public String bcf1_min_op;
	public Double bcf1_min;
	
	public String bcf1_max_op;
	public Double bcf1_max;
	
	public String bcf1_unit;
	public String bcf1_comments;
	
	public String bcf2_mean_op;
	public Double bcf2_mean;
	public String bcf2_min_op;
	public String bcf2_min;
	public String bcf2_max_op;
	public String bcf2_max;
	public String bcf2_unit;
	public String bcf2_comments;
	
	public String bcf3_mean_op;
	public Double bcf3_mean;
	public String bcf3_min_op;
	public String bcf3_min;
	public String bcf3_max_op;
	public String bcf3_max;
	public String bcf3_unit;
	public String bcf3_comments;
	
	public String significance_code;
	public String significance_type;
	public String significance_level_mean_op;
	public String significance_level_mean;
	public String significance_level_min_op;
	public String significance_level_min;
	public String significance_level_max_op;
	public String significance_level_max;
	public String significance_comments;
	public String chem_analysis_method;
	public String chem_analysis_method_comments;
	public String endpoint_assigned;
	public String organism_final_wt_mean_op;
	public String organism_final_wt_mean;
	public String organism_final_wt_min_op;
	public String organism_final_wt_min;
	public String organism_final_wt_max_op;
	public String organism_final_wt_max;
	public String organism_final_wt_unit;
	public String organism_final_wt_comments;
	
	public String intake_rate_mean_op;
	public Double intake_rate_mean;
	public String intake_rate_min_op;
	public String intake_rate_min;
	public String intake_rate_max_op;
	public String intake_rate_max;
	public String intake_rate_unit;
	public String intake_rate_comments;
	
	public String lipid_pct_mean_op;
	public Double lipid_pct_mean;
	public String lipid_pct_min_op;
	public String lipid_pct_min;
	public String lipid_pct_max_op;
	public String lipid_pct_max;
	public String lipid_pct_comments;
	
	public String dry_wet;
	public String dry_wet_pct_mean_op;
	public Double dry_wet_pct_mean;
	public String dry_wet_pct_min_op;
	public String dry_wet_pct_min;
	public String dry_wet_pct_max_op;
	public String dry_wet_pct_max;
	public String dry_wet_pct_comments;
	
	public String steady_state;
	public String additional_comments;
	public String companion_tag;
	public String created_date;
	public String modified_date;
	public String old_terretox_result_number;
	public String cas_number;
	public String chemical_name;
	public String ecotox_group;
	public String dtxsid;
	public String reference_db;
	public String reference_type;
	public String author;
	public String title;
	public String source;
	public String publication_year;
	public String doi;
	
	public String species_number;
	public String latin_name;
	public String common_name;
	public String kingdom;
	public String phylum_division;
	public String subphylum_div;
	public String superclass;
	
	public String class_;
	public String tax_order;
	public String family;
	public String genus;
	public String species;
	public String subspecies;
	public String variety;
	public String ncbi_taxid;	
	
	//media_characteristics:
	public String media_temperature_mean_op;
	public String media_temperature_mean;
	public String media_temperature_min;
	public String media_temperature_max;
	public String media_temperature_unit;
	
	
	transient Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	transient static HashSet<String>conc1_units=new HashSet<>();
	static transient UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");

	/**
	 * In the SQL query that gets the data it filters out non FW and LAB
	 * "media_type like '%FW%' and test_location like '%LAB%'
	 * Instead, store the media_type and test_location as parameters
	 * 
	 * @param propertyName
	 * @param valueNumber
	 * @return
	 */
	ExperimentalRecord toExperimentalRecordFishTox(String propertyName, int valueNumber) {

		String conc_type=null;

		Double conc_mean=null;
		Double conc_min=null;
		Double conc_max=null;
		
		String conc_mean_op=null;
		String conc_min_op=null;
		String conc_max_op=null;
		String conc_unit=null;
		
		if(valueNumber==1) {
			conc_type=conc1_type;
			conc_mean=conc1_mean;			
			conc_min=conc1_min;
			conc_max=conc1_max;
			conc_mean_op=conc1_mean_op;
			conc_min_op=conc1_min_op;
			conc_max_op=conc1_max_op;
			conc_unit=conc1_unit;
		} else if (valueNumber==2) {
			conc_type=conc2_type;
			conc_mean=conc2_mean;
			conc_min=conc2_min;
			conc_max=conc2_max;
			conc_mean_op=conc2_mean_op;
			conc_min_op=conc2_min_op;
			conc_max_op=conc2_max_op;
			conc_unit=conc2_unit;
		}
		

		if(conc_min!=null && conc_min==0) conc_min=null;

		if(conc_unit!=null) {
			if(conc_unit.equals("ml/L")) conc_unit="mL/L";
			if(conc_unit.equals("ug/ml")) conc_unit="mg/L";
		}
					
		ExperimentalRecord er=new ExperimentalRecord();
		
		setChemicalIdentifiers(er);
		
//		System.out.println(cas_number+"\t"+er.casrn);
		
		er.property_name=propertyName;
		er.keep=true;
		
		setLiteratureSource(er);
		
		er.property_value_units_original=conc_unit;
		
		if (er.keep) {
			
			if(conc_mean!=null) {
				er.property_value_numeric_qualifier=conc_mean_op;
				er.property_value_point_estimate_original=conc_mean;	
				er.property_value_string=er.property_value_point_estimate_original+" "+conc_unit;
			
			} else  if(conc_min!=null && conc_max!=null) {

				er.property_value_min_original=conc_min;
				er.property_value_max_original=conc_max;
				er.property_value_string=conc_min+" "+conc_unit+" < "+endpoint+" <"+conc_max+" "+conc_unit;

				double log=Math.log10(conc_max/conc_min);
				
				if(log>1) {
					er.keep=false;
					er.reason="Range of min and max is too wide";
//					System.out.println("Range too wide:"+conc_min+" to "+conc_max+" "+conc_unit);
				} else {
//					er.property_value_point_estimate_original=Math.sqrt(conc_min*conc_max);
				}
				
			} else if(conc_min!=null) {
				er.property_value_min_original=conc_min;
				er.property_value_string=endpoint+" > "+conc_min+" "+conc_unit;				
			} else if (conc_max!=null) {
				er.property_value_max_original=conc_max;
				er.property_value_string=endpoint+" < "+conc_max+" "+conc_unit;				

			}

			
		} 


		
		er.experimental_parameters=new LinkedHashMap<>();//keeps insertion order

		er.parameter_values=new ArrayList<>();
		er.experimental_parameters.put("test_id", test_id);
		er.experimental_parameters.put("result_id", result_id);
		er.experimental_parameters.put("Media type", media_type);
		er.experimental_parameters.put("exposure_type", exposure_type);
		er.experimental_parameters.put("chem_analysis_method", chem_analysis_method);
		er.experimental_parameters.put("Test location", test_location);
		er.experimental_parameters.put("concentration_type", getConcentrationType(conc_type));
		
		
		if(er.property_name.equals(ExperimentalConstants.strAcuteAquaticToxicity)
				|| er.property_name.equals(ExperimentalConstants.strChronicAquaticToxicity)) {//general property
			er.experimental_parameters.put("test_type", endpoint);
			setObservationDuration(er,"Observation duration");
			addSpeciesParameters(er);
		}

		if(er.property_name.equals(ExperimentalConstants.strAcuteAquaticToxicity) || er.property_name.contains("LC50")) {
			er.property_category=ExperimentalConstants.strAcuteAquaticToxicity;			
		} else if(er.property_name.equals(ExperimentalConstants.strChronicAquaticToxicity)) {
			er.property_category=ExperimentalConstants.strChronicAquaticToxicity;
		}

		
		if(effect!=null) {
			er.experimental_parameters.put("Effect", effect);
		}


		if(er.property_value_point_estimate_original==null && er.property_value_min_original==null && er.property_value_max_original==null ) {
			er.keep=false;
			er.reason="No final numerical value";
		}

		if(er.property_value_units_original==null  || er.property_value_units_original.equals("NR")) {
			er.keep=false;
			er.reason="Units missing";
		}
		
		if(er.keep) {
			uc.convertRecord(er);
		}
		
		
		return er;
		
	}
	
	private void addSpeciesParameters(ExperimentalRecord er) {
		er.experimental_parameters.put("Species latin", latin_name);
		er.experimental_parameters.put("Species common", common_name);
		String supercategory=getSpeciesSupercategory();
		if(supercategory!=null) {
			er.experimental_parameters.put("Species supercategory",supercategory);	
//				System.out.println(ecotox_group+"\t"+common_name+"\t"+supercategory);
//				System.out.println(common_name+"\t"+supercategory);
		} else {
			System.out.println(common_name+"\t"+ecotox_group+"\t"+common_name+"\tMissing supercategory");
		}
		
		String speciesType=getSpeciesType();
		er.experimental_parameters.put("Species type", speciesType);
		
	}
	
		


	private void setObservationDuration(ExperimentalRecord er,String parameterName) {
		
		String unit=obs_duration_unit;
		Double mean=getValueInDays(obs_duration_mean,unit);
		Double min=getValueInDays(obs_duration_min,unit);
		if(min!=null && min==0) min=null;
		Double max=getValueInDays(obs_duration_max,unit);
		String meanOp=obs_duration_mean_op;
		
		if(mean==null && min==null && max==null)return;
				
		ParameterValue pv=new ParameterValue();
		pv.parameter.name=parameterName;
		pv.value_qualifier=meanOp;
		pv.value_point_estimate=mean;
		pv.unit.abbreviation="days";

		int type=-1;
		if(mean!=null) {
			type=1;
			if(meanOp!=null && meanOp.contains("<")) {
				type=2;
				pv.value_max=mean;
				pv.value_point_estimate=null;
			} else if(meanOp!=null && meanOp.contains(">")) {
				pv.value_min=mean;
				pv.value_point_estimate=null;
				type=3;
			}
			
		} else if(min!=null && max!=null) {
			type=4;
			pv.value_min=min;
			pv.value_max=max;
		} else if(min!=null) {//doesnt happen for BCF?
			type=5;
			pv.value_min=min;
		} else if(max!=null) {//doesnt happen for BCF?
			type=6;
			pv.value_max=max;
		} else {
			type=7;
			return;
		}
		
		er.parameter_values.add(pv);
//		System.out.println("BCF obs dur type="+type+"\t"+meanOp+"\t"+mean+"\t"+min+"\t"+max+"\t"+unit);
		
	}
	
	/**
	 * Use observation duration in ecotox instead
	 * 
	 * @param er
	 * @param parameterName
	 */
	@Deprecated
	private void setExposureDuration(ExperimentalRecord er,String parameterName) {
		
		String unit=exposure_duration_unit;		
		Double mean=getValueInDays(exposure_duration_mean,unit);
		Double min=getValueInDays(exposure_duration_min,unit);	
		Double max=getValueInDays(exposure_duration_max,unit);
		String meanOp=exposure_duration_mean_op;
		
		if(min!=null && min==0) min=null;
		
		if(mean==null && min==null && max==null)return;
				
		ParameterValue pv=new ParameterValue();
		pv.parameter.name=parameterName;
		pv.value_qualifier=meanOp;
		pv.value_point_estimate=mean;
		pv.unit.abbreviation="days";

		int type=-1;
		if(mean!=null) {
			type=1;
			if(meanOp!=null && meanOp.contains("<")) {
				type=2;
				pv.value_max=mean;
				pv.value_point_estimate=null;
			} else if(meanOp!=null && meanOp.contains(">")) {
				pv.value_min=mean;
				pv.value_point_estimate=null;
				type=3;
			}
			
		} else if(min!=null && max!=null) {
			type=4;
			pv.value_min=min;
			pv.value_max=max;
		} else if(min!=null) {//doesnt happen for BCF?
			type=5;
			pv.value_min=min;
		} else if(max!=null) {//doesnt happen for BCF?
			type=6;
			pv.value_max=max;
		} else {
			type=7;
			return;
		}
		
		er.parameter_values.add(pv);
		
	}
	

	/**
	 * Query sqlite db for BCF records
	 * 
	 * //TODO also get the following: // t.test_radiolabel: whether concentrations are imprecise radiolabel measurements (need metabolite correction?) 
	 * r.additional_comments: have kinetic vs conc method for BCF? //
	 * 
	 * Old query:
	 * String sql="select r.endpoint, t.test_id, dtxsid, cas_number,
	 * chemical_name, bcf1_mean ,bcf1_unit,\r\n" // + " conc1_mean_op, conc1_mean,
	 * conc1_unit, conc1_min, conc1_max, conc1_min_op, conc1_max_op,\r\n" // +
	 * "exposure_duration_mean_op,
	 * exposure_duration_mean,exposure_duration_unit,\r\n" // + "media_type,
	 * test_location, exposure_type,chem_analysis_method, s.common_name,
	 * s.latin_name,s.ecotox_group, rsc.description as 'response_site',\r\n" // + "
	 * author, publication_year, title,source from tests t\r\n"
	 * 
	 * @param endpoint
	 * @return
	 */
	public static List<RecordEcotox> get_BCF_Records_From_DB(String endpoint) {

	    List<RecordEcotox> records = new ArrayList<>();

	    String sql =
	        "select " +
	        "  t.*, " +
	        "  r.*, " +
	        "  c.*, " +
	        "  r2.*, " +
	        "  s.*, " +
	        "  rsc.description as response_site, " +
	        "  mc.media_temperature_mean_op, " +
	        "  mc.media_temperature_mean, " +
	        "  mc.media_temperature_min, " +
	        "  mc.media_temperature_max, " +
	        "  mc.media_temperature_unit, " +
	        "  coalesce(et.description, 'Not reported') as exposure_type, " +
	        "  coalesce(ca.description, 'Not reported') as chem_analysis_method, " +
	        "  coalesce(mt.description, 'Not reported') as media_type, " +
	        "  coalesce(tl.description, 'Not reported') as test_location " +
	        "from tests t " +
	        "join results r on t.test_id = r.test_id " +
	        "join chemicals c on c.cas_number = t.test_cas " +
	        "left join references_ r2 on r2.reference_number = t.reference_number " +
	        "left join species s on t.species_number = s.species_number " +
	        "left join response_site_codes rsc on rsc.code = r.response_site " +
	        "left join media_characteristics mc on mc.result_id = r.result_id " +
	        "left join exposure_type_codes et on replace(t.exposure_type, '/', '') = et.code " +
	        "left join chemical_analysis_codes ca on replace(r.chem_analysis_method, '/', '') = ca.code " +
	        "left join media_type_codes mt on replace(t.media_type, '/', '') = mt.code " +
	        "left join test_location_codes tl on replace(t.test_location, '/', '') = tl.code " +
	        "where r.bcf1_mean is not null and r.endpoint = '" + endpoint + "';";

	    System.out.println(sql);

	    try {
	        Statement stat = SQLite_Utilities.getStatement(databasePath);
	        ResultSet rs = stat.executeQuery(sql);

	        while (rs.next()) {
	            RecordEcotox rec = new RecordEcotox();
	            SqlUtilities.createRecord(rs, rec);
	            records.add(rec);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return records;
	}
	
	 
//	/**
//	 * @deprecated should just get all records and filter later
//	 * @param speciesNumber
//	 * @param propertyName
//	 * @return
//	 */
//	public static List<RecordEcotox> get_Acute_Tox_Records_From_DB(int speciesNumber,String propertyName) {
//
//		List<RecordEcotox>records=new ArrayList<>();
//
//		String sql = "select *\n" + "from tests t\n" + "join results r on t.test_id=r.test_id\n"
//				+ "join chemicals c on c.cas_number=t.test_cas\n"
//				+ "join references_ r2 on r2.reference_number=t.reference_number\n"
////				+ "left join exposure_type_codes etc on t.exposure_type=etc.code "
////				+ "left join chemical_analysis_codes cac on r.chem_analysis_method=cac.code "								
//				+ "where t.species_number="+speciesNumber+" and \r\n"
//				+ "media_type like '%FW%' and test_location like '%LAB%' and \r\n"
//				+ "endpoint like '%LC50%' and \r\n"
//				+ "measurement like '%MORT%';";//just use MORT to be safe
////				+ "(measurement like '%MORT%' or measurement like '%SURV%');";
//		
//		//Note filter for duration happens later
//		
//				System.out.println(sql);
//		try {
//
//			Statement stat = SQLite_Utilities.getStatement(databasePath);
//			ResultSet rs = stat.executeQuery(sql);
//
////			JsonArray ja = new JsonArray();
//
//			Hashtable<String,String>htExposureType=getLookup(databasePath,"exposure_type_codes");
//			
//			int counter=0;
//			
//			while (rs.next()) {
//				
//				counter++;
////				System.out.println(rs.getString(1));
////				JsonObject jo = new JsonObject();
//
//				RecordEcotox rec=new RecordEcotox();
//				SqlUtilities.createRecord(rs, rec);
//				
//				rec.property_name=propertyName;
//				rec.setFromLookup(htExposureType, rec.exposure_type);
//				rec.setChemicalAnalysisMethod();
//				records.add(rec);
//			}
//
//
//			System.out.println(records.size());
//
////			System.out.println(gson.toJson(records));
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//
//		}
//		
//		return records;
//
//	}


	
	public static List<RecordEcotox> get_Acute_Tox_Records_From_DB() {

	    List<RecordEcotox> records = new ArrayList<>();

	    String sql =
	        "select " +
	        "  t.*, " +
	        "  r.*, " +
	        "  c.*, " +
	        "  r2.*, " +
	        "  s.*, " +
	        "  coalesce(e.description, 'Not reported') as effect, " +
	        "  coalesce(x.description, 'Not reported') as exposure_type, " +
	        "  coalesce(a.description, 'Not reported') as chem_analysis_method, " +
	        "  coalesce(mt.description, 'Not reported') as media_type, " +
	        "  coalesce(tl.description, 'Not reported') as test_location\n" +
	        "from tests t " +
	        "join results r on t.test_id = r.test_id " +
	        "join chemicals c on c.cas_number = t.test_cas " +
	        "join references_ r2 on r2.reference_number = t.reference_number " +
	        "left join species s on t.species_number = s.species_number " +
	        "left join effect_codes e on replace(r.effect, '/', '') = e.code " +
	        "left join exposure_type_codes x on replace(t.exposure_type, '/', '') = x.code " +
	        "left join chemical_analysis_codes a on replace(r.chem_analysis_method, '/', '') = a.code " +
	        "left join media_type_codes mt on replace(t.media_type, '/', '') = mt.code " +
	        "left join test_location_codes tl on replace(t.test_location, '/', '') = tl.code " +
	        "where (r.endpoint like '%LC50%' or r.endpoint like '%EC50%') " +
	        "  and r.measurement like '%MORT%';";

	    System.out.println(sql);

	    try {
	        Statement stat = SQLite_Utilities.getStatement(databasePath);
	        ResultSet rs = stat.executeQuery(sql);

	        while (rs.next()) {
	            RecordEcotox rec = new RecordEcotox();
	            SqlUtilities.createRecord(rs, rec);
	            records.add(rec);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return records;
	}
	
	
	public static List<RecordEcotox> get_Chronic_Tox_Records_From_DB() {

	    List<RecordEcotox> records = new ArrayList<>();

	    String sql =
	        "select " +
	        "  t.*, " +
	        "  r.*, " +
	        "  c.*, " +
	        "  r2.*, " +
	        "  s.*, " +
	        "  coalesce(e.description, 'Not reported') as effect, " +
	        "  coalesce(x.description, 'Not reported') as exposure_type, " +
	        "  coalesce(m.description, 'Not reported') as chem_analysis_method, " +
	        "  coalesce(mt.description, 'Not reported') as media_type, " +
	        "  coalesce(tl.description, 'Not reported') as test_location " +
	        "from tests t " +
	        "join results r on t.test_id = r.test_id " +
	        "join chemicals c on c.cas_number = t.test_cas " +
	        "join references_ r2 on r2.reference_number = t.reference_number " +
	        "left join species s on t.species_number = s.species_number " +
	        "left join effect_codes e on replace(r.effect, '/', '') = e.code " +
	        "left join exposure_type_codes x on replace(r.exposure_type, '/', '') = x.code " +
	        "left join chemical_analysis_codes m on replace(r.chem_analysis_method, '/', '') = m.code " +
	        "left join media_type_codes mt on replace(t.media_type, '/', '') = mt.code " +
	        "left join test_location_codes tl on replace(t.test_location, '/', '') = tl.code " +
	        "where (endpoint like '%LOEC%' or endpoint like '%NOEC%');";

	    System.out.println(sql);


	    try {
	        Statement stat = SQLite_Utilities.getStatement(databasePath);
	        ResultSet rs = stat.executeQuery(sql);

	        while (rs.next()) {
	            RecordEcotox rec = new RecordEcotox();
	            SqlUtilities.createRecord(rs, rec);
	            records.add(rec);
	        }

	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }

	    return records;
	}
	

	void setMediaType(Hashtable<String, String> htMediaType) {
		String code=media_type.replace("/", "");
		if(htMediaType.containsKey(code)) {
			media_type=htMediaType.get(code);
		} else {
			System.out.println("Unknown media_type: "+code);
		}
	}
	

	String getConcentrationType(String conc_type) {
		
		if(conc_type==null) return "Not available";
		else if(conc_type.equals("--")) return "Unspecified";
		else if (conc_type.equals("A")) return "Active ingredient";
		else if (conc_type.equals("D")) return "Dissolved";
		else if (conc_type.equals("F")) return "Formulation";
		else if (conc_type.equals("L")) return "Labile (free metal ion)";
		else if (conc_type.equals("NA")) return "Not applicable";
		else if (conc_type.equals("NC")) return "Not coded";
		else if (conc_type.equals("NR")) return "Not reported";
		else if (conc_type.equals("T")) return "Total";
		else if (conc_type.equals("U")) return "Unionized";
		else {
			System.out.println("Unknown conc_type:\t"+conc_type);
			return conc_type;
		}
	}
	

	public Double getValueInDays(String obs_duration,String units) {
		
		if(obs_duration==null) return null;
		Double studyDurationValue = Double.parseDouble(obs_duration);
		
		switch (units) {
		
		case "d":
		case "dpf":
		case "dph":
		case "dpu":
			return studyDurationValue;
		case "wk":
			return studyDurationValue *= 7.0;
		case "mo":
			return studyDurationValue *= 30.0;
		case "yr":
			return studyDurationValue *= 365.0;
		case "h":
		case "hpf":
		case "hph":
			return studyDurationValue /= 24.0;
		case "mi"://minutes
			return studyDurationValue /= 1440.0;
		case "s"://seconds
			return studyDurationValue /= (1440.0*60);

		case "-":
		case "NR":	
//			System.out.println("No study duration units for ToxVal ID " + toxval_id);
			return null;
		default:
//			System.out.println("Unknown observation duration units for ToxVal ID " + test_id + ": " + obs_duration_unit);
			return null;
		}
		
		
	}

	public boolean isAcceptableDuration(Double durationDays) {

		Double studyDurationValueInDays = getValueInDays(obs_duration_mean,obs_duration_unit);

		if (studyDurationValueInDays == null || studyDurationValueInDays < 0.95 * durationDays
				|| studyDurationValueInDays > 1.05 * durationDays) {
			return false;
		}

		return true;
	}
		
	
	private String getSpeciesSupercategory() {

		if(ecotox_group==null) {
			System.out.println("Missing ecotox_group for "+common_name);
			return null;
		}
		
		String egLC=ecotox_group.toLowerCase();
		
		if(egLC.contains("fish")) {
			return "Fish";
		} else if(egLC.contains("algae")) {
			return "Algae";
		} else if(egLC.contains("amphibians")) {
			return "Amphibians";
		} else if(egLC.contains("crustaceans")) {
			return "Crustaceans";
		} else if(egLC.contains("insects/spiders")) {
			return "Insects/spiders";
		} else if(egLC.contains("molluscs")) {
			return "Molluscs";
		} else if(egLC.contains("moss, hornworts")) {
			return "Moss, hornworts";
		} else if(egLC.contains("reptiles")) {
			return "Reptiles";
		} else if(egLC.contains("birds")) {
			return "Birds";
		} else if(egLC.contains("fungi")) {
			return "Fungi";
		} else if(egLC.contains("miscellaneous")) {
			return "Miscellaneous";
		} else if(egLC.contains("mammals")) {
			return "Mammals";
		} else if(egLC.contains("worms")) {
			return "Worms";
		} else if(egLC.contains("invertebrates")) {
			return "Invertebrates";
		} else if(egLC.contains("flowers, trees, shrubs, ferns")) {
			return "Flowers, trees, shrubs, ferns";
		} else if(egLC.equals("omit")) {
			return "Omit";
		} else {
			System.out.println("Handle\t"+ecotox_group);	
		}

		return null;
	}

	private String getSpeciesType() {

		if(ecotox_group==null) {
//			System.out.println("Missing ecotox_group for "+common_name);
			return null;
		}
		
		String egLC=ecotox_group.toLowerCase();
		
		if(egLC.contains("standard")) {
			return "standard";
		} else if(egLC.contains("invasive")) {
			return "invasive";
		} else if(egLC.contains("nuisance")) {
			return "nuisance";
		}  else {
			return "nondefined";
//			System.out.println("Handle "+egLC);
		}

		
	}

	//Simple class so can look at values with gson
	class BCF {
		
		public String bcf1_mean_op;
		public Double bcf1_mean;

		public String bcf1_min_op;
		public Double bcf1_min;
		
		public String bcf1_max_op;
		public Double bcf1_max;
		
		public String bcf1_unit;
		public String bcf1_comments;

		public String test_location;
		public String media_type;

		BCF(RecordEcotox r) {
			this.bcf1_mean_op=r.bcf1_mean_op;
			this.bcf1_mean=r.bcf1_mean;
			this.bcf1_min_op=r.bcf1_min_op;
			this.bcf1_min=r.bcf1_min;
			this.bcf1_max_op=r.bcf1_max_op;
			this.bcf1_max=r.bcf1_max;
			this.bcf1_unit=r.bcf1_unit;
			this.bcf1_comments=r.bcf1_comments;
			this.test_location=r.test_location;
			this.media_type=r.media_type;
		}

	}
	
	/**
	 * Converts Ecotox record to experimentalRecord. 
	 * 
	 * @param propertyName
	 * @return
	 */
	public ExperimentalRecord toExperimentalRecordBCF(String propertyName) {
		
		boolean limitToFish=false;
		if(propertyName.toLowerCase().contains("fish")) {
			limitToFish=true;
		}
		boolean limitToWholeBody=false;
		if(propertyName.toLowerCase().contains("whole")) {
			limitToWholeBody=true;
		}
		boolean limitToStandardTestSpecies=false;
		if(propertyName.toLowerCase().contains("standard")) {
			limitToStandardTestSpecies=true;
		}

		ExperimentalRecord er=new ExperimentalRecord();
		er.parameter_values=new ArrayList<>();
		er.property_name=propertyName;

		if(propertyName.toLowerCase().contains("bioconcentration")) {
			er.property_category="bioconcentration";
			
		} else if (propertyName.toLowerCase().contains("bioaccumulation")) {
			er.property_category="bioaccumulation";
		}

		setChemicalIdentifiers(er);
		
		er.keep=true;

		er.property_value_units_original=bcf1_unit.replace("ml/mg", "L/g").replace("ml/g", "L/kg");
		er.property_value_point_estimate_original=bcf1_mean;
		er.property_value_numeric_qualifier=bcf1_mean_op;		

		setLiteratureSource(er);
		if(er.literatureSource.citation.contains("De Bruijn,J., and J. Hermens (1991)")) {
			er.keep=false;
			er.reason="Units conversion error for this journal article by ECOTOX group";
		}

		
		if(bcf1_mean!=null) {
			er.property_value_numeric_qualifier=bcf1_mean_op;
			er.property_value_point_estimate_original=bcf1_mean;	
			er.property_value_string=er.property_value_point_estimate_original+" "+bcf1_unit;//TODO

		} else if(bcf1_min!=null && bcf1_max!=null) {
			
			double log=Math.log10(bcf1_max/bcf1_min);
			if(log>1) {
				er.keep=false;
				er.reason="Range of min and max is too wide";
			} else {
//				er.property_value_point_estimate_original=Math.sqrt(bcf1_min*bcf1_max);
			}

			er.property_value_min_original=bcf1_min;
			er.property_value_max_original=bcf1_max;
			er.property_value_string=bcf1_min+" "+bcf1_unit+" < "+endpoint+" <"+bcf1_max+" "+bcf1_unit;
			
		} else if(bcf1_min!=null) {
			er.property_value_min_original=bcf1_min;
			er.property_value_string=endpoint+" > "+bcf1_min+" "+bcf1_unit;				
		} else if (bcf1_max!=null) {
			er.property_value_max_original=bcf1_max;
			er.property_value_string=endpoint+" < "+bcf1_max+" "+bcf1_unit;				
		}

		
//			System.out.println(r.conc1_max_op+"\t"+r.conc1_min_op+"\t"+r.conc1_mean_op);
		er.experimental_parameters=new LinkedHashMap<>();
		er.experimental_parameters.put("test_id", test_id);

		setSpeciesParameters(er); 
		
		if(limitToFish && ecotox_group!=null && !ecotox_group.toLowerCase().contains("fish")) {
			er.keep=false;
			er.reason="Not a fish species";
		}
		
		if(description==null) {
//			System.out.println(gson.toJson(this));
		} else {
			if(description.contains("Whole organism")) {
				er.experimental_parameters.put("Response site", "whole body");	
			} else if (description.toLowerCase().contains("multiple tissue/organ")) {
				er.experimental_parameters.put("Response site", "multiple tissue/organs");
			} else if (description.toLowerCase().contains("muscle+bone")) {
				er.experimental_parameters.put("Response site", "muscle and bone");
			} else if (description.toLowerCase().contains("root + stem")) {
				er.experimental_parameters.put("Response site", "root and stem");
			} else {
				er.experimental_parameters.put("Response site", description.toLowerCase().trim());
			} 
		}
		if(limitToWholeBody && (description==null || !description.equals("Whole organism")))  {
			er.keep=false;
			er.reason="Not whole body";
//			System.out.println(description);
		}

		if(limitToStandardTestSpecies && ecotox_group!=null && !ecotox_group.toLowerCase().contains("standard")) {
			er.keep=false;
			er.reason="Not a standard test species";
		}
		
		if (media_type != null) {
			if (media_type.toLowerCase().contains("fresh")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, "freshwater");
			} else if (media_type.toLowerCase().contains("salt")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, "saltwater");
			} else {
				er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, media_type.toLowerCase().trim());
			}
		}
		
		if (media_type.contains("water")) {
			setWaterConcentration(er);			
		}

		setObservationDuration(er,"Observation duration");//to be consistent with Arnot 2006
		
		er.experimental_parameters.put("Test location", test_location);
		er.experimental_parameters.put("exposure_type", WordUtils.capitalizeFully(exposure_type));
		er.experimental_parameters.put("chem_analysis_method", chem_analysis_method);

		// New parameters
		// Temperature
		if (media_temperature_mean != null || media_temperature_max != null || media_temperature_min != null) {

			// System.out.println("Have water conc="+Exposure_concentration_MeanValue);

			ParameterValue pv = new ParameterValue();
			pv.parameter.name = "Temperature";
			pv.unit.abbreviation = ExperimentalConstants.str_C;
			// pv.unit.abbreviation = media_temperature_unit;

			if (media_temperature_mean != null) {
				double mean = BCFUtilities.parseTemperature(media_temperature_mean, media_temperature_unit);
				pv.value_point_estimate = mean;
			} else if (media_temperature_max != null || media_temperature_min != null) {
				if (media_temperature_max != null) {
					double max = BCFUtilities.parseTemperature(media_temperature_max, media_temperature_unit);
					pv.value_max = max;
				}
				if (media_temperature_min != null) {
					double min = BCFUtilities.parseTemperature(media_temperature_min, media_temperature_unit);
					pv.value_min = min;
				}
			}

			er.parameter_values.add(pv);
		}

		// Lipid Percentage (using lipid_pct_*)
		Boolean foundLipidPct = false;
		if (lipid_pct_mean != null || lipid_pct_max != null || lipid_pct_min != null) {
			ParameterValue pv = new ParameterValue();
			pv.parameter.name = ExperimentalConstants.expParamLipidPercent;
			pv.unit.abbreviation = ExperimentalConstants.str_dimensionless;

			if (lipid_pct_mean != null) {
				pv.value_point_estimate = lipid_pct_mean;
			}
			if (lipid_pct_max != null) {
				pv.value_max = Double.parseDouble(lipid_pct_max);
			}
			if (lipid_pct_min != null) {
				pv.value_min = Double.parseDouble(lipid_pct_min);
			}

			er.parameter_values.add(pv);
			
			foundLipidPct = true;
		}

		// Measurement Method (using measurement_comments)
		if (measurement_comments != null && !measurement_comments.trim().isEmpty()) {
			if (measurement_comments.toLowerCase().contains("steady state")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Steady State");
			} else if (measurement_comments.toLowerCase().contains("kinetic")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Kinetic");
			} else if (measurement_comments.toLowerCase().contains("dynamic")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Dynamic");
			} else if (measurement_comments.toLowerCase().contains("non-equilibrium")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Non-Equilibrium");
			} else if (measurement_comments.toLowerCase().contains("equilib")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Equilibrium");
			} else if (measurement_comments.toLowerCase().contains("equlibrium")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Equilibrium");
			} else if (measurement_comments.toLowerCase().contains("non-steady state")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Non-Steady State");
			} else if (measurement_comments.toLowerCase().contains("other")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Other");
			} else if (measurement_comments.toLowerCase().contains("unknown")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod, "Unknown");
			}

			// Lipid Percentage (using measurement_comments)
			if (!foundLipidPct) {
				Pattern pattern = Pattern.compile("lipid.*percentage", Pattern.CASE_INSENSITIVE);
				Pattern pattern2 = Pattern.compile("lipid.*content", Pattern.CASE_INSENSITIVE);
				Pattern pattern3 = Pattern.compile("%.*lipid", Pattern.CASE_INSENSITIVE);

				Pattern patternNumber = Pattern.compile("(\\d+\\.?\\d*)\\s*%");

				Matcher matcher = pattern.matcher(measurement_comments);
				Matcher matcher2 = pattern2.matcher(measurement_comments);
				Matcher matcher3 = pattern3.matcher(measurement_comments);

				Matcher matcherNumber = patternNumber.matcher(measurement_comments);

				if ((matcher.find() || matcher2.find() || matcher3.find()) && matcherNumber.find()) {
					ParameterValue pv = new ParameterValue();
					pv.parameter.name = ExperimentalConstants.expParamLipidPercent;
					pv.unit.abbreviation = ExperimentalConstants.str_dimensionless;
					double wc = Double.parseDouble(matcherNumber.group(1));
					pv.value_point_estimate = wc;
					er.parameter_values.add(pv);
					// er.experimental_parameters.put(ExperimentalConstants.expParamLipidPercent, matcherNumber.group(1));
				} else if (matcher.find() || matcher2.find() || matcher3.find()) {
					System.out.println("Matcher found for lipid percentage but no number found in: " + measurement_comments);
				}
			}
		}

		// Test Specificity (dry_wet)
		if (dry_wet != null && !dry_wet.trim().isEmpty()) {
			if (dry_wet.toLowerCase().contains("dry")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamWetDry, "Dry");
			} else if (dry_wet.toLowerCase().contains("wet")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamWetDry, "Wet");
			} else if (dry_wet.toLowerCase().contains("nc")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamWetDry, "Not Classified");
			} else if (dry_wet.toLowerCase().contains("nr")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamWetDry, "Not Reported");
			}
		}

		// Test Guideline (test_method and test_method_comments)
		if (test_method != null && !test_method.trim().isEmpty()) {
			String guideline;
			if (test_method.equals("NR")) {
				guideline = "Not reported";
			} else {
				guideline = test_method;
			}

			if (test_method_comments != null && !test_method_comments.trim().isEmpty()) {
				guideline += " - " + test_method_comments;
			}

			if (ExperimentalConstants.guidelineHashMap.containsKey(guideline)) {
				guideline = ExperimentalConstants.guidelineHashMap.get(guideline);
			}

			String normalizedGuideline = BCFUtilities.TestGuidelineFormatter.normalizeTestGuideline(guideline);
			if (normalizedGuideline != null && !normalizedGuideline.isEmpty()) {
				er.experimental_parameters.put(ExperimentalConstants.expParamGuideline, normalizedGuideline);
			}
			
			if (guideline.equals(ExperimentalConstants.guidelineOecd305)) {
				BCFUtilities.setOecd305Parameters(er);
			}
		}
		
		//TODO store t.test_radiolabel, r.additional_comments => calculation method = kinetic or conc
		//Maybe omit radiolabeled ones since have no way to know if they corrected for metabolites when
		//determining concentrations
		uc.convertRecord(er);

		return er;
	}


	private void setSpeciesParameters(ExperimentalRecord er) {
		er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin, latin_name);
		er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesCommon, common_name);

		String supercategory=getSpeciesSupercategory();
		if(supercategory!=null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, supercategory);	
		}
	}


	private void setChemicalIdentifiers(ExperimentalRecord er) {
		er.dsstox_substance_id=dtxsid;
		er.source_name=sourceName;
		String CAS1=cas_number.substring(0,cas_number.length()-3);
		String CAS2=cas_number.substring(cas_number.length()-3,cas_number.length()-1);
		String CAS3=cas_number.substring(cas_number.length()-1,cas_number.length());
		er.casrn=CAS1+"-"+CAS2+"-"+CAS3;
		
		er.chemical_name=ChemicalNameFixer.fixName(chemical_name);
		
		if(!er.chemical_name.equals(chemical_name)) {
			System.out.println("Fixed chemical name: \""+chemical_name+"\" to \""+er.chemical_name+"\"");
		}
		
	}


	private void setLiteratureSource(ExperimentalRecord er) {
		LiteratureSource ls=new LiteratureSource();
		er.literatureSource=ls;
		ls.name=author+" ("+publication_year+")";
		ls.author=author;
		ls.title=title;
		ls.year=publication_year;
		ls.citation=author+" ("+publication_year+"). "+title+"."+source;
		ls.doi=doi;
		
		er.reference=ls.citation;

	}


	private void setWaterConcentration(ExperimentalRecord er) {
		conc1_unit=conc1_unit.replace("/ml", "/mL");
		
		if(conc1_unit.contains("/g") ||  conc1_unit.contains("/acre") ||
				conc1_unit.contains("/kg")) 
			return;//not water concentration
		
		if(conc1_unit.equals("ug/d") || conc1_unit.equals("mg") || conc1_unit.equals("ng/d") ||
				conc1_unit.equals("mCi mg") || conc1_unit.equals("ppm diet") || conc1_unit.equals("cpm") ||
				conc1_unit.equals("ug") || conc1_unit.equals("ng") || conc1_unit.equals("uCi") || 
				conc1_unit.equals("ul") || conc1_unit.equals("NR") || conc1_unit.equals("mCi/mmol") ||
				conc1_unit.equals("Bq") || conc1_unit.equals("Bq/g")  || conc1_unit.equals("umol") || 
				conc1_unit.equals("g/ha") || conc1_unit.equals("AI g/ha")  ) {
			return;//not water concentration
		}
		
		ExperimentalRecord erWC=new ExperimentalRecord();
		erWC.property_name=ExperimentalConstants.strWaterSolubility;
		erWC.property_value_units_original=conc1_unit;
		if(conc1_mean!=null) erWC.property_value_point_estimate_original=conc1_mean;
		if(conc1_min!=null) erWC.property_value_min_original=conc1_min;
		if(conc1_max!=null) erWC.property_value_max_original=conc1_max;
		erWC.property_value_numeric_qualifier=conc1_mean_op;
		uc.convertRecord(erWC);
		
		if(er.keep) {			
			if(erWC.property_value_units_final==null || (!erWC.property_value_units_final.equals("g/L") && !erWC.property_value_units_final.equals("M"))) {
				conc1_units.add(conc1_unit);	
			}
		}
 		
		//TODO instead store "Water concentration (ug/L)"
				
		ParameterValue pv=new ParameterValue();
		pv.parameter.name="Water concentration";
		pv.unit.abbreviation=erWC.property_value_units_final;
		
		pv.value_point_estimate=erWC.property_value_point_estimate_final;
		pv.value_min=erWC.property_value_min_final;
		pv.value_max=erWC.property_value_max_final;
		
		if(conc1_mean_op!=null) {
			if(!conc1_mean_op.equals("~")) 			
				pv.value_qualifier=this.conc1_mean_op;	
		}
		
		if (pv.value_point_estimate != null || pv.value_min != null || pv.value_max != null) {
			er.parameter_values.add(pv);
		}
	}
}
