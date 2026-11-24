package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.WordUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.BCFUtilities;
import gov.epa.exp_data_gathering.parse.ChemicalNameFixer;
import gov.epa.exp_data_gathering.parse.ExcelSourceReader;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.ParseUtilities;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.TextUtilities;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.ghs_data_gathering.Utilities.Utilities;

public class RecordQSAR_ToolBox2 {

	public static String sourceName = "QSAR_Toolbox";
	static final transient UnitConverter unitConverter = new UnitConverter("data/density.txt");
	static Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeSpecialFloatingPointValues()
			.create();

	static final Pattern SYN_SPLIT = Pattern.compile("\\s*[;|]\\s*");
	static final Pattern DTXSID_PATTERN = Pattern.compile("DTXSID\\s*: ?\\s*(DTXSID\\d+)", Pattern.CASE_INSENSITIVE);

	static transient final Set<String> printedHandleMessages = new HashSet<>();
	static transient final Set<String> toms_other = new HashSet<>();
	static transient final Set<String> poms_other = new HashSet<>();

	public static boolean omitBasedOnGLPCompliance = false;

	static class Species {
		Integer id;
		String species_common;
		String species_scientific;
		String species_supercategory;
		String habitat;
	}

	public Integer Row;
	public String Chemical_Number;
	public String RecordNumber;
	public String CAS_Number;
	public String Chemical_name_s;
	public String SMILES;
	public String Molecular_formula;
	public String Predefined_substance_type;
	public String Additional_Ids;
	public String Identity;
	public String CAS_SMILES_relation;
	public String Comment;
	public String EndpointPath;
	public String Database;
	public String URL;
	public String Strain;
	public String Endpoint;
	public String Endpoint_other;
	public String Endpoint_type;
	public String Calculation_basis;
	public String Calculation_basis_other;
	public String Basis_for_the_BCF;
	public String Basis_for_the_BCF_other;
	public String Test_type;
	public String Conclusions;
	public String Reliability;
	public String Purpose_flag;
	public String GLP_compliance;
	public String Test_guideline;
	public String Study_result_type;
	public String Applied_transforms;
	public String Harmonized_Template;
	public String Qualifier_of_guideline;
	public String Route_of_administration;
	public String Test_organisms_species;
	public String Substance_Test_material_equality;
	public String APPLICANT_S_SUMMARY_AND_CONCLUSION_executive_summary;
	public String Assigned_SMILES;

	public String Year;
	public String Title;

	public String Strain_other;
	public String Test_type_other;
	public String Test_guideline_other;

	public String Type_of_inhalation_exposure;
	public String Route_of_administration_original;
	public String Type_of_coverage;
	public String Author_s_or_transferred_reference;

	public String Test_guideline_0;
	public String Test_guideline_1;
	public String Test_guideline_2;
	public String Test_guideline_3;
	public String Test_guideline_4;
	public String Test_guideline_5;
	public String Test_guideline_6;
	public String Test_guideline_7;

	public String Test_guideline_other_0;
	public String Test_guideline_other_1;
	public String Test_guideline_other_2;
	public String Test_guideline_other_3;
	public String Test_guideline_other_4;
	public String Test_guideline_other_5;
	public String Test_guideline_other_6;
	public String Test_guideline_other_7;

	public String Qualifier_of_guideline_0;
	public String Qualifier_of_guideline_1;
	public String Qualifier_of_guideline_2;
	public String Qualifier_of_guideline_3;
	public String Qualifier_of_guideline_4;
	public String Qualifier_of_guideline_5;
	public String Qualifier_of_guideline_6;
	public String Qualifier_of_guideline_7;

	public String Duration_MinValue;
	public String Duration_MeanValue;
	public String Duration_MaxValue;
	public String Duration_Unit;

	public String Oxygen_conditions;
	public String Details_on_results;

	public String Unit_details;
	public String Qualifier;
	public String Value_MeanValue;
	public String Value_Qualifier;
	public String Value_Unit;
	public String Value_Scale;
	public String Value_MinValue;
	public String Value_MinQualifier;
	public String Value_MaxValue;
	public String Value_MaxQualifier;
	public String Original_value_MeanValue;
	public String Original_value_Qualifier;
	public String Original_value_Unit;
	public String Original_value_Scale;
	public String Original_value_MinValue;
	public String Original_value_MinQualifier;
	public String Original_value_MaxValue;
	public String Original_value_MaxQualifier;

	public String Organ;
	public String Type_of_method;
	public String Assay;
	public String Assay_original;
	public String Interpretation_of_results;
	public String Interpretation_of_results_other;
	public String Assay_other;
	public String Route_Of_Challenge_Exposure;
	public String Route_Of_Induction_Exposure;
	public String Route_Of_Challenge_Exposure_other;
	public String Route_Of_Induction_Exposure_other;

