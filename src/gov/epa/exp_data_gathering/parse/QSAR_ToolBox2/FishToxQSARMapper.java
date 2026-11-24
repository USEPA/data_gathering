package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.util.Hashtable;
import java.util.List;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;

public class FishToxQSARMapper implements QSARRecordMapper {

	private final Hashtable<String, List<RecordQSAR_ToolBox2.Species>> htSpeciesByCommonName;

	public FishToxQSARMapper(Hashtable<String, List<RecordQSAR_ToolBox2.Species>> htSpeciesByCommonName) {
		this.htSpeciesByCommonName = htSpeciesByCommonName;
	}

	@Override
	public ExperimentalRecord map(RecordQSAR_ToolBox2 row) {
		ExperimentalRecord er = row.createBaseRecord();

		er.property_name = ExperimentalConstants.strAcuteAquaticToxicity;
		er.property_category = ExperimentalConstants.strAcuteAquaticToxicity;

		// Species common/latin
		if (row.Test_organisms_species != null && row.Test_organisms_species.contains("Other Test organisms")) {
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin, row.Test_organisms_species_other);
		} else if (row.Test_organisms_species != null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin, row.Test_organisms_species);
		}

		// Fish-only validation
		if (row.Superclass != null) {
			if (row.Superclass.contains("Actinopterygii")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, "Fish");
			} else {
				er.keep = false;
				er.reason = "Not fish";
			}
		} else {
			String supercategory = getSpeciesSupercategoryFishTox(row);
			if (supercategory != null) {
				er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesSupercategory, supercategory);
			}
			if (supercategory == null || !supercategory.equals("Fish")) {
				er.keep = false;
				er.reason = "Not a fish species";
			}
		}

		addMetadataFishTox(row, er);

		// Exposure type
		if (row.Test_type == null) {
			er.keep = false;
			er.reason = "Missing exposure type";
		} else {
			if (row.Test_type.toLowerCase().contains("semi")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamExposureType, "Renewal");
			} else {
				er.experimental_parameters.put(ExperimentalConstants.expParamExposureType, row.Test_type);
			}
			if (!row.Test_type.equals("Static") && !row.Test_type.equals("Flow-through")
					&& !row.Test_type.equals("Semi-static") && !row.Test_type.equals("Renewal")) {
				er.keep = false;
				er.reason = "Invalid exposure type";
			}
		}

		// Endpoint check
		if (row.Endpoint == null || !row.Endpoint.equals("LC50")) {
			er.keep = false;
			er.reason = "Not LC50";
		}

		// Units/value parsing
		er.property_value_units_original = row.Value_Unit;

		if (row.Value_Qualifier != null) {
			if (row.Value_Qualifier.equals("ca.")) row.Value_Qualifier = "~";
			er.property_value_numeric_qualifier = row.Value_Qualifier;
		}

		try {
			if (row.Value_MeanValue != null) {
				er.property_value_point_estimate_original = Double.parseDouble(row.Value_MeanValue);
				er.property_value_string = row.Value_MeanValue + " " + er.property_value_units_original;
			} else if (row.Value_MinValue != null && row.Value_MaxValue != null) {
				er.property_value_min_original = Double.parseDouble(row.Value_MinValue);
				er.property_value_max_original = Double.parseDouble(row.Value_MaxValue);
				er.property_value_string = row.Value_MinValue + " - " + row.Value_MaxValue + " "
						+ er.property_value_units_original;
			} else if (row.Value_MinValue != null) {
				er.property_value_min_original = Double.parseDouble(row.Value_MinValue);
				er.property_value_string = "> " + row.Value_MinValue + " " + er.property_value_units_original;
			} else if (row.Value_MaxValue != null) {
				er.property_value_max_original = Double.parseDouble(row.Value_MaxValue);
				er.property_value_string = "< " + row.Value_MaxValue + " " + er.property_value_units_original;
			}
		} catch (Exception e) {
			er.keep = false;
			er.reason = "Parse error";
		}

		RecordQSAR_ToolBox2.unitConverter.convertRecord(er);

		if (!er.hasNumericFinalValue()) {
			er.keep = false;
			er.reason = "No numeric final value";
		}

		return er;
	}

	private void addMetadataFishTox(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		if (row.Test_organisms_species != null) {
			er.experimental_parameters.put(ExperimentalConstants.expParamSpeciesLatin, row.Test_organisms_species);
		}

		if (row.Water_media_type != null && !row.Water_media_type.isEmpty()
				&& !row.Water_media_type.equalsIgnoreCase("not specified")) {
			String waterMedia = row.Water_media_type;
			if (waterMedia.equalsIgnoreCase("fresh water")) {
				waterMedia = "freshwater";
			} else if (waterMedia.equalsIgnoreCase("natural water: freshwater")) {
				waterMedia = "freshwater";
			} else if (waterMedia.equalsIgnoreCase("natural water: marine")) {
				waterMedia = "marine";
			}
			er.experimental_parameters.put(ExperimentalConstants.expParamMediaType, waterMedia);
		}

		if (row.Test_type != null && !row.Test_type.isEmpty()) {
			if (row.Test_type.toLowerCase().contains("semi")) {
				er.experimental_parameters.put(ExperimentalConstants.expParamExposureType, "Renewal");
			} else {
				er.experimental_parameters.put(ExperimentalConstants.expParamExposureType, row.Test_type);
			}
		}
	}

	private String getSpeciesSupercategoryFishTox(RecordQSAR_ToolBox2 row) {

		if (row.Test_organisms_species != null && row.Test_organisms_species.contains("Other Test organisms")
				&& htSpeciesByCommonName.containsKey(row.Test_organisms_species_other.toLowerCase())) {
			List<RecordQSAR_ToolBox2.Species> speciesList = htSpeciesByCommonName.get(row.Test_organisms_species_other.toLowerCase());
			for (RecordQSAR_ToolBox2.Species species : speciesList) {
				String sc = species.species_supercategory;
				if (sc == null) continue;
				if (sc.contains("fish")) return "Fish";
				else if (sc.contains("algae")) return "Algae";
				else if (sc.contains("crustaceans")) return "Crustaceans";
				else if (sc.contains("insects/spiders")) return "Insects/spiders";
				else if (sc.contains("molluscs")) return "Molluscs";
				else if (sc.contains("worms")) return "Worms";
				else if (sc.contains("invertebrates")) return "Invertebrates";
				else if (sc.contains("flowers, trees, shrubs, ferns")) return "Flowers, trees, shrubs, ferns";
				else if (sc.contains("microorganisms")) return "Microorganisms";
				else if (sc.equals("amphibians") || sc.equals("amphibians; standard test species")) return "Amphibians";
				else if (sc.equals("reptiles")) return "Reptiles";
				else if (sc.equals("omit")) return "Omit";
			}
		} else if (row.Test_organisms_species != null
				&& htSpeciesByCommonName.containsKey(row.Test_organisms_species.toLowerCase())) {
			List<RecordQSAR_ToolBox2.Species> speciesList = htSpeciesByCommonName.get(row.Test_organisms_species.toLowerCase());
			for (RecordQSAR_ToolBox2.Species species : speciesList) {
				String sc = species.species_supercategory;
				if (sc == null) continue;
				if (sc.contains("fish")) return "Fish";
				else if (sc.contains("algae")) return "Algae";
				else if (sc.contains("crustaceans")) return "Crustaceans";
				else if (sc.contains("insects/spiders")) return "Insects/spiders";
				else if (sc.contains("molluscs")) return "Molluscs";
				else if (sc.contains("worms")) return "Worms";
				else if (sc.contains("invertebrates")) return "Invertebrates";
				else if (sc.contains("flowers, trees, shrubs, ferns")) return "Flowers, trees, shrubs, ferns";
				else if (sc.contains("microorganisms")) return "Microorganisms";
				else if (sc.equals("amphibians") || sc.equals("amphibians; standard test species")) return "Amphibians";
				else if (sc.equals("reptiles")) return "Reptiles";
				else if (sc.equals("omit")) return "Omit";
			}
		}
		return null;
	}
}