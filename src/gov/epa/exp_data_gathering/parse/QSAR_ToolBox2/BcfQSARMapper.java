package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.events.EndDocument;

import org.apache.commons.text.WordUtils;

import com.google.gson.JsonObject;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.BCFUtilities;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.TextUtilities;
import gov.epa.exp_data_gathering.parse.UnitConverter;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox2.RecordQSAR_ToolBox2.Species;

public class BcfQSARMapper implements QSARRecordMapper {

	private final Hashtable<String, RecordQSAR_ToolBox2.Species> htSpeciesByBinomialName;
	private final Hashtable<String, List<RecordQSAR_ToolBox2.Species>> htSpeciesByCommonName;

	private static final transient UnitConverter unitConverter = RecordQSAR_ToolBox2.unitConverter;

	public BcfQSARMapper(Hashtable<String, RecordQSAR_ToolBox2.Species> htSpeciesByBinomialName,
			Hashtable<String, List<RecordQSAR_ToolBox2.Species>> htSpeciesByCommonName) {
		this.htSpeciesByBinomialName = htSpeciesByBinomialName;
		this.htSpeciesByCommonName = htSpeciesByCommonName;
	}

	@Override
	public ExperimentalRecord map(RecordQSAR_ToolBox2 row) {

		ExperimentalRecord er = row.createBaseRecord();

		if (row.Database == null) {
			er.keep = false;
			er.reason = "Database is missing";
			return er;
		}

		if (!isSupportedBcfDatabase(row.Database)) {
			er.keep = false;
			er.reason = "Unsupported BCF database: " + row.Database;
			return er;
		}

		setPropertyName(row, er);		
		if(er.property_name==null) {
			er.keep=false;
			er.reason="Invalid property:"+row.Endpoint;
			return er;
		}

		addExperimentalParameters(row, er);
		applyBcfCommonSetup(row, er, htSpeciesByBinomialName, htSpeciesByCommonName);

		return er;
	}

	private void setPropertyName(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if(row.Endpoint.equals("BCF") || row.Endpoint.equals("BCFss")) {
			er.property_name=ExperimentalConstants.strBCF;
		} else if(row.Endpoint.equals("Other Endpoint")) {			
			row.Endpoint=row.Endpoint_other;
			List<String>okEndpoints=Arrays.asList("BCF (average)",
					"lipid BCF (average)","BCFSSL","log BCF","BCFss",
					"LogBCF","BCF steady state normalised to 5% lipid content");

			if(okEndpoints.contains(row.Endpoint_other)) {
				er.property_name=ExperimentalConstants.strBCF;
			} 

		} else if(row.Endpoint.equals("BMF") || row.Endpoint.equals("BSAF") || 
				row.Endpoint.equals("Other") || row.Endpoint.equals("BAF")) {			
			//TODO
		} else {
			System.out.println("Handle "+row.Endpoint);
		}
	}


	private boolean isSupportedBcfDatabase(String db) {
		return "Bioaccumulation fish CEFIC LRI".equals(db)
				|| "Bioaccumulation Canada".equals(db)
				|| "Bioconcentration and logKow NITE".equals(db)
				|| "ECHA REACH".equals(db);
	}


	// -------------------------------------------------------------------------
	// Main BCF setup
	// -------------------------------------------------------------------------

	private void applyBcfCommonSetup(RecordQSAR_ToolBox2 row, ExperimentalRecord er,
			Hashtable<String, RecordQSAR_ToolBox2.Species> htSpeciesByBinomialName,
			Hashtable<String, List<RecordQSAR_ToolBox2.Species>> htSpeciesByCommonName) {


		er.property_category = "bioconcentration";
		setPropertyValuesBcf(row, er);
		applyBcfUnitsAndConvert(row, er);

		if (!er.hasNumericFinalValue() && er.keep) {
			er.keep = false;
			er.reason="No numeric final value";
		}
	}

