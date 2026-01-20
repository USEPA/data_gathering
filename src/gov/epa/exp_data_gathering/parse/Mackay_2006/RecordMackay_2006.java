package gov.epa.exp_data_gathering.parse.Mackay_2006;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.UnitConverter;

/**
* @author TMARTI02
*/
public class RecordMackay_2006 {
	
	String chemical_name;
	String common_name;
	String casrn;
	int volume;
	int line;
	int page;
	String section;
	int section_short;
	String property;
	String value;
	
	List<PropertyValue>values_dict;

	String annotation;
	
	int calculated;

	String media;
	String temperature;
	String ph;
	String method;
	String oc;
	String form;
	
	List<String>keyed_annotation;
	List<Paper>papers;
	List<String>matched_references;
	List<String>potential_references;
	
	String coefficient_type;
	Boolean is_average;
	
	transient UnitConverter uc = new UnitConverter("Data" + File.separator + "density.txt");
	transient static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	public static String sourceName="Mackay_2006";
	
	class PropertyValue {
		String operator;
		Double min;
		Double max;
		Double point_estimate;
	}
	
	class Paper {
		List<String>authors;
		String year;
		String sub;
		int num_authors;
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	
	ExperimentalRecord createExperimentalRecord() {

		ExperimentalRecord er = new ExperimentalRecord();
		er.property_name = ExperimentalConstants.strKOC;
//		er.chemical_name = Chemical_Name;//TODO
		er.synonyms = this.common_name;
		
		er.casrn = casrn;
//		er.dsstox_substance_id = this.DTXSID;
//		er.source_name = this.sourceName;
		
		
		PublicSource ps=new PublicSource();
		ps.name="Mackay, 2006";
		ps.description="Mackay 2006 Vols 1-4";
		ps.url="https://doi.org/10.1201/9781420044393";
		er.publicSource=ps;
		
		
		if (matched_references.size()>0) {
			
			LiteratureSource ls = new LiteratureSource();
			er.literatureSource=ls;
			ls.name= String.join("; ", keyed_annotation);
			ls.citation=String.join(";",matched_references);

			if(matched_references.size()!=keyed_annotation.size()) {
//				System.out.println("reference size mismatch: "+gson.toJson(this));
			}
			
		} else if (potential_references.size()>0) {
			LiteratureSource ls = new LiteratureSource();
			er.literatureSource=ls;
			ls.name="Potential match:"+String.join("; ", keyed_annotation);
			ls.citation="Potential match:"+String.join(";",matched_references );
		}
		

		er.experimental_parameters = new Hashtable<>();
		er.property_value_units_original = ExperimentalConstants.str_LOG_L_KG;
		
		er.parameter_values=new ArrayList<>();
		

		if (calculated==1) {
			er.keep = false;
			er.reason = "Estimated/Calculated";
		}

//		parseAnnotation(er);//TODO store the media and the experimental method
		
		er.experimental_parameters=new Hashtable<>();
		
		if(media!=null)		
			er.experimental_parameters.put("Media", media);//TODO
		
		if(method!=null)
			er.experimental_parameters.put("Measurement method", method);//TODO
		
		if(ph!=null) {
			er.pH = ph;
		}
		
		if(oc!=null) {
			System.out.println(oc);
		}
		
		
		if(temperature!=null) {
			try {
				er.temperature_C = Double.parseDouble(temperature);	

			} catch (Exception ex) {
				System.out.println("Parse error for temperature = "+temperature);
			}
		}
		
//		System.out.println(gson.toJson(this));
//		System.out.println(gson.toJson(er)+"\n");
//		//TODO add parameters:
//		//pH
//		//organicCarbon
//		//soil type

		return er;

	}


	//TODO update with code from python version or just omit Mackay_2006 source from the Java project
	public ExperimentalRecords toExperimentalRecords() {

		ExperimentalRecords ers = new ExperimentalRecords();
		
		
		for(PropertyValue pv:this.values_dict) {
			
			ExperimentalRecord er = createExperimentalRecord();
			
			if (pv.min!=null && pv.max!=null) {
				er.property_value_min_original=pv.min;
				er.property_value_max_original=pv.max;
			} else if (pv.point_estimate!=null) {
				er.property_value_point_estimate_original=pv.point_estimate;
			} else {
//				System.out.println(gson.toJson(this));
			}
			
			
			er.property_value_units_original = ExperimentalConstants.str_LOG_L_KG;
			er.property_name = ExperimentalConstants.strKOC;
			
			er.property_value_string = value;
			
			Gson gsonSimple=new Gson();
			er.property_value_string_parsed = gsonSimple.toJson(pv);
			
			er.property_value_min_original=pv.min;
			er.property_value_max_original=pv.max;
			er.property_value_numeric_qualifier=pv.operator;
			
			uc.convertRecord(er);
//			System.out.println(gson.toJson(er));
			
			ers.add(er);
		}
		

		return ers;
	}

}