	public String Author;
	public String Comments;
	public String Identity_in_file;
	public String Institution_and_country;
	public String Test_method_Data_source;
	public String Reference_source;
	public String Record_ID;
	public String Test_guideline_qualifier;
	public String Type_of_method_detail_if_other;
	public String Test_guideline_detail_if_other;
	public String Test_guideline_qualifier_detail;
	public String Test_organisms_species_detail_if_other;

	public String Type;
	public String Reliability_score;
	public String pH;
	public String Temperature;
	public String Statistics;
	public String Water_type;
	public String Superclass;
	public String Species_common_name;
	public String BCFss_lipid_MeanValue;
	public String BCFss_lipid_Unit;
	public String Exposure_concentration_MeanValue;
	public String Tissue_analyzed;

	public String PH_MeanValue;
	public String PH_Qualifier;
	public String PH_MinValue;
	public String PH_MaxValue;
	public String Temperature_MeanValue;
	public String Temperature_Qualifier;
	public String Temperature_Unit;

	public String Water_media_type;
	public String Water_media_type_other;
	public String Media;
	public String Media_other;
	public String Type_of_method_other;
	public String Principles_of_method_if_other_than_guideline;

	public String Org_carbon_MeanValue;
	public String Org_carbon_Qualifier;
	public String Org_carbon_MinValue;
	public String Org_carbon_MaxValue;

	public String Elimination_phase_duration_MeanValue;
	public String Elimination_phase_duration_Unit;
	public String Exposure_concentration_Unit;

	public String Lipid_MaxValue;
	public String Lipid_MeanValue;
	public String Lipid_MinValue;
	public String Lipid_Unit;

	public String Lipid_content_MaxValue;
	public String Lipid_content_MeanValue;
	public String Lipid_content_MinValue;
	public String Lipid_content_Unit;

	public String BCFk_lipid_BCFk_ww_lipid;
	public String BCFk_ww_k1_k2;
	public String BCFss_lipid_Scale;

	public String Reason_for_reliability_3_4;
	public String Route_of_exposure;
	public String Route_of_exposure_other;

	public String Any_other_information_on_results_incl_tables;
	public String Test_organisms_species_other;
	public String Type_of_inhalation_exposure_other;
	public String Route_of_administration_other;
	public String Type_of_coverage_other;
	public String Route_of_administration_other_original;

	public String lastUpdated;

	// Additional fields that are in the original records which may not be needed to
	// create ExperimentalRecord but need to keep so that original records json is
	// complete:

	public String Year_0;
	public String Year_1;
	public String Year_2;
	public String Year_3;
	public String Year_4;
	public String Year_5;
	public String Year_6;
	public String Year_7;
	public String Year_8;
	public String Year_9;
	public String Year_10;
	public String Year_11;
	public String Year_12;
	public String Year_13;
	public String Year_14;
	public String Year_15;
	public String Year_16;
	public String Year_17;
	public String Year_18;
	public String Year_19;

	public String Title_0;
	public String Title_1;
	public String Title_2;
	public String Title_3;
	public String Title_4;
	public String Title_5;
	public String Title_6;
	public String Title_7;
	public String Title_8;
	public String Title_9;
	public String Title_10;
	public String Title_11;
	public String Title_12;
	public String Title_13;
	public String Title_14;
	public String Title_15;
	public String Title_16;
	public String Title_17;
	public String Title_18;
	public String Title_19;

	public String Authors_or_transferred_reference;
	public String Author_s_or_transferred_reference_0;
	public String Author_s_or_transferred_reference_1;
	public String Author_s_or_transferred_reference_2;
	public String Author_s_or_transferred_reference_3;
	public String Author_s_or_transferred_reference_4;
	public String Author_s_or_transferred_reference_5;
	public String Author_s_or_transferred_reference_6;

	public String Bibliographic_source;
	public String Bibliographic_source_0;
	public String Bibliographic_source_1;
	public String Bibliographic_source_2;
	public String Bibliographic_source_3;
	public String Bibliographic_source_4;
	public String Bibliographic_source_5;
	public String Bibliographic_source_6;

	public String Year_20;
	public String Title_20;
	public String Bibliographic_source_20;
	public String Author_s_or_transferred_reference_20;

	public String Decomposition;
	public String TestMaterialIsNull;

	public String Duration_MaxQualifier;
	public String Duration_MinQualifier;
	public String Duration_Qualifier;

	public String Duration;

	public String Duration_of_test_contact_time_MeanValue;
	public String Duration_of_test_contact_time_Qualifier;
	public String Duration_of_test_contact_time_Unit;
	public String Duration_of_test_contact_time_Scale;
	public String Duration_of_test_contact_time_MinValue;
	public String Duration_of_test_contact_time_MinQualifier;
	public String Duration_of_test_contact_time_MaxValue;
	public String Duration_of_test_contact_time_MaxQualifier;
	public String Duration_Scale;

