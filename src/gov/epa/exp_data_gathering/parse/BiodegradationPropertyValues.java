package gov.epa.exp_data_gathering.parse;

import java.text.DecimalFormat;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.EstimateParser.Estimate;


/**
* @author TMARTI02
*/
public class BiodegradationPropertyValues {
	
	
	public static class ResultBinaryScore {
		public Integer score;
		public String reason;
	}
	
	
	private static void convertToContinuous(ExperimentalRecord er, Estimate estimate, Double duration) {
		// Constants
		final double DAYS = 28.0;
		final double EPS  = 0.01; // tolerance for ~28 days
		final double LOW  = 5.0;  // 5%
		final double HIGH = 95.0; // 95%

		// Setup
		er.property_name = ExperimentalConstants.strPercentageBiodegradation;
		er.property_value_units_final = ExperimentalConstants.str_dimensionless;

		er.property_value_point_estimate_original = estimate.point;
		er.property_value_min_original = estimate.min;
		er.property_value_max_original = estimate.max;

		boolean hasPoint = estimate.point != null;
		boolean hasMin   = estimate.min   != null;
		boolean hasMax   = estimate.max   != null;

		// ~28 days
		if (Math.abs(duration - DAYS) < EPS) {
			if (hasPoint) {
				setPointWithClamp(er, estimate.point);
				return;
			}
			if (hasMin && hasMax) {
				er.property_value_min_final = estimate.min;
				er.property_value_max_final = estimate.max;
				return; // dataset creation step will decide usability
			}
			if (hasMax) {
				if (estimate.max <= LOW) {
					er.property_value_point_estimate_final = 0.0;
					er.updateNote("degradation < 5%, rounded to zero");
				} else {
					er.property_value_max_final = estimate.max;
					er.keep = false;
					er.reason = "have 28 day duration with a max value > 5%";
				}
				return;
			}
			if (hasMin) {
				if (estimate.min >= HIGH) {
					er.property_value_point_estimate_final = 100.0;
					er.updateNote("degradation > 95%, rounded to 100%");
				} else {
					er.property_value_min_final = estimate.min;
					er.keep = false;
					er.reason = "have 28 day duration with a min value < 95%";
				}
				return;
			}
			return; // nothing else to do
		}

		// >28 days
		if (duration > DAYS) {
			if (hasPoint) {
				if (estimate.point <= LOW) {
					er.property_value_point_estimate_final = 0.0;
					er.updateNote("degradation (" + estimate.point + "%) < 5%, set to 0% for duration = " + duration + " days");
				} else {
					er.keep = false;
					er.updateReason("Have > 28 day duration with point estimate > 5%");
				}
			} else {
				er.keep = false;
				er.updateReason("Dont have point estimate for duration > 28 days");
			}
			return;
		}

		// <28 days
		if (hasPoint) {
			if (estimate.point >= HIGH) {
				er.property_value_point_estimate_final = 100.0;
				er.updateNote("degradation (" + estimate.point + "%) >= 95%, set to 100% for duration = " + duration + " days");
			} else {
				er.keep = false;
				er.updateReason("Have < 28 duration with point estimate < 95%");
			}
		} else {
			er.keep = false;
			er.updateReason("Dont have point estimate for duration < 28 days");
		}
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
	
	
	public static void setPropertyValues(ExperimentalRecord er, String outputMode, Estimate estimate, Double duration) {
		er.property_value_units_original=ExperimentalConstants.str_dimensionless;
		if (outputMode.equalsIgnoreCase(ExperimentalConstants.str_binary)) {
			convertToBinary(er, estimate, duration);
		} else if (outputMode.equalsIgnoreCase(ExperimentalConstants.str_continuous)) {
			convertToContinuous(er, estimate, duration);
		}
	}

	
	/**
	 * Implementation for converting record to binary
	 * 
	 * @param er
	 * @param estimate
	 * @param duration
	 */
	private static void convertToBinary(ExperimentalRecord er, Estimate estimate, Double duration) {

		er.property_name=ExperimentalConstants.strRBIODEG;

		ResultBinaryScore rbs=determineBinaryBiodegScore(estimate, duration);

		er.property_value_point_estimate_original = estimate.point;
		er.property_value_min_original = estimate.min;
		er.property_value_max_original = estimate.max;

		er.property_value_units_original = ExperimentalConstants.str_dimensionless;
		er.property_value_units_final=ExperimentalConstants.str_binary;

		if(rbs.score!=null) {
			er.property_value_point_estimate_final=(double)rbs.score;
			er.property_value_units_final=ExperimentalConstants.str_binary;
		} else {
//			System.out.println(er.casrn+"\t"+er.reason);
			er.updateReason(rbs.reason);
			er.keep=false;
		}
		
//		System.out.println(recBio.degradationValue+"\n"+JsonUtilities.gsonPretty.toJson(estimate)+"\n");

		if(estimate.min!=null && estimate.max!=null) {
			er.property_value_string=Parse.formatValue(er.property_value_min_original)+" - "+Parse.formatValue(er.property_value_max_original);
		} else if(estimate.min!=null) {
			er.property_value_string="> "+Parse.formatValue(er.property_value_min_original);
		} else if(estimate.max!=null) {
			er.property_value_string="< "+Parse.formatValue(er.property_value_max_original);
		} else if(estimate.point!=null) {
			er.property_value_string=Parse.formatValue(er.property_value_point_estimate_original);
		} 

		if(er.property_value_string!=null) {
			DecimalFormat df=new DecimalFormat("0.#");
			er.property_value_string+=" % degradation in "+ duration+ " days";
//			System.out.println(er.property_value_string);
		}
		
	}
	
	private static ResultBinaryScore determineBinaryBiodegScore(Estimate estimate, double duration) {
		
		ResultBinaryScore rbs=new ResultBinaryScore();
		int daysCutoff = 28;

		if (estimate.point != null) {
			if (estimate.point >= 60) {
				if(duration <= daysCutoff) {
					rbs.score=1;
				} else {
					rbs.reason="Point estimate > 60% but duration > 28 days";
				}
			} else {
				if (duration >= daysCutoff) {
					rbs.score=0;
				}else { // can't tell if they waited long enough
					rbs.reason="Point estimate < 60% but duration < 28 days";
				}
			}

		} else if (estimate.min != null && estimate.max != null) {
			
			if (estimate.min >= 60 && duration <= daysCutoff) {
				rbs.score=1;
			} else if (estimate.max < 60 && duration >= daysCutoff) {
				rbs.score=0;
			} else {
				rbs.reason="Can't assign score based on min and max degradation values";
			}
						
		
		} else if (estimate.max != null) {
			if (estimate.max < 60 && duration >= daysCutoff) {
				rbs.score=0;
			} else {
				rbs.reason="Can't assign score based on max degradation value";
			}
		} else if (estimate.min != null) {
			if (estimate.min >= 60 && duration <= daysCutoff) {
				rbs.score=1;
			} else {
				rbs.reason="Can't assign score based on min degradation value";
			}
		}
		return rbs;
	}

}