	private String standardizeSupercategory(RecordQSAR_ToolBox2 row, RecordQSAR_ToolBox2.Species species) {
		if (species.species_supercategory == null) {
			return null;
		}

		if (species.species_supercategory.contains("fish")) {
			return "Fish";
		} else if (species.species_supercategory.contains("algae")) {
			return "Algae";
		} else if (species.species_supercategory.contains("crustaceans")) {
			return "Crustaceans";
		} else if (species.species_supercategory.contains("insects/spiders")) {
			return "Insects/spiders";
		} else if (species.species_supercategory.contains("molluscs")) {
			return "Molluscs";
		} else if (species.species_supercategory.contains("worms")) {
			return "Worms";
		} else if (species.species_supercategory.contains("invertebrates")) {
			return "Invertebrates";
		} else if (species.species_supercategory.contains("flowers, trees, shrubs, ferns")) {
			return "Flowers, trees, shrubs, ferns";
		} else if (species.species_supercategory.contains("microorganisms")) {
			return "Microorganisms";
		} else if (species.species_supercategory.contains("amphibians")) {
			return "Amphibians";
		} else if (species.species_supercategory.equals("omit")) {
			return "Omit";
		} else if (species.species_supercategory.equals("not specified")) {
			return "not specified";
		} else {
			row.logUniqueHandle("Handle\t" + row.Species_common_name + "\t" + species.species_supercategory);
			return null;
		}
	}

