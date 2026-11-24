package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;

public class KocQSARMapper implements QSARRecordMapper {


	@Override
	public ExperimentalRecord map(RecordQSAR_ToolBox2 row) {
		ExperimentalRecord er = row.createBaseRecord();

		row.setGuidelines(er);
		row.setMeasurementMethod(er);
		row.addGenericMetadata(er);

		if (row.Endpoint == null) {
			er.keep = false;
			er.reason = "Endpoint is missing";
			return er;
		}

		er.property_name = ExperimentalConstants.strKOC;

		final double cutoff = 8.0;
		final boolean exceedsCutoff = haveValueExceedingLogKocCutoff(er, cutoff);

		final String endpoint = row.Endpoint.trim();

		if (endpoint.equalsIgnoreCase("Koc") || endpoint.equalsIgnoreCase("Kd")) {
			handleKocOrKd(row, er, exceedsCutoff, cutoff);
		} else if (endpoint.equalsIgnoreCase("log Koc") || endpoint.equalsIgnoreCase("log Kd")) {
			handleLogKocOrKd(row, er, exceedsCutoff, cutoff);
		} else {
			er.keep = false;
			er.reason = "Other endpoint";
			return er;
		}

		if (row.Endpoint != null && row.Endpoint.contains("Kd")) {
			convertKdToKoc(row, er);
		}

		if (er.measurement_method == null) {
			reject(er, "measurement method unavailable", null);
		} else if (!er.measurement_method.equals("HPLC Estimation Method")
				&& !er.measurement_method.equals("Batch Equilibrium Method")) {
			reject(er, "measurement method not Batch Equilibrium Method or HPLC Estimation Method", null);
		}

		RecordQSAR_ToolBox2.unitConverter.convertRecord(er);
		return er;
	}

	private void handleKocOrKd(RecordQSAR_ToolBox2 row, ExperimentalRecord er, boolean exceedsCutoff, double cutoff) {
		if (row.Value_Unit == null || isDimensionless(row.Value_Unit)) {
			if (exceedsCutoff) {
				er.property_value_units_original = ExperimentalConstants.str_L_KG;
				er.updateNote("non-log value, original units changed from " + row.Value_Unit + " to L/kg (value > " + cutoff + ")");
			} else {
				if (isDimensionless(row.Value_Unit)) {
					er.property_value_units_original = ExperimentalConstants.str_dimensionless;
				}
				String msg = "non-log value but original units=" + row.Value_Unit
						+ " (value < " + cutoff + " so can't tell if log value or not)";
				reject(er, msg, msg);
			}
		} else if (isLogLPerKg(row.Value_Unit)) {
			er.property_value_units_original = ExperimentalConstants.str_LOG_L_KG;
			if (exceedsCutoff) {
				reject(er, "non-log value but original units=" + row.Value_Unit + " (value > " + cutoff + ")", null);
			}
		} else if (isLPerKg(row.Value_Unit)) {
			er.property_value_units_original = ExperimentalConstants.str_L_KG;
		} else if (couldNotDetermine(row.Value_Unit)) {
			reject(er, "value could not be determined", row.Value_Unit);
		} else {
			er.property_value_units_original = row.Value_Unit;
			reject(er, "Invalid units", null);
		}
	}

	private void handleLogKocOrKd(RecordQSAR_ToolBox2 row, ExperimentalRecord er, boolean exceedsCutoff, double cutoff) {
		if (row.Value_Unit == null || isDimensionless(row.Value_Unit) || isLPerKg(row.Value_Unit) || isLogLPerKg(row.Value_Unit)) {
			if (row.Value_Unit != null) {
				if (isLogLPerKg(row.Value_Unit)) {
					er.property_value_units_original = ExperimentalConstants.str_LOG_L_KG;
				} else if (isDimensionless(row.Value_Unit)) {
					er.property_value_units_original = ExperimentalConstants.str_dimensionless;
				} else {
					er.property_value_units_original = row.Value_Unit;
				}
			}

			if (exceedsCutoff) {
				reject(er, "log value and but value>" + cutoff,
						"log value but original units=" + row.Value_Unit + " (value > " + cutoff + " so can't tell if log value or not)");
			} else {
				er.property_value_units_original = ExperimentalConstants.str_LOG_L_KG;
				er.updateNote("log value, original units changed from " + row.Value_Unit + " to "
						+ ExperimentalConstants.str_LOG_L_KG);
			}
		} else {
			reject(er, "invalid units", "endpoint=log Koc but units=" + row.Value_Unit);
		}
	}

	private void convertKdToKoc(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		boolean badQualifier = row.Org_carbon_Qualifier != null
				&& (row.Org_carbon_Qualifier.equals("<") || row.Org_carbon_Qualifier.equals("\u2265"));

		if (row.Org_carbon_MeanValue == null || badQualifier) {
			er.keep = false;
			er.reason = "Kd value is provided but organic carbon fraction is unavailable to convert to Koc";
			return;
		}

		try {
			Double oc = Double.parseDouble(row.Org_carbon_MeanValue) / 100.0;
			if (oc > 1e-3) {
				if (er.property_value_point_estimate_final != null) er.property_value_point_estimate_final /= oc;
				if (er.property_value_min_final != null) er.property_value_min_final /= oc;
				if (er.property_value_max_final != null) er.property_value_max_final /= oc;
				er.experimental_parameters.put("Percentage_Organic_Carbon", row.Org_carbon_MeanValue);
				er.updateNote("Kd converted to Koc via % organic carbon = " + row.Org_carbon_MeanValue);
			} else {
				er.keep = false;
				er.reason = "Kd value is provided but organic carbon fraction is unavailable to convert to Koc";
			}
		} catch (Exception ex) {
			er.keep = false;
			er.reason = "Can't convert Kd to Koc";
		}
	}

	private void reject(ExperimentalRecord er, String reason, String note) {
		er.keep = false;
		er.reason = reason;
		if (note != null) {
			er.updateNote(note);
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

	private static boolean isLPerKg(String u) {
		String n = normUnit(u);
		return n != null && java.util.Set.of("l/kg", "ml/g", "dm3/kg", "(ml/g)", "cm3/g", "cm^3/g", "cm3.g-1", "cm3g-1", "mlg^-1").contains(n);
	}

	private static boolean isLogLPerKg(String u) {
		String n = normUnit(u);
		return n != null && java.util.Set.of("log(l/kg)").contains(n);
	}

	private static boolean isDimensionless(String u) {
		String n = normUnit(u);
		return n != null && java.util.Set.of("dimensionless").contains(n);
	}

	private static boolean couldNotDetermine(String u) {
		return u != null && u.toLowerCase(java.util.Locale.ROOT).contains("value could not be determined");
	}

	private static String normUnit(String u) {
		return u == null ? null : u.trim().toLowerCase(java.util.Locale.ROOT).replace(" ", "");
	}
}