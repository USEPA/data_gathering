package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.ghs_data_gathering.Parse.Parse;

public class SensitizationQSARMapper implements QSARRecordMapper {


	@Override
	public ExperimentalRecord map(RecordQSAR_ToolBox2 row) {
		ExperimentalRecord er = row.createBaseRecord();

		if (row.EndpointPath == null) {
			er.keep = false;
			er.reason = "EndpointPath not set";
			return er;
		}

		row.setGuidelines(er);
		row.setMeasurementMethod(er);
		row.addGenericMetadata(er);
		
		addMetadataSensitization(row, er);//do we need this?

		setPropertyNameSensitization(row, er);
		if (!er.keep) {
			return er;
		}

		setPropertyValuesLLNA(row, er);

		if (row.Title != null) {
			er.document_name = row.Title;
			if (row.Year != null) {
				try {
					er.document_name += " " + new DecimalFormat("0").format(Double.parseDouble(row.Year));
				} catch (Exception ex) {
					er.document_name += " " + row.Year;
				}
			}
		}

		er.date_accessed = row.lastUpdated;
		return er;
	}

	// ---------------------------------------------------------------------
	// Sensitization-specific methods moved from RecordQSAR_ToolBox2
	// ---------------------------------------------------------------------

	private void setPropertyNameSensitization(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		if (row.Database != null && row.Database.equals("ECHA REACH")) {
			if (row.Harmonized_Template != null && row.Harmonized_Template.equals("SkinSensitisation")) {
				er.property_name = ExperimentalConstants.strSkinSensitizationLLNA;
			}
		} else if (row.Database != null && row.Database.equals("Skin Sensitization")) {
			if (row.Assay != null && row.Assay.contains("LLNA")) {
				er.property_name = ExperimentalConstants.strSkinSensitizationLLNA;
				checkLLNA_Guideline(row, er);
			}
		}

		if (er.property_name == null) {
			er.property_name = "Not set";
			er.keep = false;
			er.reason = "Property name not set";
		}
	}

	private void setPropertyValuesLLNA(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		DecimalFormat df = new DecimalFormat("0.0");
		double nonSensitizing = 0;
		double sensitizing = 1;

		if (row.Database != null && row.Database.equals("Skin Sensitization")) {
			if (row.Assay != null && row.Assay.equals("LLNA")) {

				if (row.Value_Scale != null && row.Value_Scale.equals("Skin sensitization EC3(ratio)")) {

					double value = Double.parseDouble(row.Value_MeanValue);

					er.property_value_string = "EC3 = " + df.format(value) + "%";
					er.property_value_point_estimate_original = value;
					er.property_value_units_original = "%";
					er.property_value_numeric_qualifier = row.Value_Qualifier;

					if (row.Value_Qualifier != null) {
						er.property_value_string = row.Value_Qualifier + er.property_value_string;
					}

					if (row.Value_Qualifier != null && row.Value_Qualifier.equals(">")) {
						if (value >= 100) {
							er.property_value_qualitative = "Not sensitizing";
						} else {
							er.keep = false;
							er.reason = "Ambiguous";
							er.property_value_qualitative = "Ambiguous";
						}
					} else if (row.Value_Qualifier != null && row.Value_Qualifier.equals("<")) {
						er.property_value_qualitative = "Sensitizing";
					} else if (value >= 100) {
						er.property_value_qualitative = "Not sensitizing";
					} else {
						er.property_value_qualitative = "Sensitizing";
					}

				} else if (row.Value_Scale != null && row.Value_Scale.equals("Skin sensitisation I (Oasis)")) {

					er.property_value_string = row.Value_MeanValue;

					if (row.Value_MeanValue.equals("Negative") || row.Value_MeanValue.equals("Not sensitising")) {
						er.property_value_qualitative = "Not sensitizing";
						er.property_value_point_estimate_original = nonSensitizing;
						er.property_value_units_original = ExperimentalConstants.str_binary;
					} else if (row.Value_MeanValue.equals("Strongly positive") || row.Value_MeanValue.equals("Weakly positive")) {
						er.property_value_qualitative = "Sensitizing";
						er.property_value_point_estimate_original = sensitizing;
						er.property_value_units_original = ExperimentalConstants.str_binary;
					}
				}
			}

			if (er.property_value_qualitative != null) {
				if (er.property_value_qualitative.contains("Not sensitizing")) {
					er.property_value_point_estimate_final = nonSensitizing;
				} else if (er.property_value_qualitative.contains("Sensitizing")) {
					er.property_value_point_estimate_final = sensitizing;
				}
				er.property_value_units_final = ExperimentalConstants.str_binary;
			}
		}
	}

	private void checkLLNA_Guideline(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		String strGuidelinesQualifiers = er.experimental_parameters.get(ExperimentalConstants.expParamGuideline) + "";

		List<String> goodGuidelines = Arrays.asList("429", "870.2600", "B.42", "442A", "442B", "442 B", "B.51", "406",
				"595.12", "B.6");

		if (er.keep) {
			if (er.experimental_parameters.get(ExperimentalConstants.expParamGuideline) != null
					&& !hasString(goodGuidelines, strGuidelinesQualifiers)) {
				System.out.println("Invalid guideline:\t" + strGuidelinesQualifiers);
				er.keep = false;
				er.reason = "Invalid guideline";
			}
		}
	}

	private boolean hasString(List<String> examples, String str) {
		for (String example : examples) {
			if (str.contains(example)) {
				return true;
			}
		}
		return false;
	}
	
	//TODO do we need this?
	private void addMetadataSensitization(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Assay != null) {
			er.experimental_parameters.put("Assay", row.Assay);
		}
		if (row.Assay_other != null) {
			er.experimental_parameters.put("Assay other", row.Assay_other);
		}
		if (row.Interpretation_of_results != null) {
			er.experimental_parameters.put("Interpretation of results", row.Interpretation_of_results);
		}
		if (row.Route_Of_Challenge_Exposure != null) {
			er.experimental_parameters.put("Route of challenge exposure", row.Route_Of_Challenge_Exposure);
		}
		if (row.Route_Of_Induction_Exposure != null) {
			er.experimental_parameters.put("Route of induction exposure", row.Route_Of_Induction_Exposure);
		}
	}
}