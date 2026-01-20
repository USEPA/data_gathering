package gov.epa.exp_data_gathering.parse.Koc;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.CompareExperimentalRecords;
import gov.epa.exp_data_gathering.parse.CompareExperimentalRecords.Source;
import gov.epa.exp_data_gathering.parse.DsstoxMapperFromChemRegExcelExport;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.ParameterValue;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.ParseUtilities;
import gov.epa.exp_data_gathering.parse.TextUtilities;
import gov.epa.exp_data_gathering.parse.ITRC.RecordITRC;
import gov.epa.exp_data_gathering.parse.Mackay_2006.RecordMackay_2006;
import gov.epa.exp_data_gathering.parse.Montgomery.RecordMontgomery;
import gov.epa.exp_data_gathering.parse.OPERA.RecordOPERA;
import gov.epa.exp_data_gathering.parse.QSAR_ToolBox.RecordQSAR_ToolBox;
import gov.epa.exp_data_gathering.parse.USDA_Pesticide_Property_DB.RecordPesticidePropertyDB;
import gov.epa.ghs_data_gathering.GetData.ECHA_IUCLID.ParseREACH_JSON_Files.RecordSorter;

/**
 * 
 * 
 * @author TMARTI02
 */
public class ParseKoc extends Parse {

	RecordKoc recKoc = new RecordKoc();

	public ParseKoc() {
		sourceName = RecordKoc.sourceName;
		this.init();
	}