	private void setSpeciesParameters(RecordQSAR_ToolBox2 row,
			Hashtable<String, RecordQSAR_ToolBox2.Species> htSpeciesByBinomialName, ExperimentalRecord er) {

		
		if (row.Test_organisms_species != null) {
			
			if (row.Test_organisms_species.equalsIgnoreCase("Other Test Organisms (species)")) {
				if(row.Test_organisms_species_other.toLowerCase().contains("fish")) {
					er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, "Fish");
					er.updateNote("Test_organisms_species_other="+row.Test_organisms_species_other);
					return;
				} else {
//					System.out.println(row.Test_organisms_species_other);
					row.Test_organisms_species=row.Test_organisms_species_other;
//					RecordQSAR_ToolBox2.Species species = htSpeciesByBinomialName
//							.get(row.Test_organisms_species.toLowerCase());
//					if(species!=null) {
//						System.out.println(species.species_scientific+"\t"+ species.species_supercategory);
//					}
				}
			}
			
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin, row.Test_organisms_species);
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesCommon, row.Species_common_name);

			
			if (htSpeciesByBinomialName.containsKey(row.Test_organisms_species.toLowerCase())) {
				RecordQSAR_ToolBox2.Species species = htSpeciesByBinomialName
						.get(row.Test_organisms_species.toLowerCase());

				if (species.species_common != null) {
					er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesCommon, species.species_common);
				}

				if (species.species_scientific != null) {
					er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin,
							species.species_scientific);
				}

				if (species.species_supercategory != null) {
					String supercategory = standardizeSupercategory(row, species);
					if (supercategory == null || supercategory.equals("not specified")) {
						er.keep = false;
						er.updateReason("unspecified supercategory");
					}
					er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, supercategory);
				} else {
					er.keep = false;
					er.updateReason("Couldn't set species supercategory");
				}

			} else {

				if (row.Test_organisms_species.equals("Gnathopogon Coerulescens")
						|| row.Test_organisms_species.equals("Ictalurus Nebulosus")
						|| row.Test_organisms_species.equals("Chasmichthys Gulosus")) {
					er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, "Fish");

				} else if (row.Test_organisms_species.equals("Asellus Brevicaudus")
						|| row.Test_organisms_species.equals("Porcellus Scaber")
						|| row.Test_organisms_species.equals("Paratya Compressa Compressa")) {
					er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, "Crustaceans");

				} else {
					er.keep = false;
					er.updateReason("Test_organisms_species not in species ht");
				}
			}

		} else {
			er.keep = false;
			er.updateReason("Test_organisms_species is missing");

		}
	}

	private void applyBcfUnitsAndConvert(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Value_Unit == null) {
			er.keep = false;
			er.updateReason("Units are null");
			return;
		}

		if (row.Value_Unit.equals("log(L/kg)")) {
			er.property_value_units_original = ExperimentalConstants.str_LOG_L_KG;
		} else if (row.Value_Unit.equals("dimensionless") || row.Value_Unit.equals("whole body w.w.")
				|| row.Value_Unit.equals("%") || row.Value_Unit.equals("BCF: bioconcentration factor")
				|| row.Value_Unit.equals("L/Kg ww")) {
			er.property_value_units_original = ExperimentalConstants.str_L_KG;
		} else if (row.Value_Unit.toLowerCase().equals("folds") || row.Value_Unit.toLowerCase().equals("x")
				|| row.Value_Unit.toLowerCase().equals("kg/d") || row.Value_Unit.toLowerCase().contains("conc.")) {
			er.keep = false;
			er.reason = "Units are invalid";
			return;
		} else {
			// keep original units and let converter attempt to handle/reject
			er.property_value_units_original = row.Value_Unit;
		}

		er.property_value_string+=" "+er.property_value_units_original;
		
		if (er.property_name != null) {
			unitConverter.convertRecord(er);
		}
	}


	private void addExperimentalParameters(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		setObservationDuration(row, er);
		addParameterMediaType(row, er);

		addParametersTestLocationAndExposureType(row, er);		
		addParameterReliability(row, er);
		addParameterLipidContent(row, er);

		addParameterTestGuideline(row, er);
		
		if (row.GLP_compliance != null) {
			er.experimental_parameters.put("Test guideline compliance", row.GLP_compliance);
			if (RecordQSAR_ToolBox2.omitBasedOnGLPCompliance && !row.GLP_compliance.startsWith("yes")) {
				er.keep = false;
				er.reason = "GLP compliance = " + row.GLP_compliance;
			}
		}

		setSpeciesParameters(row, htSpeciesByBinomialName, er);
		addParameterResponseSite(row, er);
		addParameterWaterConcentration(row, er);
		
		setCalculationMethod(row, er);

		if (row.Statistics != null) {
			er.experimental_parameters.put("Statistics", row.Statistics);
		}

		// Database-specific additions
		addExperimentalParametersByDatabase(row, er);


	}

	private void setCalculationMethod(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		String paramName = ExperimentalConstants.expParamCalculationMethod;

		if(row.Calculation_basis!=null) {
			if(row.Calculation_basis.equals("Other Calculation basis")) {
				row.Calculation_basis=row.Calculation_basis_other;
			}
			
			if(row.Calculation_basis.toLowerCase().contains("estimate") || 
					row.Calculation_basis.toLowerCase().contains("regression-derived equation")) {
				er.keep=false;
				er.updateReason("Calculation_basis indicates record is estimated: "+ row.Calculation_basis);
			}
			
			er.experimental_parameters.put(paramName, row.Calculation_basis);
//			System.out.println(row.Calculation_basis);
			return;
		}
		
		if (row.Endpoint != null) {
			if(row.Endpoint.equals("BCFss")) {
				er.experimental_parameters.put(paramName, "Steady State");
//				System.out.println(er.casrn+"\tSteady State");
			}
		}

		String guidelines = "";
		if (er.experimental_parameters.get(ExperimentalConstants.expParamGuideline) != null) {
			guidelines = er.experimental_parameters.get(ExperimentalConstants.expParamGuideline).toString();
		}
		
		//TODO should we set calculation method based on test guideline for BCF?

	}

	private void addParameterResponseSite(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		String tissueType = null;
		if (row.Basis_for_the_BCF != null && !row.Basis_for_the_BCF.isEmpty()
				&& !row.Basis_for_the_BCF.toLowerCase().contains("other")) {
			tissueType = row.Basis_for_the_BCF;
		} else if (row.Basis_for_the_BCF_other != null && !row.Basis_for_the_BCF_other.isEmpty()
				&& !row.Basis_for_the_BCF_other.toLowerCase().contains("other")) {
			tissueType = row.Basis_for_the_BCF_other;
		} else if (row.Tissue_analyzed != null && !row.Tissue_analyzed.isEmpty()) {
			tissueType = row.Tissue_analyzed;
		} else if (row.Organ != null && !row.Organ.isEmpty()) {
			tissueType = row.Organ;
		}

		
		String responseSiteFinal = BCFUtilities.ResponseSiteFormatter.normalizeResponseSite(tissueType, er);
		if (responseSiteFinal != null && !responseSiteFinal.isEmpty()) {
			// Only put in the properly cleaned and formatted response site values
			er.experimental_parameters.put(ExperimentalConstants.expParamResponseSite, responseSiteFinal);
		}
		
//		if(tissueType!=null && tissueType.contains("adjusted to 5% lipid content")) {
//			er.experimental_parameters.put("adjusted to 5% lipid content","yes");
//		} else {
//			er.experimental_parameters.put("adjusted to 5% lipid content","no");
//		}
		
	}

	private void addParametersTestLocationAndExposureType(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Test_type == null || row.Test_type.isBlank()) {
			return;
		}

		String testType = row.Test_type.trim();

		if (testType.equalsIgnoreCase("Other Test Type") && row.Test_type_other != null) {
			testType = row.Test_type_other.trim();
		}

		if (testType.equalsIgnoreCase("semi-static")) {
			testType = "Renewal";
		}

		if (testType.toLowerCase().contains("estimated")) {
			er.keep = false;
			er.updateReason("Test_type: Estimated");
			return;
		}
		
		if (testType.toLowerCase().contains("feeding study")) {
			er.keep = false;
			er.updateReason("Test_type: Feeding study");
			return;
		}

		if (testType.equalsIgnoreCase("Field Study")) {
			er.experimental_parameters.put(ExperimentalConstants.expParamTestLocation, "Field");
		} else {
			er.experimental_parameters.put(ExperimentalConstants.expParamTestLocation, "Lab");
		}

		er.experimental_parameters.put(ExperimentalConstants.expParamExposureType,
				WordUtils.capitalizeFully(testType));
		
	}

	private void addParameterReliability(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Reliability_score != null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamReliability, row.Reliability_score);
			if(row.Reliability_score.contains("3") || row.Reliability_score.contains("4")) {
				er.keep = false;
				er.reason = "Insufficient reliability";
			}
		} else if (row.Reliability!=null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamReliability, row.Reliability);
			if(row.Reliability.contains("3") || row.Reliability.contains("4")) {
				er.keep = false;
				er.reason = "Insufficient reliability";
			}
		}
	}

	private void addParameterMediaType(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Water_type != null && !row.Water_type.isEmpty()
				&& !row.Water_type.equalsIgnoreCase("not specified")) {
			String waterType = row.Water_type.toLowerCase().replace("fresh water", "freshwater")
					.replace("salt water", "saltwater").trim();
			if (waterType.equalsIgnoreCase("fresh water")) {
				waterType = "freshwater";
			} else if (waterType.equalsIgnoreCase("natural water: freshwater")) {
				waterType = "freshwater";
			} else if (waterType.equalsIgnoreCase("natural water: marine")) {
				waterType = "marine";
			}
			er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, waterType);

		} else if (row.Water_media_type != null && !row.Water_media_type.isEmpty()
				&& !row.Water_media_type.equalsIgnoreCase("not specified")) {

			String waterMediaType = row.Water_media_type;
			if(row.Water_media_type.equals( "Other Water media type")) {
				if(row.Water_media_type_other!=null) {
					waterMediaType=row.Water_media_type_other;
				}
			} 
			if (waterMediaType.equalsIgnoreCase("fresh water")) {
				waterMediaType = "freshwater";
			} else if (waterMediaType.equalsIgnoreCase("natural water: freshwater")) {
				waterMediaType = "freshwater";
			} else if (waterMediaType.equalsIgnoreCase("natural water: marine")) {
				waterMediaType = "marine";
			}
			er.experimental_parameters.put(ExperimentalConstants.expParamMediaType,
					waterMediaType.toLowerCase().trim());
		}


	}


	private void addParameterLipidContent(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		// Lipid concentration
		String lipidContentMax = null;
		String lipidContentMin = null;
		String lipidContentMean = null;
		String lipidContentUnit = null;

		if (row.Lipid_content_Unit != null && !row.Lipid_content_Unit.isEmpty()
				&& !row.Lipid_content_Unit.equalsIgnoreCase("Published Data")) {
			lipidContentMax = row.Lipid_content_MaxValue;
			lipidContentMin = row.Lipid_content_MinValue;
			lipidContentMean = row.Lipid_content_MeanValue;
			lipidContentUnit = row.Lipid_content_Unit;
		} else if (row.Lipid_Unit != null && !row.Lipid_Unit.isEmpty()) {
			lipidContentMax = row.Lipid_MaxValue;
			lipidContentMin = row.Lipid_MinValue;
			lipidContentMean = row.Lipid_MeanValue;
			lipidContentUnit = row.Lipid_Unit;
		} else if (row.Database != null && row.Database.equals(ExperimentalConstants.sourceCanada)) {
			if (row.Comments != null && row.Comments.toLowerCase().contains("lipid content")) {
				Pattern lipidPattern = Pattern.compile("(\\d+\\.?\\d*)\\s*%");
				Matcher lipidMatcher = lipidPattern.matcher(row.Comments);
				if (lipidMatcher.find()) {
					lipidContentMean = lipidMatcher.group(1);
					lipidContentUnit = "%";
				}
			}
		}

		if (lipidContentMax != null || lipidContentMin != null || lipidContentMean != null) {
			lipidContentMax = convertLipidUnits(lipidContentMax, lipidContentUnit);
			lipidContentMin = convertLipidUnits(lipidContentMin, lipidContentUnit);
			lipidContentMean = convertLipidUnits(lipidContentMean, lipidContentUnit);

			ParameterValue pv = new ParameterValue();
			pv.parameter.name = ExperimentalConstants.expParamLipidPercent;
			pv.unit.abbreviation = ExperimentalConstants.str_dimensionless;

			if (lipidContentMax != null && !lipidContentMax.isEmpty()) {
				pv.value_max = Double.parseDouble(lipidContentMax);
			}
			if (lipidContentMin != null && !lipidContentMin.isEmpty()) {
				pv.value_min = Double.parseDouble(lipidContentMin);
			}
			if (lipidContentMean != null && !lipidContentMean.isEmpty()) {
				pv.value_point_estimate = Double.parseDouble(lipidContentMean);
			}

			er.parameter_values.add(pv);
		}
	}

	private void addExperimentalParametersByDatabase(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		if (row.Database == null) {
			return;
		}

		if (row.Database.equals("Bioaccumulation fish CEFIC LRI")) {

			if (row.Comments != null) {
				er.note = row.Comments;
			}
			er.reference = row.Reference_source;


			LiteratureSource ls = new LiteratureSource();
			er.literatureSource = ls;
			if (row.Author != null) {
				ls.name = row.Author + " (" + row.Year + ")";
				ls.author = row.Author;
				ls.title = row.Title;
				ls.citation = row.Author + " (" + row.Year + "). " + ls.title + ". " + row.Reference_source;
			} else {
				ls.citation = row.Reference_source + " (" + row.Year + ")";
			}

		} else if (row.Database.equals("Bioaccumulation Canada")) {
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin, row.Test_organisms_species);

		} else if (row.Database.equals("Bioconcentration and logKow NITE")) {

			er.publicSourceOriginal = new PublicSource();
			er.publicSourceOriginal.name = "Bioconcentration and logKow NITE";
			er.publicSourceOriginal.url = "https://www.nite.go.jp/en/chem/qsar/evaluation.html";
			er.publicSourceOriginal.description = "National Institute of Technology and Evaluation (Japan)";
			
			if (row.Test_guideline != null) {
				String guideline = BCFUtilities.TestGuidelineFormatter.normalizeTestGuideline(row.Test_guideline);
				if (guideline != null && !guideline.isEmpty()) {
					er.experimental_parameters.put(ExperimentalConstants.expParamGuideline, guideline);
				}
				
//				System.out.println(guideline);
				BCFUtilities.setOecd305Parameters(er);
			} else {
				// TODO Set to flow-through and what else?
//				er.experimental_parameters.put(ExperimentalConstants.expParamExposureType, "Flow-through");
			}

			if (row.Year != null) {
				er.document_name = "NITE " + row.Year;
			}

		} else if (row.Database.equals("ECHA REACH")) {
			addLiteratureInformation(row, er);
						
			if (er.experimental_parameters.get("Test guideline")!=null) {
				
				String guideline2 = (String)er.experimental_parameters.get("Test guideline");
				if (guideline2.equals(ExperimentalConstants.guidelineOecd305)) {
					BCFUtilities.setOecd305Parameters(er);
				} else if (guideline2.contains("850.1730")) {
					BCFUtilities.setOpps8501730Parameters(er);
					er.updateNote("OPPTS 850.1730 guideline assumed to be similar enough to OECD 305 to update parameters");
				}
			}
		}
	}

	private void addParameterWaterConcentration(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Exposure_concentration_MeanValue != null && !row.Exposure_concentration_MeanValue.isEmpty()
				&& row.Exposure_concentration_Unit != null && !row.Exposure_concentration_Unit.isEmpty()) {
			ParameterValue pv = new ParameterValue();
			pv.parameter.name = ExperimentalConstants.expParamWaterConcentration;
			pv.unit.abbreviation = ExperimentalConstants.str_g_L;
			pv.unit.name = "G_L";
			if (row.Exposure_concentration_Unit.equalsIgnoreCase(ExperimentalConstants.str_g_L)) {
				pv.value_point_estimate = Double.parseDouble(row.Exposure_concentration_MeanValue);
			} else if (row.Exposure_concentration_Unit.equalsIgnoreCase(ExperimentalConstants.str_mg_L)) {
				pv.value_point_estimate = Double.parseDouble(row.Exposure_concentration_MeanValue) / 1000.0;
			} else if (row.Exposure_concentration_Unit.equalsIgnoreCase(ExperimentalConstants.str_ug_L)) {
				pv.value_point_estimate = Double.parseDouble(row.Exposure_concentration_MeanValue) / 1000000.0;
			}
			er.parameter_values.add(pv);
		}
	}

	private void addLiteratureInformation(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {



		DecimalFormat df = new DecimalFormat("0");

		Set<String>titles = new HashSet<>();

		if(row.Title!=null) {
			titles.add(row.Title);
		}

		if(row.Title_0!=null) {
			titles.add(row.Title_0);
		}

		if(row.Title_1!=null) {
			titles.add(row.Title_1);
		}

		if(row.Title_2!=null) {
			titles.add(row.Title_2);
		}

		//		System.out.println(titles);
		String title2=null;

		for (String title:titles) {
			if(!title.equals("Unnamed")) {
				title2=title;
				break;
			}
		}

		if(title2!=null) {
			er.document_name = title2;

			if (row.Year != null) {
				try {
					er.document_name += " (" + df.format(Double.parseDouble(row.Year)+")");
				} catch (Exception ex) {
					er.document_name += " (" + row.Year+")";
				}
			}

//			if(row.CAS_Number.equals("102-71-6")) {
//				System.out.println(titles);
//				System.out.println(er.document_name);
//			}

			//			System.out.println(er.document_name);

		}


		//		System.out.println(row.Author+"("+ls.year+") "+ls.title+"."+ls.journal);

		//		if(row.Author!=null && row.Year!=null && row.Title!=null && row.Reference_source!=null) {
		//			ls.citation = ls.author+"("+ls.year+") "+ls.title+"."+ls.journal;
		//			er.literatureSource = ls;
		//			
		//			System.out.println(ls.citation);
		//			
		//		} else if(row.Author==null && row.Title!=null && row.Reference_source==null) {
		//			er.document_name = row.Title;
		//			if (row.Year != null) {
		//				try {
		//					er.document_name += " " + df.format(Double.parseDouble(row.Year));
		//				} catch (Exception ex) {
		//					er.document_name += " " + row.Year;
		//					System.out.println(er.document_name);
		//				}
		//			}
		//
		//		} else {
		//			String json=JsonUtilities.gsonPretty.toJson(ls);
		//			
		//			if(!json.equals("{}"))
		//				System.out.println(json);
		//		}
	}

	private void setObservationDuration(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Duration_Unit == null)
			return;

		Double mean = getValueInDays(row.Duration_MeanValue, row.Duration_Unit);
		Double min = getValueInDays(row.Duration_MinValue, row.Duration_Unit);
		Double max = getValueInDays(row.Duration_MaxValue, row.Duration_Unit);

		ParameterValue pv = new ParameterValue();
		pv.parameter.name = "Observation duration";

		if (min != null && max != null) {
			pv.value_min = min;
			pv.value_max = max;
		} else if (mean != null) {
			pv.value_point_estimate = mean;
		} else if (min != null) {
			pv.value_min = min;
		} else if (max != null) {
			pv.value_max = max;
		} else {
			pv.value_point_estimate = mean;
		}

		pv.unit.name = "DAYS";
		pv.unit.abbreviation = "days";
		er.parameter_values.add(pv);
	}

	private Double getValueInDays(String obs_duration, String units) {
		if (obs_duration == null)
			return null;

		if (obs_duration.contains("-")) {
			String[] parts = obs_duration.split("-");
			if (parts.length == 2) {
				try {
					Double low = Double.parseDouble(parts[0]);
					Double high = Double.parseDouble(parts[1]);
					obs_duration = String.valueOf((low + high) / 2);
				} catch (NumberFormatException e) {
					return null;
				}
			}
		}

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
		case "mi":
		case "min":
			return studyDurationValue /= 1440.0;
		case "s":
			return studyDurationValue /= (1440.0 * 60);
		case "-":
		case "NR":
			return null;
		default:
			return null;
		}
	}

	private void addParameterTestGuideline(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		JsonObject jo = new JsonObject();

		jo.addProperty("Test_guideline", row.Test_guideline);
		jo.addProperty("Test_guideline_0", row.Test_guideline_0);
		jo.addProperty("Test_guideline_other", row.Test_guideline_other);
		jo.addProperty("Test_guideline_other_0", row.Test_guideline_other_0);

		String guideline=null;

		if(row.Qualifier_of_guideline!=null && row.Qualifier_of_guideline.equals("no guideline followed")) {
			er.keep=false;
			er.reason="no guideline followed";
		}
		
		
		if (row.Test_guideline != null) {
			if (row.Test_guideline.toLowerCase().contains("other test guideline")) {
				guideline = BCFUtilities.TestGuidelineFormatter.normalizeTestGuideline(row.Test_guideline_other);
			} else {
				guideline = BCFUtilities.TestGuidelineFormatter.normalizeTestGuideline(row.Test_guideline);
			}
		} else {
			if (row.Test_guideline_0 != null) {
				if (row.Test_guideline_0.toLowerCase().contains("other test guideline")) {
					guideline = BCFUtilities.TestGuidelineFormatter
							.normalizeTestGuideline(row.Test_guideline_other_0);
				} else {
					guideline = BCFUtilities.TestGuidelineFormatter.normalizeTestGuideline(row.Test_guideline_0);
				}
			}
		}

		if (guideline != null && !guideline.isEmpty()) {

			String guideline2 = BCFUtilities.TestGuidelineFormatter.normalizeTestGuideline(row.Test_guideline);

			//			System.out.println(guideline+"\t"+guideline2);

			er.experimental_parameters.put(ExperimentalConstants.expParamGuideline, guideline2);


			
		}

	}

	private boolean haveValueExceedingLogKocCutoff(ExperimentalRecord er, double cutoff) {
		boolean exceedCutoff = false;

		if (er.property_value_point_estimate_original != null && er.property_value_point_estimate_original > cutoff) {
			exceedCutoff = true;
		}
		if (er.property_value_min_original != null && er.property_value_min_original > cutoff) {
			exceedCutoff = true;
		}
		if (er.property_value_max_original != null && er.property_value_max_original > cutoff) {
			exceedCutoff = true;
		}
		return exceedCutoff;
	}

	private String convertLipidUnits(String value, String unit) {
		if (value == null || unit == null) {
			return "";
		}
		if (unit.contains("%")) {
			return value;
		} else if (unit.contains("/")) {
			String[] unitStrings = unit.split("/");

			double conversionNumerator = 1.0;
			if (unitStrings[0].contains("mg")) {
				conversionNumerator = 1 / 1000.0;
			} else if (unitStrings[0].contains("µg")) {
				conversionNumerator = 1 / 1000000.0;
			}

			double conversionDenominator = (unitStrings[1].contains("kg")) ? 1 / 1000.0 : 1.0;

			double valueDouble = Double.parseDouble(value);
			valueDouble = valueDouble * conversionNumerator * conversionDenominator * 100.0;

			return String.valueOf(valueDouble);
		} else {
			return value;
		}
	}

	// -------------------------------------------------------------------------
	// Local support
	// -------------------------------------------------------------------------

	private void setPropertyValuesBcf(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		// Uses the row's generic property parsing and unit conversion machinery.
		row.setPropertyValues(er, true);

		if(row.Value_Unit==null) {
			er.keep=false;
			er.updateReason("Value_Unit is missing");
		}

	}
}
