package gov.epa.ghs_data_gathering.Parse.ToxVal.ParseTable_toxval;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Vector;

import gov.epa.api.Chemical;
import gov.epa.api.Chemicals;
import gov.epa.api.Score;
import gov.epa.api.ScoreRecord;
import gov.epa.ghs_data_gathering.Parse.ToxVal.ParseToxValDB;
import gov.epa.ghs_data_gathering.Parse.ToxVal.Utilities;


public class ParseToxVal  {

	static CalculateRiskAssessmentClass2  crac2=new CalculateRiskAssessmentClass2();

	void getRecordsForCAS(String CAS, String filePathDatabaseAsText, String filePathRecordsForCAS) {

		try {

			BufferedReader br = new BufferedReader(new FileReader(filePathDatabaseAsText));

			FileWriter fw = new FileWriter(filePathRecordsForCAS);

			String header = br.readLine();

			fw.write(header + "\r\n");

			int colCAS = Utilities.FindFieldNumber(header, "casrn", "\t");
			System.out.println(colCAS);

			while (true) {

				String Line = br.readLine();

				//				System.out.println(Line);

				if (Line == null)
					break;

				String[] vals = Line.split("\t");

				String currentCAS = vals[colCAS];

				if (currentCAS.contentEquals(CAS)) {
					System.out.println(Line);
					fw.write(Line + "\r\n");
				}

			}
			br.close();
			fw.flush();
			fw.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	void goThroughRecords(String filepath, String destFilepathJSON, String destFilepathTXT,String versionToxVal) {

		try {

			BufferedReader br = new BufferedReader(new FileReader(filepath));
			String header = br.readLine();

			Chemical chemical = new Chemical();

			while (true) {

				String Line = br.readLine();

				//				System.out.println(Line);

				if (Line == null)
					break;

				RecordToxVal r = RecordToxVal.createRecord(header, Line);

				Score score = null;

				createScoreRecord(chemical, r,versionToxVal);

			}

			chemical.writeChemicalToJsonFile(destFilepathJSON);
			chemical.toFlatFile(destFilepathTXT, "\t");

			br.close();

		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

//	void goThroughRecordsMultipleChemicals(String filepathText, String destfilepathJson, String destfilepathText,
//			Vector<String> casList,String versionToxVal) {
//
//		try {
//
//			//			FileInputStream inputStream = new FileInputStream(new File(filepathXLSX));
//			//			Workbook workbook = new XSSFWorkbook(inputStream);
//			//			Sheet firstSheet = workbook.getSheetAt(0);
//			//			Row rowHeader=firstSheet.getRow(0);
//			//			for (int i=0;i<rowHeader.getLastCellNum();i++) {
//			//				System.out.println(rowHeader.getCell(i));
//			//			}
//
//			BufferedReader br = new BufferedReader(new FileReader(filepathText));
//
//			String header = br.readLine();
//
//			String[] hlist = header.split("\t");
//
//			Chemicals chemicals = new Chemicals();
//
//			Chemical chemical = new Chemical();
//
//			String oldCAS = "";
//
//			while (true) {
//
//				String Line = br.readLine();
//				//				System.out.println(Line);
//
//				if (Line == null)
//					break;
//
//				RecordToxVal r = RecordToxVal.createRecord(header, Line);
//
//				if (!casList.contains(r.casrn))
//					continue;
//
//				if (!r.casrn.contentEquals(oldCAS)) {
//					chemical = new Chemical();
//					chemical.CAS = r.casrn;
//					chemical.name = r.name;
//					chemicals.add(chemical);
//					oldCAS = r.casrn;
//				}
//
//				createScoreRecord(chemical, r,versionToxVal);
//				//				System.out.println(Line);
//
//			}
//
//			chemicals.writeToFile(destfilepathJson);
//			chemicals.toFlatFile(destfilepathText, "\t");
//			//			writeChemicalToFile(chemical, destfilepath);
//
//			br.close();
//
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//
//	}


	// Deleted the NeuroCriticalEffect commented code because that code is included elsewhere.
	


	public static void createScoreRecord(Chemical chemical, RecordToxVal r,String versionToxVal) {
		
		//2023-11-16- refactor to remove reliance on human_eco because it's not reliable
		
		if ((r.species_supercategory.toLowerCase().contains("fish") ||
				r.species_supercategory.toLowerCase().contains("crustacean") ||
				r.species_supercategory.toLowerCase().contains("algae"))) {

			if (versionToxVal.equals(ParseToxValDB.v8)) {

				if (r.toxval_units.contentEquals("mg/L") && r.habitat.contentEquals("aquatic")) {

					if (r.species_supercategory.toLowerCase().contains("standard test species") && 
							!r.species_supercategory.toLowerCase().contains("exotic") &&
							!r.species_supercategory.toLowerCase().contains("nuisance") &&
							!r.species_supercategory.toLowerCase().contains("invasive"))
					handleEco(chemical, r);
				}
			} else if (versionToxVal.equals(ParseToxValDB.v94) || versionToxVal.equals(ParseToxValDB.v96)) {
				
				if (r.toxval_units.contentEquals("mg/m3") && CreateAquaticToxicityRecords.validAquaticSpeciesToxvalv94(r.species_scientific)) {
					try {
						double dvalue=Double.parseDouble(r.toxval_numeric);
						r.toxval_numeric = dvalue/1000.0+"";
						r.toxval_units="mg/L";
						handleEco(chemical, r);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					if(ParseToxValDB.debug)
						System.out.println(r.toxval_units+"\tbad units for aquatic tox\t"+r.toxval_type+"\t"+r.species_common);
				}
			}
			
		} else if(r.human_eco.equals("human health")) {
			handleHuman(chemical, r);
		} else {
			//TODO add print
		}
	}

	private static void handleEco(Chemical chemical, RecordToxVal r) {
		//note there isnt any eco in toxval anymore
		CreateAquaticToxicityRecords.createDurationRecord(chemical, r);			
	}
	
	/*
	 * if((r.toxval_type.contentEquals("LC50") ||
	 * r.toxval_type.contentEquals("EC50")) &&
	 * (r.study_type.toLowerCase().contains("acute") ||
	 * r.study_duration_class.toLowerCase().contains("acute"))) {
	 */
			// if (r.risk_assessment_class.contentEquals("acute")) {
			//	|| r.risk_assessment_class.contentEquals("mortality:acute")
			//	|| r.risk_assessment_class.contentEquals("growth:acute")
			//	|| r.risk_assessment_class.contentEquals("reproduction:acute")
			//	|| r.risk_assessment_class.contentEquals("ecotoxicity invertebrate")
			//	|| r.risk_assessment_class.contentEquals("ecotoxicity plants")) {
			
	/*
	 * CreateAquaticToxicityRecords.createAquaticToxAcuteRecords(chemical, r);
	 * 
	 * 
	 * } else if((r.toxval_type.contentEquals("NOEC") ||
	 * r.toxval_type.contentEquals("LOEC")) &&
	 * (r.study_type.toLowerCase().contains("chronic") ||
	 * r.study_duration_class.toLowerCase().contains("chronic"))) {
	 */
			//				if (r.risk_assessment_class.contentEquals("chronic")
			//						|| r.risk_assessment_class.contentEquals("mortality:chronic")
			//						|| r.risk_assessment_class.contentEquals("growth:chronic")
			//						|| r.risk_assessment_class.contentEquals("reproduction:chronic")
			//						|| r.risk_assessment_class.contentEquals("ecotoxicity invertebrate")
			//						|| r.risk_assessment_class.contentEquals("ecotoxicity plants")) {
	/*
	 * CreateAquaticToxicityRecords.createAquaticToxChronicRecords(chemical, r);
	 * 
	
		}
	}
	
	*/

	/*
	 * Added: && r.human_eco.contentEquals("human health") because there is at least
	 * one eco entry labeled "acute"  -Leora 4/23/20
	 * 
	 * I'm not quite sure whether things such as the inclusion criteria for
	 * human_eco should go here or whether they should be located in the code for
	 * the class.
	 * 
	 */

	//
	//			} else if (r.risk_assessment_class.contentEquals("repeat dose")
	//					|| r.risk_assessment_class.contentEquals("short-term")
	//					|| r.risk_assessment_class.contentEquals("subacute")
	//					|| r.risk_assessment_class.contentEquals("subchronic")) {
	//				CreateOrganOrSystemicToxRecords.createDurationRecord(chemical, r);

	//} else if (r.risk_assessment_class.contentEquals("cancer")) {

	
	private static void handleHuman(Chemical chemical, RecordToxVal r) {
		if (r.toxval_type.contentEquals("LD50") || r.toxval_type.contentEquals("LC50")) {
			CreateAcuteMammalianToxicityRecords.createAcuteMammalianToxicityRecords(chemical, r);
		} else if (r.toxval_type.toLowerCase().contains("cancer slope factor") ||
				r.toxval_type.toLowerCase().contains("cancer unit risk")) {
//			System.out.println("creating cancer record");
			CreateCancerRecords.createCancerRecords(chemical, r);
		} else if (r.study_duration_class.toLowerCase().contains("reproduct") ||//NOT in v96
				r.study_duration_class.toLowerCase().contains("multigeneration") ||//NOT in v96
				r.study_duration_class.toLowerCase().contains("developmental") ||// in v96
				r.toxval_subtype.toLowerCase().contains("reproduct") ||//NOT in v96
				r.toxval_subtype.toLowerCase().contains("developmental") ||//in v96
				r.toxval_subtype.toLowerCase().contains("multigeneration") ||//NOT in v96
				r.study_type.toLowerCase().contains("reproduct") ||//in v96
				r.study_type.toLowerCase().contains("multigeneration") ||//in v96
				r.study_type.toLowerCase().contains("developmental") ||//in v96
				r.critical_effect.toLowerCase().contains("reproduct") ||//in v96
				r.critical_effect.toLowerCase().contains("multigeneration") ||//NOT in v96
				r.critical_effect.toLowerCase().contains("developmental")) { //in V96
			//System.out.println("here repro");
			// } else if (r.risk_assessment_class.contentEquals("developmental")
			//		|| r.risk_assessment_class.contentEquals("developmental neurotoxicity")
			//		|| r.risk_assessment_class.contentEquals("reproductive")){
			CreateReproductiveDevelopmentalToxicityRecords crd = new CreateReproductiveDevelopmentalToxicityRecords();
			crd.createReproductiveDevelopmentalRecords(chemical, r);
			//	} else if (r.risk_assessment_class.contentEquals("neurotoxicity")) {
			//		} else if(r.study_type.toLowerCase().contains("neuro") ||
			//				isNeuroCriticalEffect(r)) {
			//			CreateNeurotoxicityRecords2.createDurationRecord(chemical, r);
			//		I'm moving the creation of neurotoxicity records
			//			into the CreateOrganOrSystemicToxRecords class
			//			because they have the same scoring criteria.  -Leora
		} else {
			CreateOrganOrSystemicToxRecords.createDurationRecord(chemical, r);	
		}
	}
	/* Probably should rename as Acute or Chronic AquaticToxicity instead of Ecotoxicity.

		} else if (r.risk_assessment_class.contentEquals("ecotoxicity invertebrate")) {
			CreateEcotoxicityRecords.createEcotoxInvertebrateRecords(chemical, r);

		} else if (r.risk_assessment_class.contentEquals("subchronic")) {
			 createSubchronicRecords(chemical,r);

		} else if (r.risk_assessment_class.contentEquals("short-term")) {
			 createShorttermRecords(chemical,r);

		} else if (r.risk_assessment_class.contentEquals("subacute")) {
			 createSubacuteRecords(chemical,r);
	 */


	//		} else if (r.risk_assessment_class.contentEquals("acute")) {
	//			//TODO
	//
	//		} else if (r.risk_assessment_class.contentEquals("chronic")) {
	//			//TODO
	//			
	//		} else if (r.risk_assessment_class.contentEquals("mortality:acute")) {
	//			//TODO
	//			
	//		} else if (r.risk_assessment_class.contentEquals("mortality:chronic")) {
	//			//TODO




	//			System.out.println("unknown rac="+r.risk_assessment_class);







	/*
	 * } else if
	 * (r.risk_assessment_class.contentEquals("developmental neurotoxicity")) {
	 * createDevelopmentalNeurotoxicityRecords(chemical, r);
	 */

	// } else {
	// TODO add methods for other risk assessment classes
	/*
	 * System.out.println("unknown rac="+r.risk_assessment_class);
	 * 
	 * rac to add -Leora 4/23/20:
	 * 
	 * mortality:acute mortality:chronic
	 * 
	 * chronic (human health: species include human and rat; eco: different aquatic
	 * species) subchronic (human health: rat) short-term repeat dose subacute
	 * 
	 * growth:acute growth:chronic
	 *
	 * reproductive
	 * 
	 * neurotoxicity developmental neurotoxicity
	 * 
	 * ecotoxicity invertebrate
	 * 
	 * 
	 * 
	 * Excellent! That’s what you are supposed to get because code hasn’t been
	 * added to handle these risk assessment classes. If you look at the code for
	 * the GoThroughRecords method in the ParseToxVal class, you will see a block of
	 * code like this: if (r.risk_assessment_class.contentEquals("acute")) {........
	 * For right now you can comment out the line above that prints unknown rac so
	 * that it doesn’t clutter the output. So far I have only handled a few of the
	 * hazard categories (and may not be complete yet).
	 * 
	 * Look at the code for the first 3 I added and make sure I did it the same way
	 * as Richard. I may have restricted the records more than he did- for example
	 * for acute oral I only used “toxval_type”= “LD50” and the 4 species
	 * that we used earlier in the ParseChemIdplus class.
	 **** 
	 * [I think the point is to look at what Richard did and suggest changes if we
	 * don't agree. -Leora 4/23/20]****
	 * 
	 * If you didn’t remember, pressing F3 will jump to a different method when
	 * the cursor is on it. Alt + left arrow will go back to where you were. Todd
	 * 
	 * 
	 */


	/*
	 * There does not appear to be a rac for genetox. However, there is a separate
	 * file called toxval_genetox_summary_2020-01-16 that I downloaded from the ftp
	 * site I want to do something like: If the chemical is in the
	 * toxval_genetox_summary_2020-01-16 file then createGenetoxScore(chemical,r);
	 * 
	 * Need to import the toxval_genetox_summary_2020-01-16 Excel file. Then:
	 * private void createGenetoxRecords(Chemical chemical, RecordToxVal r) { if
	 * genetox_call = "clastogen" OR "gentox" OR "pred clastogen" OR "pred gentox"
	 * then score= VH [the vertical line key to indicate "OR" is not working on my
	 * keyboard] [there are no genetox_call data that would indicate H or M] if
	 * genetox_call = "non gentox" OR "pred non gentox" then score= L if
	 * genetox_call = "inconclusive" OR "not clastogen" then score= N/A -Leora
	 */

		// Organism Test Type Route Reported Dose (Normalized Dose) Effect Source
	static ScoreRecord saveToxValInfo(Score score,RecordToxVal tr,Chemical chemical) {

		ScoreRecord sr=new ScoreRecord(score.hazard_name,chemical.getCAS(),chemical.getName());
		
		sr.route=tr.exposure_route;
		
		sr.url=tr.url;		
		sr.longRef=tr.long_ref;

		sr.source = ScoreRecord.sourceToxVal;
		sr.sourceOriginal=tr.source;
		
		setAuthority(sr);
		
		sr.valueMassOperator=tr.toxval_numeric_qualifier;
		sr.valueMass = Double.parseDouble(tr.toxval_numeric);
		sr.valueMassUnits = tr.toxval_units;
		
		sr.toxvalID=tr.toxval_id;
		sr.testOrganism=tr.species_common;
		sr.testType=tr.toxval_type;
		
				
//		sr.reported_dose=tr.toxval_numeric_original+" "+tr.toxval_units_original;//now separate variable
//		sr.normalized_dose=tr.toxval_numeric +" "+tr.toxval_units;
//		String note = "Reported Dose: " + tr.toxval_numeric_original+" "+tr.toxval_units_original + "<br>\r\n";
//		note += "Normalized Dose: " + tr.toxval_numeric +" "+tr.toxval_units+"<br>\r\n";
//		// if (tr.Effect==null || tr.Effect.equals("")) {
//		// tr.Effect="N/A";
//		// }
//		
//		return note;
		return sr;
	}
	
	public static void setAuthority(ScoreRecord sr) {


		Vector<String>authoritativeSources=new Vector<>();
//		authSources.add("ECHA");
		authoritativeSources.add("ATSDR");
		authoritativeSources.add("ATSDR MRLs");//v96
				
		authoritativeSources.add("ATSDR PFAS");//toxval_v94
		authoritativeSources.add("ATSDR PFAS 2021");//toxval_v94, retired

		authoritativeSources.add("PPRTV (CPHEA)");//most up to date
		authoritativeSources.add("PPRTV (NCEA)");//has additional metadata
		authoritativeSources.add("PPRTV (ORNL)");//should be retired	
		
		
		authoritativeSources.add("EPA AEGL");
		authoritativeSources.add("HEAST");		
		authoritativeSources.add("EPA OPP");
		authoritativeSources.add("EPA OPPT");
		authoritativeSources.add("Cal OEHHA");
//		I deleted "Cal EPA" because it is listed below.
		authoritativeSources.add("RSL");
		authoritativeSources.add("IRIS");
		authoritativeSources.add("ECOTOX");//should be screening since BCFBAF is screening
		authoritativeSources.add("CalEPA");
		authoritativeSources.add("Cal EPA");
		authoritativeSources.add("IARC");

		authoritativeSources.add("NIOSH");//Leora check // Yes, NIOSH is authoritative.  -Leora
		authoritativeSources.add("NTP ROC");//Leora check // Yes, NTP ROC is authoritative.  -Leora
		authoritativeSources.add("NTP PFAS");
		authoritativeSources.add("PFAS 150 SEM v2");
		
		authoritativeSources.add("NTP RoC");//2023-11-15
		
				
		Vector<String>screeningSources=new Vector<>();
		
		screeningSources.add("EPA HHTV");//V96, dont know what this is
		screeningSources.add("ToxRefDB");
		screeningSources.add("ECHA");
		screeningSources.add("EFSA");
		screeningSources.add("EFSA2");
		screeningSources.add("ECHA IUCLID");
		screeningSources.add("Pennsylvania DEP ToxValues");
		screeningSources.add("Chiu");
		screeningSources.add("Wignall");
		screeningSources.add("Health Canada");
		screeningSources.add("HPVIS");
		screeningSources.add("EPA OW Drinking Water Standards");
		screeningSources.add("WHO IPCS");
		screeningSources.add("WHO JECFA Tox Studies");
		screeningSources.add("Alaska DEC");
		screeningSources.add("COSMOS");
		screeningSources.add("DOD");
		screeningSources.add("DOE Wildlife Benchmarks");
		
		screeningSources.add("HAWC");//toxval_v8
		screeningSources.add("HAWC PFAS 150");//toxval_v94
		screeningSources.add("HAWC PFAS 430");//toxval_v94
		screeningSources.add("HAWC Project");//toxval_v94
		
		
		screeningSources.add("HESS");
		screeningSources.add("TEST");
		screeningSources.add("ChemIDplus");
		screeningSources.add("Copper Manufacturers");
		screeningSources.add("Uterotrophic Hershberger DB");
		
		
		if (authoritativeSources.contains(sr.sourceOriginal)) {
			sr.listType=ScoreRecord.typeAuthoritative;
		} else if (screeningSources.contains(sr.sourceOriginal)) {
			sr.listType=ScoreRecord.typeScreening;				
		} else {
			System.out.println(sr.sourceOriginal+"\tunknown original source");
		}
		
	}

	public static String formatDose(double dose) {
		DecimalFormat df = new DecimalFormat("0.00");
		DecimalFormat df2 = new DecimalFormat("0");
		DecimalFormat dfSci = new DecimalFormat("0.00E00");

		double doseRoundDown = Math.floor(dose);

		double percentDifference = Math.abs(doseRoundDown - dose) / dose * 100.0;

		if (dose < 0.01) {
			return dfSci.format(dose);
		} else {
			if (percentDifference > 0.1) {
				return df.format(dose);
			} else {
				return df2.format(dose);
			}
		}

	}

	/*
	 * Combining all of these into OrganOrSystemicToxRecords:
	 * 
	 * private void createChronicRecords(Chemical chemical, RecordToxVal r) {
	 * }
	 * 
	 * private void createSubchronicRecords(Chemical chemical, RecordToxVal r) {
	 * 
	 * // study_duration_value and study_duration_units can be used to determine the
	 * actual duration for studies called subchronic, short term, or repeat dose.
	 * Then DfE criteria for repeated dose toxicity (28, 40-50, or 90 days) can be
	 * used.
	 * }
	 * 
	 * private void createShorttermRecords(Chemical chemical, RecordToxVal r) {
	 * 
	 * private void createSubacuteRecords(Chemical chemical, RecordToxVal r) {
	 * 
	 * }
	 */



	/*
	 * private void createGrowthAcuteRecords(Chemical chemical, RecordToxVal r) {
	 * 
	 * }
	 * 
	 * private void createGrowthChronicRecords(Chemical chemical, RecordToxVal r) {
	 * 
	 * }
	 */

//	private void createReproductiveRecords(Chemical chemical, RecordToxVal r) {

		/*
		 * Reproductive will have the same code as Developmental (same DfE criteria),
		 * which is detailed above.  So ReproductiveDevelopmental was combined into one class.
		 */

	

//	private static void createNeurotoxicityRecords2(Chemical chemical, RecordToxVal r) {

		// Neurotoxicity is now included in OrganOrSystemicToxRecords.


	// private void createDevelopmentalNeurotoxicityRecords(Chemical chemical, RecordToxVal r) {
	//  Including deveopmental neurotoxicity in neurotoxicity.
	/*
	 * DevelopmentalNeurotoxicity will have the same code as Developmental (same DfE
	 * criteria), which is detailed above. -Leora
	 */



	/*
	 * So this class is just for creating the individual scores. Do we also want to
	 * integrate into one score for each chemical? Or is it actually best to not
	 * even do that so that we are not assigning a "final" score? But integrating
	 * the scores is one of the things that I've been contemplating for the ToxVal
	 * data. As we discussed, it might make sense to use the priority_id field and
	 * take the minimum score from each of the seven priority_id categories and then
	 * priority_id 1>2>3>4>5>6>7 in the trumping method.
	 * 
	 * Also, instead of, or in combination with, the trumping scheme, we could
	 * remove extreme outliers and then take the minimum of the remaining scores.
	 * Since the values are continuous instead of ordinal, removing outliers makes
	 * sense.
	 * 
	 * In Grace's Science Webinar presentation on 4/22/20, she talked about using
	 * ToxVal data to develop TTC. She filtered from ToxVal: toxval type: NO(A)EL or
	 * NO(A)EC species: rats, mice, rabbits To derive representative values, she
	 * removed outliers that exceeded the IQR. Maybe we should remove outliers
	 * similar to what Grace did.
	 *
	 * -Leora 4/23/20
	 */

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ParseToxVal p = new ParseToxVal();
		//		p.createFiles();

		String folder = "C:\\Users\\Leora\\Desktop\\Tele\\ToxVal";
		// String folder="E:\\Documents\\0000 epa\\0 telework\\AA dashboard";

		// String CAS = "79-06-1"; // acrylamide
		String CAS = "123-91-1"; // 1,4-Dioxane

		String filePathDatabaseAsText = folder + File.separator + "toxval_pod_summary_with_references_2020-01-16.txt";

		String filePathRecordsForCAS = folder + File.separator + "toxval_pod_summary_" + CAS + ".txt";

		String filePathRecordsForCAS_json = folder + File.separator + "records_" + CAS + ".json";
		String filePathRecordsForCAS_txt = folder + File.separator + "records_" + CAS + ".txt";

		//		p.getRecordsForCAS(CAS,filePathDatabaseAsText, filePathRecordsForCAS);		

		p.goThroughRecords(filePathRecordsForCAS, filePathRecordsForCAS_json, filePathRecordsForCAS_txt,ParseToxValDB.v8);

		//		Vector<String>vecCAS=new Vector<>();
		//		vecCAS.add("79-06-1");
		//		vecCAS.add("79-01-6"); 
		//		vecCAS.add("108-95-2"); 
		//		vecCAS.add("50-00-0"); 
		//		vecCAS.add("111-30-8");
		//		vecCAS.add("302-01-2"); 
		//		vecCAS.add("75-21-8"); 
		//		vecCAS.add("7803-57-8"); 
		//		vecCAS.add("101-77-9"); 
		//		vecCAS.add("10588-01-9"); 

		//		vecCAS.add("107-13-1"); 
		//		vecCAS.add("110-91-8"); 
		//		vecCAS.add("106-93-4"); 
		//		vecCAS.add("67-56-1"); 
		//		vecCAS.add("7664-39-3"); 
		//		vecCAS.add("556-52-5"); 
		//		vecCAS.add("87-86-5"); 
		//		vecCAS.add("62-53-3"); 
		//		vecCAS.add("106-89-8"); 
		//		vecCAS.add("7778-50-9");
		//				
		//		String filePathRecordsForCASList_json=folder+File.separator+"toxval_pod_summary_top 20.json";		
		//		String filePathRecordsForCASList_txt=folder+File.separator+"toxval_pod_summary_Top20.txt";
		//
		//		p.goThroughRecordsMultipleChemicals(filePathDatabaseAsText,filePathRecordsForCASList_json,filePathRecordsForCASList_txt,vecCAS);

	}

}