	@Override
	protected void createRecords() {

		if (generateOriginalJSONRecords) {
			System.out.println("Getting original records from excel spreadsheet of multiple literature sources");
			List<RecordKoc> records = recKoc.parseExcelFile();
			writeOriginalRecordsToFile(records);
			System.out.println("done");
		}
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental = new ExperimentalRecords();
		Hashtable<String, ExperimentalRecords> htRecsAll = new Hashtable<>();

		try {

			String mainFolder = "data\\experimental\\";
			String strFolder = mainFolder + RecordKoc.sourceName + "\\";
			// Now using convenience method that works for any Record class:
			List<RecordKoc> recordsDB = getOriginalRecordsFromJsonFiles(strFolder, RecordKoc[].class, "UTF-8");
//			System.out.println(recordsDB.size());
//			System.out.println(gson.toJson(recordsDB));

			Iterator<RecordKoc> it = recordsDB.iterator();
			while (it.hasNext()) {
				RecordKoc r = it.next();
				ExperimentalRecord er = r.toExperimentalRecord();
				recordsExperimental.add(er);
			}

			htRecsAll.put(RecordKoc.sourceName, recordsExperimental);

			getExperimentalRecordsOtherSources(htRecsAll);
//			runComparison(htRecsAll);

			ExperimentalRecords allRecords = new ExperimentalRecords();// from all sources not just the one spreadsheet
			String filepathChemreg = strFolder + "/excel files/Koc ChemReg curation.xlsx";
			DsstoxMapperFromChemRegExcelExport dm = new DsstoxMapperFromChemRegExcelExport(filepathChemreg);

			System.out.println("\nUpdating experimental records");
			
			for (String sourceName : htRecsAll.keySet()) {
				for (ExperimentalRecord er : htRecsAll.get(sourceName)) {

					//Since no literature info and only lose 6 records, remove Yaws records:
					if (er.publicSource != null && er.publicSource.name.equals("Yaws, 1999"))
						continue;

					er.chemical_name = TextUtilities.fixName(er.chemical_name);
					fixPermethrins(er);
					dm.getCuratedIdentifiers(er);
					dm.getDtxsid(er);
					if (er.dsstox_substance_id == null) {
						er.keep = false;
						er.updateReason("Could not map identifiers to DSSTox record");
					}
					
					allRecords.add(er);
				}
			}

			dm.saveMissingChemregToTextFiles(RecordKoc.sourceName);

			Hashtable<String, ExperimentalRecords> htER = allRecords
					.createExpRecordHashtableBySID(ExperimentalConstants.str_L_KG);
			System.out.println("\nNumber of sids=" + htER.size());

//			double median=ExperimentalRecords.calculateMedian(recs, true);

//			Hashtable<String,Double>htMedian=ExperimentalRecords.calculateMedian(htER, true);

//			System.out.println(htMedian.size()+"\n");
//			for(String key:htMedian.keySet()) {
//				System.out.println(key+"|"+htMedian.get(key));
//			}

			return allRecords;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void fixPermethrins(ExperimentalRecord er) {
		Object chemicalForm=er.getExperimentalParameter("chemical_form");
		if(chemicalForm!=null && er.chemical_name!=null) {
			er.chemical_name=chemicalForm+"-"+er.chemical_name;
			er.experimental_parameters.remove("chemical_form");
			
			if(er.chemical_name.equals("cis-Cypermethrin")) {
				er.casrn="211504-93-7";
			} else if(er.chemical_name.equals("trans-Cypermethrin")) {
				er.casrn="211504-94-8";
			} else if(er.chemical_name.equals("cis-Permethrin")) {
				er.casrn="61949-76-6";
			} else if(er.chemical_name.equals("trans-Permethrin")) {
				er.casrn="61949-77-7";
			}
			
			System.out.println("Updated name from chemical form:"+er.chemical_name);
		}
	}

	private void runComparison(Hashtable<String, ExperimentalRecords> htRecsAll) {
		CompareExperimentalRecords cer = new CompareExperimentalRecords();
		CompareExperimentalRecords.printChemicalsInCommon = false;
		CompareExperimentalRecords.printSourceChemical = true;

//				cer.cm.compare(htRecsAll, RecordKoc.sourceName, RecordPesticidePropertyDB.sourceName, ExperimentalConstants.strKOC,ExperimentalConstants.str_L_KG,"sid");
//				cer.cm.compare(htRecsAll, RecordKoc.sourceName, RecordMontgomery.sourceName, ExperimentalConstants.strKOC,ExperimentalConstants.str_L_KG,"sid");
//				cer.cm.compare(htRecsAll, RecordKoc.sourceName, RecordMackay_2006.sourceName, ExperimentalConstants.strKOC,ExperimentalConstants.str_L_KG,"sid");
//				cer.cm.compare(htRecsAll, RecordKoc.sourceName, RecordITRC.sourceName, ExperimentalConstants.strKOC,ExperimentalConstants.str_L_KG,"sid");

		cer.cm.compareToOtherSources(htRecsAll, RecordPesticidePropertyDB.sourceName, ExperimentalConstants.strKOC,
				ExperimentalConstants.str_L_KG, "sid");
		cer.cm.compareToOtherSources(htRecsAll, RecordMontgomery.sourceName, ExperimentalConstants.strKOC,
				ExperimentalConstants.str_L_KG, "sid");
		cer.cm.compareToOtherSources(htRecsAll, RecordMackay_2006.sourceName, ExperimentalConstants.strKOC,
				ExperimentalConstants.str_L_KG, "sid");
		cer.cm.compareToOtherSources(htRecsAll, RecordITRC.sourceName, ExperimentalConstants.strKOC,
				ExperimentalConstants.str_L_KG, "sid");
	}

	private void getExperimentalRecordsOtherSources(Hashtable<String, ExperimentalRecords> htRecsAll) {

//		Source				Params	
//		Mackay 2006			Soil_Type, Media, measurement_method TODO
//		USDA Pesticide DB	Soil_Type., tempC, Percentage_Organic_Matter,  pH
//		Montgomery 1993		Soil_Type, Percentage_Organic_Carbon
//		ITRC				Testing_Conditions, Media

		// Bring in the other sources from the experimental records jsons and add the
		// dtxsids:
		List<Source> sources = new ArrayList<>();
		sources.add(new Source(RecordPesticidePropertyDB.sourceName, null));
		sources.add(new Source(RecordMontgomery.sourceName, null));

//			sources.add(new Source(RecordMackay_2006.sourceName,null));
		Source sourceMackay = new Source(RecordMackay_2006.sourceName, null);
		String erPathMackay = "C:\\Users\\TMARTI02\\OneDrive - Environmental Protection Agency (EPA)\\0 python\\model_management\\pf_data_gathering\\data\\experimental\\Mackay_2006\\Mackay_2006 Experimental Records.json";
		sourceMackay.experimentalRecordsPath = erPathMackay;
		sources.add(sourceMackay);

		sources.add(new Source(RecordITRC.sourceName, null));
//			sources.add(new Source(RecordQSAR_ToolBox.sourceName,"Koc ECHA Reach"));
		addOtherSourcesFromExperimentalRecords(htRecsAll, sources);
	}

	/**
	 * Brings in records from other sources from the ExperimentalRecords jsons and
	 * adds the dtxsids
	 * 
	 * @param htRecsAll
	 */
	private void addOtherSourcesFromExperimentalRecords(Hashtable<String, ExperimentalRecords> htRecsAll,
			List<Source> sources) {
//		sources.add(new Source(ExperimentalConstants.strSourceOPERA28,null));

		for (Source source : sources) {

			ExperimentalRecords recsOther = null;

			if (source.experimentalRecordsPath != null) {
				recsOther = ExperimentalRecords.loadFromJSON(source.experimentalRecordsPath);
			} else {
				recsOther = ExperimentalRecords.getExperimentalRecords(source.sourceName, source.subfolder, "UTF-8");
			}

			htRecsAll.put(source.sourceName, recsOther);

//			System.out.println("\n"+source.sourceName+"\t"+recsOther.size());
		}
	}

	public static void main(String[] args) {
		ParseKoc p = new ParseKoc();

//		RecordKoc.printMissing=false;

		p.generateOriginalJSONRecords = true;
		p.removeDuplicates = false;// dont know which one is right
		p.writeCheckingExcelFile = false;
		p.createFiles();

	}

}
