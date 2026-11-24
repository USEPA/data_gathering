package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.text.DecimalFormat;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.TextUtilities;

public class AcuteToxicityQSARMapper implements QSARRecordMapper {

	private final ParseQSAR_ToolBox2 pqt;

	public AcuteToxicityQSARMapper(ParseQSAR_ToolBox2 pqt) {
		this.pqt = pqt;
	}

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

		// Generic metadata
		row.addGenericMetadata(er);

		// Acute-specific setup
		setUnitsAcute(row, er);
		setPropertyValuesAcute(row, er);
		setPropertyNameAcuteToxicity(row, er);

		if (!er.keep) {
			return er;
		}

		RecordQSAR_ToolBox2.unitConverter.convertRecord(er);

		if (!er.hasNumericFinalValue()) {
			er.keep = false;
			er.reason = "No numeric final value";
		}

		er.date_accessed = row.lastUpdated;
		return er;
	}

	// ---------------------------------------------------------------------
	// Acute-specific methods moved from RecordQSAR_ToolBox2
	// ---------------------------------------------------------------------

	private void setUnitsAcute(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Value_Unit != null) {

			String units = row.Value_Unit;
			units = units.replace(", (5.8 mg/L air)", "");
			units = units.replace("(22,948 mg/m3, 23 mg/L nominal)", "");
			units = units.replace("(5300 mg/m3, 5.3 mg/L)", "");
			units = units.replace("(4500 mg/kg bw)", "");

			units = units.replace("(analytical)", "");
			units = units.replace("(nominal)", "");

			units = units.replace("mg/Kg", ExperimentalConstants.str_mg_kg);

			units = units.replace("mL/kg bw", ExperimentalConstants.str_mL_kg);
			units = units.replace("ul/kg bw", ExperimentalConstants.str_uL_kg);
			units = units.replace("µl/kg bw", ExperimentalConstants.str_uL_kg);

			units = units.replace("cm3/kg bw", ExperimentalConstants.str_mL_kg);
			units = units.replace("cc/kg", ExperimentalConstants.str_mL_kg);

			units = units.replace("mg/L air", ExperimentalConstants.str_mg_L);
			units = units.replace("mg/L in drinking water", ExperimentalConstants.str_mg_L);

			units = units.replace("µl/L air", ExperimentalConstants.str_uL_L);

			units = units.replace("mg/m^3 air", ExperimentalConstants.str_mg_m3);
			units = units.replace("mg/m3", ExperimentalConstants.str_mg_m3);

			units = units.replace("gm/kg bw", ExperimentalConstants.str_g_kg);
			units = units.replace("g/kg/ bw", ExperimentalConstants.str_g_kg);
			units = units.replace("g/Kg", ExperimentalConstants.str_g_kg);
			units = units.replace("g/kg bw.", ExperimentalConstants.str_g_kg);
			units = units.replace("g/kg/bw", ExperimentalConstants.str_g_kg);

			units = units.replace("g/m3 air", ExperimentalConstants.str_g_m3);

			er.property_value_units_original = units.trim();

		} else {
			er.keep = false;
			er.reason = "Missing units";
		}
	}

	private void setPropertyNameAcuteToxicity(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		if (row.Harmonized_Template == null) {
			er.property_name = "Not set";
			er.keep = false;
			er.reason = "Missing Harmonized_Template";

		} else if (row.Harmonized_Template.equals("SkinSensitisation")) {

			if (row.Assay != null && row.Assay.contains("LLNA")) {
				er.property_name = ExperimentalConstants.strSkinSensitizationLLNA;
			}

		} else if (row.Test_organisms_species == null) {
			er.property_name = "Not set";
			er.keep = false;
			er.reason = "Missing Test_organisms_species";

		} else if (row.Endpoint_other != null && !row.Harmonized_Template.equals("SkinSensitisation")) {
			er.keep = false;
			er.reason = "Property not handled";
			er.property_name = row.Harmonized_Template.replace("AcuteToxicity", "") + " "
					+ row.Test_organisms_species + " " + row.Endpoint_other;

		} else if (row.Harmonized_Template.equals("AcuteToxicityOral")) {

			if (row.Test_organisms_species.equals("rat")) {
				er.property_name = ExperimentalConstants.strORAL_RAT_LD50;
			} else if (row.Test_organisms_species.equals("mouse")) {
				er.property_name = ExperimentalConstants.strORAL_MOUSE_LD50;
			} else if (row.Test_organisms_species.equals("rabbit")) {
				er.property_name = ExperimentalConstants.strORAL_RABBIT_LD50;
			}
			er.property_category = ExperimentalConstants.strAcuteOralToxicity;

		} else if (row.Harmonized_Template.equals("AcuteToxicityInhalation")) {
			if (row.Test_organisms_species.equals("rat")) {
				er.property_name = ExperimentalConstants.strInhalationRatLC50;
			} else if (row.Test_organisms_species.equals("mouse")) {
				er.property_name = ExperimentalConstants.strInhalationMouseLC50;
			} else if (row.Test_organisms_species.equals("rabbit")) {
				er.property_name = ExperimentalConstants.strInhalationRabbitLC50;
			}
			er.property_category = ExperimentalConstants.strAcuteInhalationToxicity;

		} else if (row.Harmonized_Template.equals("AcuteToxicityDermal")) {
			if (row.Test_organisms_species.equals("rat")) {
				er.property_name = ExperimentalConstants.strDERMAL_RAT_LD50;
			} else if (row.Test_organisms_species.equals("mouse")) {
				er.property_name = ExperimentalConstants.strDERMAL_MOUSE_LD50;
			} else if (row.Test_organisms_species.equals("rabbit")) {
				er.property_name = ExperimentalConstants.strDERMAL_RABBIT_LD50;
			}
			er.property_category = ExperimentalConstants.strAcuteDermalToxicity;
		}

		if (er.property_name == null) {
			er.property_name = "Not set";
			er.keep = false;
			er.reason = "Property name not set";
		}
	}

	private void setPropertyValuesAcute(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {

		row.Value_MeanValue = TextUtilities.fixMojibake(row.Value_MeanValue);
		row.Value_MinValue = TextUtilities.fixMojibake(row.Value_MinValue);
		row.Value_MaxValue = TextUtilities.fixMojibake(row.Value_MaxValue);
		row.Qualifier = TextUtilities.fixMojibake(row.Qualifier);

		String infStr = "\u221E";

		if (row.Value_MinValue != null && row.Original_value_MaxValue != null) {

			if (row.Value_MinValue.contains(infStr) || row.Value_MaxValue.contains(infStr)) {
				er.keep = false;
				er.updateReason("Property value is infinite");
				return;
			}

			try {
				er.property_value_min_original = Double.parseDouble(row.Value_MinValue);
			} catch (Exception ex) {
				er.keep = false;
				er.reason = "Could not parse min value";
				return;
			}
			try {
				er.property_value_max_original = Double.parseDouble(row.Value_MaxValue);
			} catch (Exception ex) {
				er.keep = false;
				er.reason = "Could not parse max value";
				return;
			}

			er.property_value_string = gov.epa.exp_data_gathering.parse.Parse.formatValue(er.property_value_min_original) + " - "
					+ gov.epa.exp_data_gathering.parse.Parse.formatValue(er.property_value_max_original);

		} else if (row.Value_MinValue != null) {

			if (row.Value_MinValue.contains(infStr)) {
				er.keep = false;
				er.updateReason("Property value is infinite");
				return;
			}

			er.property_value_min_original = Double.parseDouble(row.Value_MinValue);
			er.property_value_string = "> " + gov.epa.exp_data_gathering.parse.Parse.formatValue(er.property_value_min_original);

		} else if (row.Value_MaxValue != null) {

			if (row.Value_MaxValue.contains(infStr)) {
				er.keep = false;
				er.updateReason("Property value is infinite");
				return;
			}

			er.property_value_max_original = Double.parseDouble(row.Value_MaxValue);
			er.property_value_string = "< " + gov.epa.exp_data_gathering.parse.Parse.formatValue(er.property_value_max_original);

		} else if (row.Value_MeanValue != null) {

			if (row.Value_MeanValue.contains(infStr)) {
				er.keep = false;
				er.reason = "Infinite property value";
				return;
			}

			if (row.Value_MeanValue.contains("NaN")) {
				er.keep = false;
				er.reason = "NaN value";
				return;
			}

			if (row.Value_MeanValue.contains("not specified")) {
				er.keep = false;
				er.reason = "Property value is not specified";
				return;
			}

			try {
				er.property_value_point_estimate_original = Double.parseDouble(row.Value_MeanValue);
			} catch (Exception ex) {
				er.keep = false;
				er.reason = "Could not parse Value_MeanValue";
				return;
			}

			er.property_value_string = "";

			if (row.Qualifier != null) {
				if (row.Qualifier.contentEquals("No qualifier")) {
					er.property_value_numeric_qualifier = null;
				} else if (row.Qualifier.equals("ca.")) {
					er.property_value_numeric_qualifier = "~";
				} else if (row.Qualifier.contentEquals("?")) {
					er.property_value_numeric_qualifier = "?";
					er.keep = false;
					er.reason = "Unknown numeric qualifier";
				} else if (row.Qualifier.contentEquals(">")) {
					er.property_value_numeric_qualifier = ">";
				} else if (row.Qualifier.contentEquals(">=")) {
					er.property_value_numeric_qualifier = ">=";
				} else if (row.Qualifier.contentEquals("<=")) {
					er.property_value_numeric_qualifier = "<=";
				} else if (row.Qualifier.contentEquals("<")) {
					er.property_value_numeric_qualifier = "<";
				} else if (row.Qualifier.contentEquals("\u2264")) {
					er.property_value_numeric_qualifier = "\u2264";
				} else if (row.Qualifier.contentEquals("\u2265")) {
					er.property_value_numeric_qualifier = "\u2265";
				} else {
					System.out.println("Row " + row.Row + ", Unhandled qualifier:\t" + row.Qualifier);
				}

				if (er.property_value_numeric_qualifier != null && er.property_value_numeric_qualifier.length() > 0) {
					er.property_value_string += er.property_value_numeric_qualifier + " ";
				}
			}

			er.property_value_string += gov.epa.exp_data_gathering.parse.Parse.formatValue(er.property_value_point_estimate_original);
		}

		if (er.property_value_string != null && er.property_value_units_original != null) {
			er.property_value_string += " " + er.property_value_units_original;
		}

		if (er.property_value_string == null) {
			er.keep = false;
			er.reason = "No property_value_string";
		}
	}
}