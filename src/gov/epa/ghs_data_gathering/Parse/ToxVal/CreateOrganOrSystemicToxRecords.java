package gov.epa.ghs_data_gathering.Parse.ToxVal;

import java.util.ArrayList;

import gov.epa.ghs_data_gathering.API.Chemical;
import gov.epa.ghs_data_gathering.API.ScoreRecord;

public class CreateOrganOrSystemicToxRecords {


	/* I want to convert this Stata code to Java code:

	 gen study_dur_in_days=.
	 replace study_dur_in_days=study_duration_value if study_duration_units=="day"
	 replace study_dur_in_days=study_duration_value*7 if study_duration_units=="week"
	 replace study_dur_in_days=study_duration_value*30 if study_duration_units=="month"
	 replace study_dur_in_days=study_duration_value*365 if study_duration_units=="year"
	 replace study_dur_in_days=study_duration_value/24 if study_duration_units=="hour"
	 replace study_dur_in_days=study_duration_value/1440 if study_duration_units=="minute"

	 Here is my attempt...

	 */

	public static void createDurationRecord(Chemical chemical, RecordToxVal tr) {

		double study_dur_in_days=-1.0;
		/* I think there is no way to make a variable blank so I made it -1, which is not ideal. */

		double study_duration_value = Double.parseDouble(tr.study_duration_value);
		/* Do I need to do this to change it from whatever format it was in into double? */

		if (tr.study_duration_units.contentEquals("day")) {
			study_dur_in_days=study_duration_value;
		} else if (tr.study_duration_units.contentEquals("week")) {
			study_dur_in_days=study_duration_value*7.0;
		} else if (tr.study_duration_units.contentEquals("month")) {
			study_dur_in_days=study_duration_value*30.0;
		} else if (tr.study_duration_units.contentEquals("year")) {
			study_dur_in_days=study_duration_value*365.0;
		} else if (tr.study_duration_units.contentEquals("hour")) {
			study_dur_in_days=study_duration_value/24.0;
		} else if (tr.study_duration_units.contentEquals("minute")) {
			study_dur_in_days=study_duration_value/1440.0;
		} else {
			System.out.println("unknown units="+tr.study_duration_units);
			return;
		}

		
		if (!tr.toxval_type.contentEquals("NOAEL") && !tr.toxval_type.contentEquals("LOAEL")) {
			return;//not a valid record for systemic tox
		}
				
		ScoreRecord sr=createScoreRecord(chemical, tr);//create generic score record
		
		sr.duration=study_dur_in_days;
		sr.durationUnits="days";
				
		
		if (tr.exposure_route.contentEquals("oral")) {
			if (study_dur_in_days <= 91.0 && study_dur_in_days >= 89.0) {
				/*Got error when tried to use "=". It said "<=" was expected.*/			
				setNinetyOralScore(sr, chemical);
				chemical.scoreSystemic_Toxicity_Repeat_Exposure.records.add(sr);								
			} else if (study_dur_in_days <= 50.0 && study_dur_in_days >= 40.0) {
				setFortyFiftyOralScore(sr, chemical);
				chemical.scoreSystemic_Toxicity_Repeat_Exposure.records.add(sr);
			} else if (study_dur_in_days <= 31.0 && study_dur_in_days >= 27.0) {
				setTwentyEightOralScore(sr,chemical);
			}
				
			
		} else if (tr.exposure_route.contentEquals("dermal")) {

			if (study_dur_in_days <= 91.0 && study_dur_in_days >= 89.0) {
				/*Got error when tried to use "=". It said "<=" was expected.*/			
				setNinetyDermalScore(sr, chemical);
				chemical.scoreSystemic_Toxicity_Repeat_Exposure.records.add(sr);								
			} else if (study_dur_in_days <= 50.0 && study_dur_in_days >= 40.0) {
				setFortyFiftyDermalScore(sr, chemical);
				chemical.scoreSystemic_Toxicity_Repeat_Exposure.records.add(sr);
			} else if (study_dur_in_days <= 31.0 && study_dur_in_days >= 27.0) {
				setTwentyEightDermalScore(sr,chemical);
			}

		} else if (tr.exposure_route.contentEquals("inhalation")) {
			if (study_dur_in_days <= 91.0 && study_dur_in_days >= 89.0) {
				/*Got error when tried to use "=". It said "<=" was expected.*/			
				setNinetyInhalationScore(sr, chemical);
				chemical.scoreSystemic_Toxicity_Repeat_Exposure.records.add(sr);								
			} else if (study_dur_in_days <= 50.0 && study_dur_in_days >= 40.0) {
				setFortyFiftyInhalationScore(sr, chemical);
				chemical.scoreSystemic_Toxicity_Repeat_Exposure.records.add(sr);
			} else if (study_dur_in_days <= 31.0 && study_dur_in_days >= 27.0) {
				setTwentyEightInhalationScore(sr,chemical);
			}
			
		}
				
			
		if (tr.toxval_type.contentEquals("NOAEL") || tr.toxval_type.contentEquals("LOAEL")) {
			if (tr.toxval_units.contentEquals("mg/kg") &&
					tr.exposure_route.contentEquals("oral") &&
					study_dur_in_days <= 1.0 &&
					study_dur_in_days > 0.0) {		
				setSingleDoseOralScore(sr,chemical);	
			} else if(tr.toxval_units.contentEquals("mg/kg") &&
					tr.exposure_route.contentEquals("oral") &&
					study_dur_in_days <= 1.0 &&
					study_dur_in_days > 0.0) {
				setSingleDoseDermalScore(sr,chemical);
			} else if(tr.toxval_units.contentEquals("mg/L") &&
					tr.exposure_route.contentEquals("oral") &&
					study_dur_in_days <= 1.0 &&
					study_dur_in_days > 0.0) {
				setSingleDoseInhalationScore(sr,chemical);
			}
		}	
		
		if (sr.score!=null) {
//			System.out.println(sr.scoreToInt());
		} else {
//			System.out.println("no score, duration="+study_dur_in_days+" days");
			//Cant add record since score not assigned (doesnt fall into above if statements)
			return;
		}
				
		if (study_dur_in_days>1) {
			chemical.scoreSystemic_Toxicity_Repeat_Exposure.records.add(sr);
		} else {
			chemical.scoreSystemic_Toxicity_Single_Exposure.records.add(sr);
		}

		
	}



