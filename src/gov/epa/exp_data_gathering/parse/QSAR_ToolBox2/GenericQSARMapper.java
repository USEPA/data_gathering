package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.text.WordUtils;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ParameterValue;

public class GenericQSARMapper implements QSARRecordMapper {

	private final ParseQSAR_ToolBox2 pqt;

	public GenericQSARMapper(ParseQSAR_ToolBox2 pqt) {
		this.pqt = pqt;
	}

	@Override
	public ExperimentalRecord map(RecordQSAR_ToolBox2 row) {
		ExperimentalRecord er = row.createBaseRecord();

		row.setGuidelines(er);
		row.setMeasurementMethod(er);
		row.addGenericMetadata(er);

		if (row.EndpointPath == null) {
			er.keep = false;
			er.reason = "EndpointPath not set";
			return er;
		}

		switch (row.EndpointPath) {
		
			case "Physical Chemical Properties#Partition Coefficient:#N-Octanol/Water":
				er.property_name = ExperimentalConstants.strLogKOW;
				row.setPropertyValues(er, true);
				setPropertyValueLogKow(row, er);
				row.setExperimentalParameters(er);
				break;

			case "Physical Chemical Properties#Water solubility":
				er.property_name = ExperimentalConstants.strWaterSolubility;
				row.setPropertyValues(er, true);
				setPropertyValueWS(row, er);
				row.setExperimentalParameters(er);
				break;

			default:
				er.keep = false;
				er.reason = "Unhandled EndpointPath: " + row.EndpointPath;
				break;
		}

		er.date_accessed = row.lastUpdated;
		return er;
	}

	// ---------------------------------------------------------------------
	// Moved from RecordQSAR_ToolBox2 because only GenericQSARMapper needs them
	// ---------------------------------------------------------------------

	private void setPropertyValueLogKow(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Type == null) {
			er.keep = false;
			er.reason = "Pow type missing";
			return;
		}

		if (row.Type.equals("log Pow")) {
			er.property_value_units_original = ExperimentalConstants.str_LOG_UNITS;
		} else if (row.Type.equals("Pow")) {
			er.property_value_units_original = ExperimentalConstants.str_dimensionless;
		}

		RecordQSAR_ToolBox2.unitConverter.convertRecord(er);

		if (row.Type.equals("Pow") && er.property_value_point_estimate_original != null
				&& er.property_value_point_estimate_original == 0.0) {
			er.keep = false;
			er.reason = "Pow=0 and Type=Pow";
		}
	}

	private void setPropertyValueWS(RecordQSAR_ToolBox2 row, ExperimentalRecord er) {
		if (row.Value_Unit != null) {

			if (er.property_value_string != null) {
				er.property_value_string += " " + row.Value_Unit;
			}

			if (row.Value_Unit.contains("salt water")) {
				er.keep = false;
				er.reason = "salt water";
			}

			List<String> wtpct = Arrays.asList("w/w-%", "% w/w", "W/W", "(% w/w)", "(%w/w)", "% w/w",
					"%wt", "% w/w solution", "% by weight", "% (m/m)", "% [m/m]", "%(m/m)", "Wt%", "w/w",
					"W/W %", "mass%", "(w/w)", "S/mass %", "S/mass %", "S/mass%", "wt. %", "% weight",
					"% w/w of solution", "%w/w of solution", "% (w/w)", "Mass %", "mass %", "wt.%", "wt %",
					"w/w%", "wt%", "%w/w", "% w/w", "wt-");

			List<String> volpct = Arrays.asList("vol%");

			if (wtpct.contains(row.Value_Unit)) {
				er.property_value_units_original = ExperimentalConstants.str_pctWt;
			} else if (volpct.contains(row.Value_Unit)) {
				er.property_value_units_original = ExperimentalConstants.str_pctVol;
			} else if (row.Value_Unit.equals("µg a.i./L")) {
				er.property_value_units_original = ExperimentalConstants.str_ug_L;
			} else {
				er.property_value_units_original = row.Value_Unit;
			}
		}

		RecordQSAR_ToolBox2.unitConverter.convertRecord(er);
	}
}