package gov.epa.exp_data_gathering.parse.QSAR_ToolBox2;

import java.text.DecimalFormat;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.BiodegradationPropertyValues;
import gov.epa.exp_data_gathering.parse.EstimateParser.Estimate;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;

public class BiodegQSARMapper implements QSARRecordMapper {
	
	ParseQSAR_ToolBox2 pqt=null;
	
	public BiodegQSARMapper(ParseQSAR_ToolBox2 pqt) {
		this.pqt=pqt;
	}
	
	@Override
	public ExperimentalRecord map(RecordQSAR_ToolBox2 row) {
		ExperimentalRecord er = row.createBaseRecord();

		row.setSourceInformation(er);
		row.setIdentifiers(er);

		er.property_name = pqt.propertyName;
		er.source_name = pqt.sourceName;
		row.lastUpdated = pqt.fileDate;

		if (row.Details_on_results != null && row.Details_on_results.toLowerCase().contains("biowin")) {
			er.keep = false;
			er.updateReason("possible BIOWIN estimation");
		}

		if (row.Reliability != null) {
			er.experimental_parameters.put("Reliability", row.Reliability);
		}

		if (row.GLP_compliance != null) {
			er.experimental_parameters.put("Test guideline compliance", row.GLP_compliance);
			if (RecordQSAR_ToolBox2.omitBasedOnGLPCompliance && !row.GLP_compliance.startsWith("yes")) {
				er.keep = false;
				er.updateReason("GLP compliance = " + row.GLP_compliance);
			}
		}

		if (row.GLP_compliance == null && RecordQSAR_ToolBox2.omitBasedOnGLPCompliance) {
			er.keep = false;
			er.updateReason("GLP compliance is missing");
		}

		if (!"Biodegradation NITE".equals(er.original_source_name)) {
			if (row.Oxygen_conditions != null) {
				er.experimental_parameters.put("Oxygen conditions", row.Oxygen_conditions);
				if (!row.Oxygen_conditions.startsWith("aerobic")) {
					er.keep = false;
					er.updateReason("Oxygen conditions = " + row.Oxygen_conditions);
				}
			} else {
				er.keep = false;
				er.updateReason("Oxygen conditions is N/A");
			}
		}

		if (row.Interpretation_of_results != null) {
			if (row.Interpretation_of_results.equals("Other Interpretation of results")) {
				row.Interpretation_of_results = row.Interpretation_of_results_other;
			}
			er.experimental_parameters.put("Interpretation of results", row.Interpretation_of_results);
		}

		er.property_value_units_original = ExperimentalConstants.str_dimensionless;
		row.setPropertyValues(er, false);

		if (er.measurement_method == null) {
			er.keep = false;
			er.updateReason("measurement method is missing");
		}

		Double duration = getDuration(row);
		if (duration == null) {
			er.keep = false;
			er.updateReason("Duration is unavailable");
		} else if (row.Duration_Unit == null) {
			er.keep = false;
			er.updateReason("Duration_Unit is null");
		}

		DecimalFormat df = new DecimalFormat("0.#");
		if (duration != null && er.property_value_string != null) {
			er.property_value_string += " in " + df.format(duration) + " days";
		}

		if (er.property_value_string != null) {
			er.property_value_string = er.property_value_string.replace("Dimensionless", "% degradation");
		}

		normalizeBiodegParameters(er);

		if (row.Test_guideline != null) {
			applyGuidelineFilter(row, er, pqt);
		} else {
			er.keep = false;
			er.updateReason("guideline unavailable");
		}

		if (row.Title != null) {
			er.document_name = row.Title;
			if (row.Year != null) {
				try {
					er.document_name += " " + df.format(Double.parseDouble(row.Year));
				} catch (Exception ex) {
					er.document_name += " " + row.Year;
				}
			}
		}

		if (!"%".equals(row.Value_Unit)) {
			er.keep = false;
			er.reason = "Units = " + row.Value_Unit;
		}

		if (er.keep) {
			Estimate estimate = getDegradationEstimate(row);
			BiodegradationPropertyValues.setPropertyValues(er, estimate, duration, row.Interpretation_of_results);
		}

		er.measurement_method = null;
		return er;
	}

	private void normalizeBiodegParameters(ExperimentalRecord er) {
		er.experimental_parameters.remove(ExperimentalConstants.expParamGuideline);
		er.experimental_parameters.remove(ExperimentalConstants.expParamExposureType);
		er.experimental_parameters.remove(ExperimentalConstants.expParamTestLocation);
		er.experimental_parameters.remove(ExperimentalConstants.expParamExposureDuration);
		er.experimental_parameters.remove(ExperimentalConstants.expParamWetDry);
		er.experimental_parameters.remove(ExperimentalConstants.expParamMeasurementMethod);
	}

	private void applyGuidelineFilter(RecordQSAR_ToolBox2 row, ExperimentalRecord er, ParseQSAR_ToolBox2 pqt) {
		if (row.Test_guideline.equals("Other Test Guideline")) {
			if (row.Test_guideline_other != null) {
				er.experimental_parameters.put(ExperimentalConstants.expParamGuideline, row.Test_guideline_other);
			}
		} else {
			er.experimental_parameters.put(ExperimentalConstants.expParamGuideline, row.Test_guideline);
		}

		String tg = (String) er.experimental_parameters.get(ExperimentalConstants.expParamGuideline);

		if (tg != null && pqt.outputTestGuideline != null
				&& !tg.contains(pqt.outputTestGuideline)
				&& !tg.contains(pqt.outputTestGuideline.replace(" ", ""))) {
			er.keep = false;
			er.updateReason("wrong guideline");
		} else if (pqt.outputTestGuideline != null) {
			BiodegradationPropertyValues.methods.add(er.measurement_method);

			if (pqt.outputTestGuideline.equals(ExperimentalConstants.str301F)
					&& !BiodegradationPropertyValues.OECD301Classifier.isValidFor301F(er.measurement_method)) {
				er.keep = false;
				er.updateReason(er.measurement_method + " is not valid for " + pqt.outputTestGuideline);
			} else if (pqt.outputTestGuideline.equals(ExperimentalConstants.str301C)
					&& !BiodegradationPropertyValues.OECD301Classifier.isValidFor301C(er.measurement_method)) {
				er.keep = false;
				er.updateReason(er.measurement_method + " is not valid for " + pqt.outputTestGuideline);
			}
		}
	}