	public String Oxygen_conditions_other;

	// physchem properties
	public String PH_Scale;
	public String PH_MinQualifier;
	public String PH_MaxQualifier;

	public String Temperature_Scale;

	public String Atmospheric_pressure_MeanValue;
	public String Atmospheric_pressure_Qualifier;
	public String Atmospheric_pressure_Unit;
	public String Atmospheric_pressure_Scale;
	public String Atmospheric_pressure_MinValue;
	public String Atmospheric_pressure_MinQualifier;
	public String Atmospheric_pressure_MaxValue;
	public String Atmospheric_pressure_MaxQualifier;

	public String Atmospheric_Pressure_MeanValue;
	public String Atmospheric_Pressure_Qualifier;
	public String Atmospheric_Pressure_Unit;
	public String Atmospheric_Pressure_Scale;
	public String Atmospheric_Pressure_MinValue;
	public String Atmospheric_Pressure_MinQualifier;
	public String Atmospheric_Pressure_MaxValue;
	public String Atmospheric_Pressure_MaxQualifier;

	public String Org_carbon_Scale;
	public String Org_carbon_MinQualifier;
	public String Org_carbon_MaxQualifier;

	public String Elimination_phase_duration_Scale;
	public String Exposure_concentration_Scale;

	public String Lipid_Scale;
	public String Lipid_content_0_MaxQualifier;
	public String Lipid_content_0_MaxValue;
	public String Lipid_content_0_MeanValue;
	public String Lipid_content_0_MinQualifier;
	public String Lipid_content_0_MinValue;
	public String Lipid_content_0_Qualifier;
	public String Lipid_content_0_Scale;
	public String Lipid_content_0_Unit;
	public String Lipid_content_1_MeanValue;
	public String Lipid_content_1_Qualifier;
	public String Lipid_content_1_Scale;
	public String Lipid_content_1_Unit;
	public String Lipid_content_2_MeanValue;
	public String Lipid_content_2_Qualifier;
	public String Lipid_content_2_Scale;
	public String Lipid_content_2_Unit;
	public String Lipid_content_3_MeanValue;
	public String Lipid_content_3_Qualifier;
	public String Lipid_content_3_Scale;
	public String Lipid_content_3_Unit;
	public String Lipid_content_4_MeanValue;
	public String Lipid_content_4_Qualifier;
	public String Lipid_content_4_Scale;
	public String Lipid_content_4_Unit;
	public String Lipid_content_5_MeanValue;
	public String Lipid_content_5_Scale;
	public String Lipid_content_5_Unit;
	public String Lipid_content_6_MeanValue;
	public String Lipid_content_6_Scale;
	public String Lipid_content_6_Unit;
	public String Lipid_content_MaxQualifier;
	public String Lipid_content_MinQualifier;
	public String Lipid_content_Qualifier;
	public String Lipid_content_Scale;
	public String Lipid_method;

	public String Refference_ID;
	public String Sexual_maturation_offspring;

	public String Order;
	public String Phylum;
	public String Class;
	public String Division;
	public String Subclass;
	public String Subdivision;
	public String Subfamily;
	public String Subgenus;
	public String Subkingdom;
	public String Suborder;
	public String Subphylum;
	public String Superdivision;
	public String Superfamily;
	public String Superorder;
	public String Superphylum;
	public String Family;
	public String Genus;
	public String Infraclass;
	public String Infrakingdom;
	public String Infraorder;
	public String Infraphylum;
	public String Kingdom;

	public String T50_Elimination_MeanValue;
	public String T50_Elimination_Scale;
	public String T50_Elimination_Unit;
	public String Test_specificity;
	public String Tribe;
	public String Vehicle;

	public ExperimentalRecord toExperimentalRecord(ParseQSAR_ToolBox2 pqt,
			Hashtable<String, Species> htSpeciesByBinomialName,
			Hashtable<String, List<Species>> htSpeciesByCommonName) {

		ExperimentalRecord er = createBaseRecord();

		// TODO do they always have Database, Endpoint, and EndpointPath?

		if (Database == null) {
			er.keep = false;
			er.reason = "Database is missing";
			return er;
		}

		if (Endpoint == null) {
			er.keep = false;
			er.reason = "Endpoint is missing";
			return er;
		}

		if (EndpointPath == null) {
			er.keep = false;
			er.reason = "EndpointPath is missing";
			return er;
		}

		switch (EndpointPath) {
		case "Environmental Fate and Transport#Biodegradation#in water: screening tests":
			return new BiodegQSARMapper(pqt).map(this);

		case "Environmental Fate and Transport#Bioaccumulation#aquatic / sediment":
			return new BcfQSARMapper(htSpeciesByBinomialName, htSpeciesByCommonName).map(this);

		case "Ecotoxicological Information#Aquatic Toxicity":
			return new FishToxQSARMapper(htSpeciesByCommonName).map(this);

		case "Environmental Fate and Transport#Transport and Distribution#Adsorption/desorption":
			return new KocQSARMapper().map(this);

		case "Human Health Hazards#Sensitisation":
			return new SensitizationQSARMapper().map(this);

		case "Human Health Hazards#Acute Toxicity":
			return new AcuteToxicityQSARMapper(pqt).map(this);

		default:
			return new GenericQSARMapper(pqt).map(this);
		}

	}

