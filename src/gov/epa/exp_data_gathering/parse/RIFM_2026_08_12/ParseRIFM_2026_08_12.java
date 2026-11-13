package gov.epa.exp_data_gathering.parse.RIFM_2026_08_12;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.google.gson.JsonObject;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.DsstoxMapperFromChemRegExcelExport;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.RIFM_2026_01.RecordRIFM_2026_01;

public class ParseRIFM_2026_08_12 extends Parse {

	private String baseFolderPath = "data" + java.io.File.separator + "experimental" + java.io.File.separator + "RIFM_2026_08_12";

	
	static String property; 
	
	public ParseRIFM_2026_08_12() {
		sourceName = "RIFM_2026_08_12"; // TODO Consider creating ExperimentalConstants.strSourceRIFM_2026_08_12 instead.
		this.init();

		// TODO Is this a toxicity source? If so, rename original and experimental records files here.
	}

	@Override
	protected void createOriginalRecords() {
		Vector<JsonObject> records = RecordRIFM_2026_08_12.parseRIFM_2026_08_12RecordsFromExcel();
		writeOriginalRecordsToFile(records);
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental = new ExperimentalRecords();
		//		Hashtable<String, ExperimentalRecords> htRecsAll = new Hashtable<>();

		try {

			String strFolder = mainFolder + java.io.File.separator;
			// Now using convenience method that works for any Record class:
			List<RecordRIFM_2026_08_12> recordsDB = getOriginalRecordsFromJsonFiles(strFolder, RecordRIFM_2026_08_12[].class,
					"UTF-8");//			System.out.println(recordsDB.size());
//			System.out.println(gson.toJson(recordsDB));
			
			boolean useExcelMapping = true;
			
			DsstoxMapperFromChemRegExcelExport dm=null;
			
			if (useExcelMapping) {
				String filepathChemreg = "data"+File.separator+"experimental"+File.separator+sourceName+File.separator + "excel files" + java.io.File.separator + "RIFM ChemReg curation.xlsx";
				dm = new DsstoxMapperFromChemRegExcelExport(filepathChemreg);
			}
			

			Iterator<RecordRIFM_2026_08_12> it = recordsDB.iterator();
			while (it.hasNext()) {
				RecordRIFM_2026_08_12 r = it.next();

				//				System.out.println(r.CAS);

				if (r.Test_substance_CASRN == null)
					continue;

				ExperimentalRecord er = r.toExperimentalRecord();

				if (useExcelMapping) {

//					if (er.keep) {
						dm.getCuratedIdentifiers(er);
						dm.getDtxsid(er);
//					}

					if (er.dsstox_substance_id == null) {
						er.keep = false;
						er.updateReason("Could not map identifiers to DSSTox record");
					}
				}

				recordsExperimental.add(er);
			}

			if (useExcelMapping) {
				dm.saveMissingChemregToTextFiles(RecordRIFM_2026_01.sourceName);
				dm.printMissingChemreg();
			}

			return recordsExperimental;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private void addExperimentalRecord(RecordRIFM_2026_08_12 r,
			ExperimentalRecords recordsExperimental) {
		ExperimentalRecord er=r.toExperimentalRecord();
		recordsExperimental.add(er);
	}
	
	public void setProperty(String property) {
		
		
		// Update mainFolder and jsonFolder based on mode
		String subfolder;
		
		ParseRIFM_2026_08_12.property=property;
		
		if (property.equals(ExperimentalConstants.strRBIODEG)) {
			subfolder = "RBiodeg 301F RIFM";
		} else if (property.equals(ExperimentalConstants.strRBIODEG_10_day)) {
			subfolder = "RBiodeg 301F RIFM (10 day window)";
		} else if (property.equals(ExperimentalConstants.strRBIODEG_10_day2)) {
			subfolder = "RBiodeg 301F RIFM (10 day window) 2";
		} else if (property.equals(ExperimentalConstants.strPercentageBiodegradation)) {
			subfolder = "Percent Biodegradation 301F RIFM";
		} else if (property.equals(ExperimentalConstants.strRBIODEG_multiclass)) {
			subfolder = "RBiodeg 301F RIFM multiclass";
		} else {
			throw new IllegalArgumentException("Invalid output mode. Use binary or continuous constants");
		}
		
		this.mainFolder = baseFolderPath + java.io.File.separator + subfolder;
		this.jsonFolder = this.mainFolder;
		
		// Ensure the folder exists
		new java.io.File(this.mainFolder).mkdirs();
	}


	public static void main(String[] args) {
		ParseRIFM_2026_08_12 p = new ParseRIFM_2026_08_12();

		p.generateOriginalJSONRecords=true;
		//p.howManyOriginalRecordsFiles=2;

		p.removeDuplicates=false;
		
//		List<String>properties=Arrays.asList(ExperimentalConstants.strRBIODEG,ExperimentalConstants.strRBIODEG_10_day,ExperimentalConstants.strPercentageBiodegradation);
//		List<String>properties=Arrays.asList(ExperimentalConstants.strRBIODEG_multiclass);
		List<String>properties=Arrays.asList(ExperimentalConstants.strRBIODEG_10_day2);

		for (String property:properties) {
			p.setProperty(property);
			p.writeJsonExperimentalRecordsFile=true;
			p.writeExcelExperimentalRecordsFile=true;
			p.writeExcelFileByProperty=false;		
			p.writeCheckingExcelFile=false;//creates random sample spreadsheet
			p.createFiles();
		}
		
//		p.setProperty(ExperimentalConstants.strRBIODEG);

	}
}