	Estimate getDegradationEstimate(RecordQSAR_ToolBox2 row) {
		Estimate estimate = new Estimate();

		if (row.Value_Unit == null || !row.Value_Unit.trim().equals("%")) {
			System.out.println(row.Endpoint + "\t" + row.Value_Unit + "\tNot % units!");
			return estimate;
		}

		if (row.Value_MeanValue != null && !row.Value_MeanValue.trim().isEmpty()) {
			double meanVal = Double.parseDouble(row.Value_MeanValue.trim());
			String qual = (row.Value_Qualifier == null) ? "" : row.Value_Qualifier.trim();

			if (qual.isEmpty() || qual.equals("ca.") || qual.equals("ca") || qual.equals("~") || qual.equals("approx.")) {
				estimate.point = meanVal;
			} else if (qual.equals("<") || qual.equals("\u2264")) {
				estimate.max = meanVal;
			} else if (qual.equals(">") || qual.equals("\u2265")) {
				estimate.min = meanVal;
			} else {
				System.out.println(row.CAS_Number + "\tUnhandled qualifier: " + row.Value_Qualifier + "\t"
						+ row.Value_MinValue + "\t" + row.Value_MaxValue);
			}
		}

		if (row.Value_MinValue != null && !row.Value_MinValue.trim().isEmpty() && estimate.min == null) {
			estimate.min = Double.parseDouble(row.Value_MinValue.trim());
		}

		if (row.Value_MaxValue != null && !row.Value_MaxValue.trim().isEmpty() && estimate.max == null) {
			estimate.max = Double.parseDouble(row.Value_MaxValue.trim());
		}

		return estimate;
	}

	Double getDuration(RecordQSAR_ToolBox2 row) {
		Double duration = null;

		if (row.Duration_MeanValue != null) {
			duration = Double.parseDouble(row.Duration_MeanValue);
		}

		if (row.Duration_Unit == null) {
			return null;
		} else if (row.Duration_Unit.equals("d")) {
		} else if (row.Duration_Unit.equals("min")) {
			if (duration != null) duration /= (24.0 * 60);
		} else if (row.Duration_Unit.equals("h")) {
			if (duration != null) duration /= 24.0;
		} else if (row.Duration_Unit.equals("wk")) {
			if (duration != null) duration *= 7.0;
		} else if (row.Duration_Unit.equals("mo")) {
			if (duration != null) duration *= 30;
		} else {
			System.out.println("Bad duration units:" + row.Duration_Unit);
		}
		return duration;
	}

	public static class ResultBinaryScore {
		public Integer score;
		public String reason;

		public ResultBinaryScore() {
		}

		public ResultBinaryScore(Integer score, String reason) {
			this.score = score;
			this.reason = reason;
		}
	}

	public enum BiodegEvidence {
		NONE, BELOW_60, ABOVE_60, CONFLICT
	}

	ResultBinaryScore determineResultBinaryScore60PercentWithin28days(Estimate estimate, Double duration) {
		ResultBinaryScore rbs = new ResultBinaryScore();
		int daysCutoff = 28;

		if (duration == null) {
			rbs.reason = "Duration is null";
			return rbs;
		}

		if (estimate.point != null) {
			if (estimate.point >= 60) {
				if (duration <= daysCutoff) {
					rbs.score = 1;
				} else {
					rbs.reason = "Point estimate > 60% but duration > 28 days";
				}
			} else {
				if (duration >= daysCutoff) {
					rbs.score = 0;
				} else {
					rbs.reason = "Point estimate < 60% but duration < 28 days";
				}
			}
		} else if (estimate.min != null && estimate.max != null) {
			if (estimate.min >= 60 && duration <= daysCutoff) {
				rbs.score = 1;
			} else if (estimate.max < 60 && duration >= daysCutoff) {
				rbs.score = 0;
			} else {
				rbs.reason = "Can't assign score based on min and max degradation values";
			}
		} else if (estimate.max != null) {
			if (estimate.max < 60 && duration >= daysCutoff) {
				rbs.score = 0;
			} else {
				rbs.reason = "Can't assign score based on max degradation value";
			}
		} else if (estimate.min != null) {
			if (estimate.min >= 60 && duration <= daysCutoff) {
				rbs.score = 1;
			} else {
				rbs.reason = "Can't assign score based on min degradation value";
			}
		} else {
			rbs.reason = "Missing or non-percentage degradation value";
		}
		return rbs;
	}

	private static void setPointWithClamp(ExperimentalRecord er, double point) {
		if (point > 100.0) {
			er.property_value_point_estimate_final = 100.0;
			er.updateNote("Degradation exceeds 100%, setting to 100%");
		} else if (point < 0.0) {
			er.property_value_point_estimate_final = 0.0;
			er.updateNote("Degradation < 0%, setting to 0%");
		} else {
			er.property_value_point_estimate_final = point;
		}
	}
}