	void addGenericMetadata(ExperimentalRecord er) {

		// Generic source/database info
		if (this.Database != null) {
			er.original_source_name = this.Database;
		}

		// Generic species info
		if (this.Test_organisms_species != null && !this.Test_organisms_species.isBlank()) {
			er.experimental_parameters.put("species", this.Test_organisms_species);
		}

		// Generic strain info
		if (this.Strain != null && !this.Strain.isBlank()) {
			er.experimental_parameters.put("Strain", this.Strain);
		}

		// GLP compliance
		if (this.GLP_compliance != null && !this.GLP_compliance.isBlank()) {
			er.experimental_parameters.put("Test guideline compliance", this.GLP_compliance);
			if (omitBasedOnGLPCompliance && !this.GLP_compliance.startsWith("yes")) {
				er.keep = false;
				er.updateReason("GLP compliance = " + this.GLP_compliance);
			}
		}

		// Generic results/details field
		if (this.Details_on_results != null && !this.Details_on_results.isBlank()) {
			er.experimental_parameters.put("Biodegradation records", this.Details_on_results);
		}

		// Generic media type
		if (this.Media != null && !this.Media.isBlank() && !this.Media.equalsIgnoreCase("not specified")) {
			String media = this.Media;
			if (media.equalsIgnoreCase("fresh water")) {
				media = "freshwater";
			} else if (media.equalsIgnoreCase("natural water: freshwater")) {
				media = "freshwater";
			} else if (media.equalsIgnoreCase("natural water: marine")) {
				media = "marine";
			}
			er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, media);
		}

		// Generic measurement method from Endpoint if needed
		if (this.Endpoint != null && !this.Endpoint.isBlank()) {
			er.experimental_parameters.put(ExperimentalConstants.expParamMeasurementMethod,
					WordUtils.capitalizeFully(this.Endpoint));
		}