	private static ScoreRecord createScoreRecord(Chemical chemical, RecordToxVal tr) {

		ScoreRecord sr = new ScoreRecord();
		sr = new ScoreRecord();
		sr.source = ScoreRecord.sourceToxVal;
		sr.sourceOriginal=tr.source;
		
		sr.route=tr.exposure_route;

		sr.valueMassOperator=tr.toxval_numeric_qualifier;
		sr.valueMass = Double.parseDouble(tr.toxval_numeric);
		sr.valueMassUnits = tr.toxval_units;
		sr.note=ParseToxVal.createNote(tr);
				
		
		
		return sr;
	}



	

	private static void setNinetyOralScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);
		
		
		if (dose < 10) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg-day) < 10 mg/kg-day";
		} else if (dose >= 10 && dose <= 100) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "10 mg/kg-day < NOAEL or LOAEL (" + strDose + " mg/kg-day) <= 100 mg/kg-day";
		} else if (dose > 100) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg-day) >  100 mg/kg-day";
		} else { 
			System.out.println(chemical.CAS + "\tNinetyOral\t" + strDose);
		}
	}

	

	private static void setFortyFiftyOralScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	
		

		if (dose < 20) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg-day) < mg/kg-day";
		} else if (dose >= 20 && dose <= 200) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "20 mg/kg-day < NOAEL or LOAEL (" + strDose + " mg/kg-day) <= 200 mg/kg-day";
		} else if (dose > 200) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg-day) >  100 mg/kg-day";
		} else { 
			System.out.println(chemical.CAS + "\tFortyFiftyOral\t" + strDose);
		}
	}


	private static void setTwentyEightOralScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	
	
		if (dose < 30) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg-day) < 30 mg/kg-day";
		} else if (dose >= 30 && dose <= 300) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "30 mg/kg-day < NOAEL or LOAEL (" + strDose + " mg/kg-day) <= 300 mg/kg-day";
		} else if (dose > 300) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg-day) >  300 mg/kg-day";
		} else { 
			System.out.println(chemical.CAS + "\tTwentyEightOral\t" + strDose);
		}
	}
	

	private static void setNinetyDermalScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	
		
		if (dose < 20) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg-day) < 20 mg/kg-day";
		} else if (dose >= 20 && dose <= 200) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "20 mg/kg-day < NOAEL or LOAEL (" + strDose + " mg/kg-day) <= 200 mg/kg-day";
		} else if (dose > 200) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg-day) >  200 mg/kg-day";
		} else { 
			System.out.println(chemical.CAS + "\tNinetyDermal\t" + strDose);
		}
	}


	private static void setFortyFiftyDermalScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);
		

		if (dose < 40) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg-day) < 40 mg/kg-day";
		} else if (dose >= 40 && dose <= 400) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "40 mg/kg-day < NOAEL or LOAEL (" + strDose + " mg/kg-day) <= 400 mg/kg-day";
		} else if (dose > 400) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg-day) >  400 mg/kg-day";
		} else { 
			System.out.println(chemical.CAS + "\tFortyFiftyDermal\t" + strDose);
		}
	}

	

	private static void setTwentyEightDermalScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);


		if (dose < 60) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg-day) < 60 mg/kg-day";
		} else if (dose >= 60 && dose <= 600) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "60 mg/L < NOAEL or LOAEL (" + strDose + " mg/kg-day) <= 600 mg/kg-day";
		} else if (dose > 600) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg-day) >  600 mg/kg-day";
		} else { 
			System.out.println(chemical.CAS + "\tTwentyEightDermal\t" + strDose);
		}
	}


	private static void setNinetyInhalationScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	

		if (dose < 0.2) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/L-day) < 0.2 mg/L-day";
		} else if (dose >= 0.2 && dose <= 1) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "0.2 mg/L-day < NOAEL or LOAEL (" + strDose + " mg/L-day) <= 1 mg/L-day";
		} else if (dose > 1) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/L-day) >  1 mg/L-day";
		} else { 
			System.out.println(chemical.CAS + "\tNinetyInhalation\t" + strDose);
		}
	}

	

	private static void setFortyFiftyInhalationScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	

		if (dose < 0.4) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/L-day) < 0.4 mg/L-day";
		} else if (dose >= 0.4 && dose <= 2) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "0.4 mg/L-day < NOAEL or LOAEL (" + strDose + " mg/L-day) <= 2 mg/L-day";
		} else if (dose > 2) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/L-day) >  2 mg/L-day";
		} else { 
			System.out.println(chemical.CAS + "\tFortyFiftyInhalation\t" + strDose);
		}
	}


	private static void setTwentyEightInhalationScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	

		if (dose < 0.6) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/L-day) < 0.6 mg/L-day";
		} else if (dose >= 0.6 && dose <= 3) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "0.6 mg/L-day < NOAEL or LOAEL (" + strDose + " mg/L-day) <= 3 mg/L-day";
		} else if (dose > 3) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/L-day) >  3 mg/L-day";
		} else { 
			System.out.println(chemical.CAS + "\tTwentyEightInhalation\t" + strDose);
		}
	}



	private static void setSingleDoseOralScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	

		if (dose <= 300) {
			sr.score = ScoreRecord.scoreVH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg) < 300 mg/kg";
		} else if (dose > 300 && dose <= 2000) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "300 mg/kg < NOAEL or LOAEL (" + strDose + " mg/kg) <= 2000 mg/kg";
		} else if (dose > 2000 && dose <= 3000) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "2000 mg/kg < NOAEL or LOAEL (" + strDose + " mg/kg) <= 3000 mg/kg";
		} else if (dose > 3000) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg) >  3000 mg/kg";
		} else { 
			System.out.println(chemical.CAS + "\tSingleDoseOral\t" + strDose);
		}
	}

	


	private static void setSingleDoseDermalScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	

		if (dose <= 1000) {
			sr.score = ScoreRecord.scoreVH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/kg) < 1000 mg/kg";
		} else if (dose > 1000 && dose <= 2000) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "1000 mg/kg < NOAEL or LOAEL (" + strDose + " mg/kg) <= 2000 mg/kg";
		} else if (dose > 2000 && dose <= 3000) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "2000 mg/kg < NOAEL or LOAEL (" + strDose + " mg/kg) <= 3000 mg/kg";
		} else if (dose > 3000) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/kg) >  3000 mg/kg";
		} else { 
			System.out.println(chemical.CAS + "\tSingleDoseDermal\t" + strDose);
		}
	}

	

	private static void setSingleDoseInhalationScore(ScoreRecord sr, Chemical chemical) {

		sr.rationale = "route: " + sr.route + ", ";
		double dose = sr.valueMass;
		String strDose = ParseToxVal.formatDose(dose);	

		if (dose <= 10) {
			sr.score = ScoreRecord.scoreVH;
			sr.rationale = "NOAEL or LOAEL" + " (" + strDose + " mg/L) < 10 mg/L";
		} else if (dose > 10 && dose <= 20) {
			sr.score = ScoreRecord.scoreH;
			sr.rationale = "10 mg/L < NOAEL or LOAEL (" + strDose + " mg/L) <= 20 mg/L";
		} else if (dose > 20 && dose <= 30) {
			sr.score = ScoreRecord.scoreM;
			sr.rationale = "20 mg/L < NOAEL or LOAEL (" + strDose + " mg/L) <= 30 mg/L";
		} else if (dose > 30) {
			sr.score = ScoreRecord.scoreL;
			sr.rationale = "NOAEL or LOAEL" + "(" + strDose + " mg/L) >  30 mg/L";
		} else { 
			System.out.println(chemical.CAS + "\tSingleDoseInhalation\t" + strDose);
		}
	}
}