		// Generic test location / exposure type
		if (this.Test_type != null && !this.Test_type.isBlank()) {
			if (this.Test_type.equalsIgnoreCase("Field Study")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamTestLocation, "Field");
			} else {
				er.experimental_parameters.put(ExperimentalConstants.expParamTestLocation, "Lab");
				if (this.Test_type.toLowerCase().contains("semi")) {
					er.experimental_parameters.put(ExperimentalConstants.expParamExposureType, "Renewal");
				} else {
					er.experimental_parameters.put(ExperimentalConstants.expParamExposureType,
							WordUtils.capitalizeFully(this.Test_type));
				}
			}
		}
	}

	void logUniqueHandle(String message) {
		if (message == null || message.isBlank())
			return;
		if (printedHandleMessages.add(message)) {
			System.out.println(message);
		}
	}

	static String parseDtxsid(String input) {
		if (input == null || input.isBlank())
			return null;
		Matcher matcher = DTXSID_PATTERN.matcher(input);
		return matcher.find() ? matcher.group(1) : null;
	}

	ExperimentalRecord createBaseRecord() {
		ExperimentalRecord er = new ExperimentalRecord();
		er.parameter_values = new ArrayList<>();
		er.experimental_parameters = new HashMap<>();
		setSourceInformation(er);
		setIdentifiers(er);
		return er;
	}

	void setSourceInformation(ExperimentalRecord er) {
		er.source_name = sourceName;
		er.original_source_name = Database;
		er.url = this.URL;
	}

	boolean hasNoLettersAscii(String s) {
		return s != null && s.matches("[^A-Za-z]*");
	}

	void setIdentifiers(ExperimentalRecord er) {
		if (this.CAS_Number != null && !this.CAS_Number.equals("No CAS number")
				&& !this.CAS_Number.contains("Missing")) {
			er.casrn = this.CAS_Number;
			if (er.casrn.contains("Invalid"))
				er.casrn = null;
		}

		List<String> names = null;
		if (this.Chemical_name_s != null && !this.Chemical_name_s.isBlank()) {
			this.Chemical_name_s = ChemicalNameFixer.replaceCommonHtmlEntities(this.Chemical_name_s);
			names = SYN_SPLIT.splitAsStream(this.Chemical_name_s.trim()).map(String::trim).filter(s -> !s.isEmpty())
					.map(s -> s.replace('_', ' ')).map(s -> s.replaceAll("\\s+", " ")).map(String::trim)
					.filter(s -> !s.equals(".")).collect(Collectors.toCollection(LinkedHashSet::new)).stream()
					.collect(Collectors.toList());
		}

		er.synonyms = (names == null || names.isEmpty()) ? null : String.join("; ", names);

		if (names != null && !names.isEmpty()) {
			for (String name : names) {
				if (ParseUtilities.isValidCAS(name) && er.casrn == null) {
					er.casrn = ParseUtilities.fixCASLeadingZero(name);
					continue;
				}
				if (hasNoLettersAscii(name))
					continue;
				er.chemical_name = ChemicalNameFixer.fixName(name);
				break;
			}
		}

		if (this.Additional_Ids != null && this.Additional_Ids.contains("DTXSID :")) {
			er.dsstox_substance_id = parseDtxsid(Additional_Ids);
		}

		er.smiles = this.SMILES;
		if (er.smiles != null && er.smiles.contains("missing"))
			er.smiles = null;

		if (er.casrn == null && er.chemical_name == null && er.smiles == null) {
			er.keep = false;
			er.updateReason("No chemical identifiers");
		}
	}

	void setGuidelines(ExperimentalRecord er) {
		List<String> guidelines = new ArrayList<>();
		List<String> guidelineQualifiers = new ArrayList<>();

		if (this.Test_guideline != null) {
			guidelines.add(this.Test_guideline);
			guidelineQualifiers.add(this.Qualifier_of_guideline);
		}
		if (this.Test_guideline_0 != null) {
			guidelines.add(this.Test_guideline_0);
			guidelineQualifiers.add(this.Qualifier_of_guideline_0);
		}
		if (this.Test_guideline_1 != null) {
			guidelines.add(this.Test_guideline_1);
			guidelineQualifiers.add(this.Qualifier_of_guideline_1);
		}
		if (this.Test_guideline_2 != null) {
			guidelines.add(this.Test_guideline_2);
			guidelineQualifiers.add(this.Qualifier_of_guideline_2);
		}
		if (this.Test_guideline_3 != null) {
			guidelines.add(this.Test_guideline_3);
			guidelineQualifiers.add(this.Qualifier_of_guideline_3);
		}
		if (this.Test_guideline_4 != null) {
			guidelines.add(this.Test_guideline_4);
			guidelineQualifiers.add(this.Qualifier_of_guideline_4);
		}
		if (this.Test_guideline_5 != null) {
			guidelines.add(this.Test_guideline_5);
			guidelineQualifiers.add(this.Qualifier_of_guideline_5);
		}
		if (this.Test_guideline_6 != null) {
			guidelines.add(this.Test_guideline_6);
			guidelineQualifiers.add(this.Qualifier_of_guideline_6);
		}
		if (this.Test_guideline_7 != null) {
			guidelines.add(this.Test_guideline_7);
			guidelineQualifiers.add(this.Qualifier_of_guideline_7);
		}
		if (this.Test_guideline_other != null) {
			guidelines.add(this.Test_guideline_other);
			guidelineQualifiers.add("");
		}

		String strGuidelinesQualifiers = "";
		for (int g = 0; g < guidelines.size(); g++) {
			if (g > 0)
				strGuidelinesQualifiers += "; ";
			if (guidelineQualifiers.get(g) != null && guidelineQualifiers.get(g).trim().length() > 0) {
				strGuidelinesQualifiers += guidelineQualifiers.get(g) + " ";
			}
			strGuidelinesQualifiers += guidelines.get(g);
		}

		if (strGuidelinesQualifiers != null && strGuidelinesQualifiers.length() > 0) {
			String guideline = BCFUtilities.TestGuidelineFormatter.normalizeTestGuideline(strGuidelinesQualifiers);
			if (guideline != null && guideline.length() > 0) {
				er.experimental_parameters.put(ExperimentalConstants.expParamGuideline, guideline);
			}
		}
	}

	void setMeasurementMethod(ExperimentalRecord er) {

		String guidelines = "";
		if (er.experimental_parameters.get(ExperimentalConstants.expParamGuideline) != null) {
			guidelines = er.experimental_parameters.get(ExperimentalConstants.expParamGuideline).toString();
		}

		if (Media != null && !Media.isEmpty() && !Media.equalsIgnoreCase("not specified")) {
			if (Media.equalsIgnoreCase("fresh water")) {
				Media = "freshwater";
			} else if (Media.equalsIgnoreCase("natural water: freshwater")) {
				Media = "freshwater";
			} else if (Media.equalsIgnoreCase("natural water: marine")) {
				Media = "marine";
			}
			er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, Media);
		}

		if (Type_of_method != null) {
			String tom = Type_of_method.toLowerCase();

			if (tom.contains("other") && Type_of_method_other != null) {
				String tom2 = Type_of_method_other.toLowerCase();
				if (!tom2.contains("not ") && !tom2.contains("no ")) {
					er.measurement_method = Type_of_method_other;
				}
			} else if (!tom.contains("other") && !tom.contains("not ") && !tom.contains("no ")) {
				er.measurement_method = Type_of_method;
			}
		} else if (this.Endpoint != null) {
			er.measurement_method = this.Endpoint;
			if (this.Endpoint.equals("Other Endpoint")) {
				er.measurement_method = this.Endpoint_other;
			}
		}

		if (Principles_of_method_if_other_than_guideline != null) {
			poms_other.add(Principles_of_method_if_other_than_guideline);
			er.experimental_parameters.put("Principles_of_method_if_other_than_guideline",
					Principles_of_method_if_other_than_guideline);
		}

		if (Type_of_method_other != null) {
			er.experimental_parameters.put("Type_of_method_other", Type_of_method_other);
			toms_other.add(Type_of_method_other);
		}

		if (er.measurement_method == null) {
			if (guidelines.contains("Guideline 121") || guidelines.contains("Method C.19")
					|| guidelines.contains("94.75") || guidelines.contains("HPLC")) {
				er.measurement_method = "HPLC Estimation Method";
			} else if (guidelines.contains("Guideline 106") || guidelines.contains("Method C.18")
					|| guidelines.contains("796.2750") || guidelines.contains("835.1220")
					|| guidelines.contains("835.1110")) {
				er.measurement_method = "Batch Equilibrium Method";
			}
		}
	}

	boolean hasString(List<String> examples, String str) {
		for (String example : examples) {
			if (str.contains(example)) {
				return true;
			}
		}
		return false;
	}

	void setPropertyValues(ExperimentalRecord er, boolean checkQualifier) {

		Value_MeanValue = TextUtilities.fixMojibake(Value_MeanValue);
		Value_MinValue = TextUtilities.fixMojibake(Value_MinValue);
		Value_MaxValue = TextUtilities.fixMojibake(Value_MaxValue);

//		Qualifier = TextUtilities.fixMojibake(Qualifier);

		String infStr = "\u221E";

		if (this.Value_MinValue != null && this.Original_value_MaxValue != null) {

			if (Value_MinValue.contains(infStr) || Value_MaxValue.contains(infStr)) {
				er.keep = false;
				er.updateReason("Property value is infinite");
				return;
			}

			try {
				er.property_value_min_original = Double.parseDouble(this.Value_MinValue);
			} catch (Exception ex) {
				er.keep = false;
				er.reason = "Could not parse min value";
				return;
			}
			try {
				er.property_value_max_original = Double.parseDouble(this.Value_MaxValue);
			} catch (Exception ex) {
				er.keep = false;
				er.reason = "Could not parse max value";
				return;
			}

			er.property_value_string = Parse.formatValue(er.property_value_min_original) + " - "
					+ Parse.formatValue(er.property_value_max_original);

		} else if (this.Value_MinValue != null) {

			if (Value_MinValue.contains(infStr)) {
				er.keep = false;
				er.updateReason("Property value is infinite");
				return;
			}

			er.property_value_min_original = Double.parseDouble(this.Value_MinValue);
			er.property_value_string = "> " + Parse.formatValue(er.property_value_min_original);

		} else if (this.Value_MaxValue != null) {

			if (Value_MaxValue.contains(infStr)) {
				er.keep = false;
				er.updateReason("Property value is infinite");
				return;
			}

			er.property_value_max_original = Double.parseDouble(this.Value_MaxValue);
			er.property_value_string = "< " + Parse.formatValue(er.property_value_max_original);

		} else if (this.Value_MeanValue != null) {

			if (Value_MeanValue.contains(infStr)) {
				er.keep = false;
				er.reason = "Infinite property value";
				return;
			}

			if (Value_MeanValue.contains("NaN")) {
				er.keep = false;
				er.reason = "NaN value";
				return;
			}

			if (Value_MeanValue.contains("not specified")) {
				er.keep = false;
				er.reason = "Property value is not specified";
				return;
			}

			try {
				er.property_value_point_estimate_original = Double.parseDouble(this.Value_MeanValue);
			} catch (Exception ex) {
				er.keep = false;
				er.reason = "Could not parse Value_MeanValue";
				return;
			}

			er.property_value_string = "";

			if (this.Qualifier != null && checkQualifier) {
				if (Qualifier.contentEquals("No qualifier")) {
					er.property_value_numeric_qualifier = null;
				} else if (Qualifier.equals("ca.")) {
					er.property_value_numeric_qualifier = "~";
				} else if (Qualifier.contentEquals("?")) {
					er.property_value_numeric_qualifier = "?";
					er.keep = false;
					er.reason = "Unknown numeric qualifier";
				} else if (Qualifier.contentEquals(">")) {
					er.property_value_numeric_qualifier = ">";
				} else if (Qualifier.contentEquals(">=")) {
					er.property_value_numeric_qualifier = ">=";
				} else if (Qualifier.contentEquals("<=")) {
					er.property_value_numeric_qualifier = "<=";
				} else if (Qualifier.contentEquals("<")) {
					er.property_value_numeric_qualifier = "<";
				} else if (Qualifier.contentEquals("\u2264")) {
					er.property_value_numeric_qualifier = "\u2264";
				} else if (Qualifier.contentEquals("\u2265")) {
					er.property_value_numeric_qualifier = "\u2265";
				} else {
					System.out.println("Row " + this.Row + ", Unhandled qualifier:\t" + this.Qualifier);
				}

				if (er.property_value_numeric_qualifier != null && er.property_value_numeric_qualifier.length() > 0) {
					er.property_value_string += er.property_value_numeric_qualifier + " ";
				}
			}

			er.property_value_string += Parse.formatValue(er.property_value_point_estimate_original);
		}

		if (er.property_value_string != null && er.property_value_units_original != null) {
			er.property_value_string += " " + er.property_value_units_original;
		}

		if (er.property_value_string == null) {
			er.keep = false;
			er.reason = "No property_value_string";
		}
	}

	void setExperimentalParameters(ExperimentalRecord er) {

		if (PH_MeanValue != null) {
			double dbl_pH = Double.parseDouble(PH_MeanValue);
			if (dbl_pH <= 14) {
				ParameterValue pv = new ParameterValue();
				pv.parameter.name = ExperimentalConstants.expParamPh;
				pv.unit.abbreviation = ExperimentalConstants.str_LOG_UNITS;
				pv.value_point_estimate = dbl_pH;
				if (PH_Qualifier != null) {
					pv.value_qualifier = PH_Qualifier.replace("ca.", "~");
				}
				er.parameter_values.add(pv);
			}
		} else if (PH_MaxValue != null || PH_MinValue != null) {
			ParameterValue pv = new ParameterValue();
			pv.parameter.name = ExperimentalConstants.expParamPh;
			pv.unit.abbreviation = ExperimentalConstants.str_LOG_UNITS;
			if (PH_MinValue != null)
				pv.value_min = Double.parseDouble(PH_MinValue);
			if (PH_MaxValue != null)
				pv.value_max = Double.parseDouble(PH_MaxValue);
			if (PH_Qualifier != null)
				pv.value_qualifier = PH_Qualifier.replace("ca.", "~");
			er.parameter_values.add(pv);
		} else if (pH != null && !pH.isEmpty()) {
			ParameterValue pv = new ParameterValue();
			pv.parameter.name = ExperimentalConstants.expParamPh;
			pv.unit.abbreviation = ExperimentalConstants.str_LOG_UNITS;

			if (pH.contains("-")) {
				String[] pHRange = pH.split("-");
				pv.value_min = Double.parseDouble(pHRange[0]);
				pv.value_max = Double.parseDouble(pHRange[1]);
			} else {
				Pattern phPattern = Pattern.compile("\\d+");
				Matcher phMatcher = phPattern.matcher(pH);
				if (phMatcher.find()) {
					pv.value_point_estimate = Double.parseDouble(phMatcher.group());
				}
			}

			if (PH_Qualifier != null) {
				pv.value_qualifier = PH_Qualifier.replace("ca.", "~");
			}

			if (pv.value_point_estimate != null || pv.value_min != null || pv.value_max != null) {
				er.parameter_values.add(pv);
			}
		}

		if (Temperature_MeanValue != null) {
			String temperatureMean = null;
			if (Temperature_Unit != null) {
				if (Temperature_Unit.contains("C")) {
					temperatureMean = Temperature_MeanValue;
				} else if (Temperature_Unit.contains("F")) {
					temperatureMean = String.valueOf(5 / 9 * (Double.parseDouble(Temperature_MeanValue) - 32));
				} else if (Temperature_Unit.equals("K")) {
					temperatureMean = String.valueOf(Double.parseDouble(Temperature_MeanValue) - 273.15);
				} else {
					logUniqueHandle("Handle temp unit=" + Temperature_Unit);
				}
			} else {
				temperatureMean = Temperature_MeanValue;
				er.updateNote("°C assumed for temperature");
			}

			if (temperatureMean != null && !temperatureMean.isEmpty()) {
				ParameterValue pv = new ParameterValue();
				pv.parameter.name = ExperimentalConstants.expParamTemperature;
				pv.unit.abbreviation = "C";
				Pattern tempPattern = Pattern.compile("\\d+");
				Matcher tempMatcher = tempPattern.matcher(temperatureMean);
				if (tempMatcher.find()) {
					pv.value_point_estimate = Double.parseDouble(tempMatcher.group());
				}
				if (pv.value_point_estimate != null) {
					er.parameter_values.add(pv);
				}
			}
		} else if (Temperature != null && !Temperature.isEmpty()) {
			ParameterValue pv = new ParameterValue();
			pv.parameter.name = ExperimentalConstants.expParamTemperature;
			pv.unit.abbreviation = "C";
			Pattern tempPattern = Pattern.compile("\\d+");
			Matcher tempMatcher = tempPattern.matcher(Temperature);
			if (tempMatcher.find()) {
				pv.value_point_estimate = Double.parseDouble(tempMatcher.group());
			}
			if (pv.value_point_estimate != null) {
				er.parameter_values.add(pv);
			}
			er.updateNote("°C assumed for temperature");
		}
	}

	public static Vector<JsonObject> parseQSAR_ToolBoxRecordsFromExcel(String fileName, String sourceName) {
		ExcelSourceReader esr = new ExcelSourceReader(fileName, sourceName);
		esr.headerRowNum = 2;
		return esr.parseRecordsFromExcel(2);
	}

	public static List<JsonObject> parseQSAR_ToolBoxRecordsFromTextFile(String fileName, String sourceName) {
		String folder = "data\\experimental\\" + sourceName + "\\text files\\";
		String filePath = folder + fileName;

		Field[] fields = RecordQSAR_ToolBox2.class.getDeclaredFields();
		Set<String> classFieldNames = new HashSet<>();
		for (Field field : fields)
			classFieldNames.add(field.getName());

		try (FileInputStream fis = new FileInputStream(filePath);
				InputStreamReader isr = new InputStreamReader(fis, ExcelSourceReader.encoding);
				BufferedReader br = new BufferedReader(isr)) {

			String headerLine = br.readLine();
			LinkedList<String> headersOriginal = Utilities.Parse3(headerLine, "\t");
			List<String> headers = new ArrayList<>();

			for (String header : headersOriginal) {
				if (header.equals("#"))
					header = "Chemical_Number";
				if (header.equals("Interpretation of results"))
					header = "Interpretation_Of_Results";
				if (header.equals("Interpretation of results other"))
					header = "Interpretation_Of_Results_other";
				if (!header.isEmpty() && Character.isDigit(header.charAt(0))) {
					switch (header.charAt(0)) {
					case '0':
						header = "Zero" + header.substring(1);
						break;
					case '1':
						header = "One" + header.substring(1);
						break;
					case '2':
						header = "Two" + header.substring(1);
						break;
					case '3':
						header = "Three" + header.substring(1);
						break;
					case '4':
						header = "Four" + header.substring(1);
						break;
					case '5':
						header = "Five" + header.substring(1);
						break;
					case '6':
						header = "Six" + header.substring(1);
						break;
					case '7':
						header = "Seven" + header.substring(1);
						break;
					case '8':
						header = "Eight" + header.substring(1);
						break;
					case '9':
						header = "Nine" + header.substring(1);
						break;
					}
				}
				header = header.trim().replaceAll("[^\\p{Alnum}]+", "_").replaceAll("^_", "").replaceAll("_$", "");
				headers.add(header);
			}

			List<JsonObject> records = new ArrayList<>();
			while (true) {
				String line = br.readLine();
				if (line == null)
					break;
				LinkedList<String> values = Utilities.Parse3(line, "\t");
				JsonObject jo = new JsonObject();
				for (int j = 0; j < values.size(); j++) {
					String header = headers.get(j);
					String value = values.get(j);
					if (!value.isBlank())
						jo.addProperty(header, value);
				}
				records.add(jo);
			}
			return records;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Hashtable<String, List<Species>> createSupercategoryHashtable(Connection conn) {
		Hashtable<String, List<Species>> htSpecies = new Hashtable<>();
		String sql = "select species_id, species_common, species_scientific, species_supercategory, habitat from species";

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				Species species = new Species();
				species.id = rs.getInt(1);
				species.species_common = rs.getString(2);
				species.species_scientific = rs.getString(3);
				species.species_supercategory = rs.getString(4);
				species.habitat = rs.getString(5);

				htSpecies.computeIfAbsent(species.species_common, k -> new ArrayList<>()).add(species);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return htSpecies;
	}
}