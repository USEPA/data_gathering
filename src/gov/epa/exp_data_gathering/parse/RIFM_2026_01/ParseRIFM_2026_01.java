package gov.epa.exp_data_gathering.parse.RIFM_2026_01;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.google.gson.JsonObject;

import gov.epa.exp_data_gathering.parse.DsstoxMapperFromChemRegExcelExport;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;

public class ParseRIFM_2026_01 extends Parse {

	public ParseRIFM_2026_01() {
		sourceName = "RIFM_2026_01"; // TODO Consider creating ExperimentalConstants.strSourceRIFM_2026_01 instead.
		this.init();

		// TODO Is this a toxicity source? If so, rename original and experimental
		// records files here.
	}

	@Override
	protected void createRecords() {
		Vector<JsonObject> records = RecordRIFM_2026_01.parseRIFM_2026_01RecordsFromExcel();
		writeOriginalRecordsToFile(records);
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental = new ExperimentalRecords();
//		Hashtable<String, ExperimentalRecords> htRecsAll = new Hashtable<>();

		try {

			String mainFolder = "data\\experimental\\";
			String strFolder = mainFolder + RecordRIFM_2026_01.sourceName + "\\";
			// Now using convenience method that works for any Record class:
			List<RecordRIFM_2026_01> recordsDB = getOriginalRecordsFromJsonFiles(strFolder, RecordRIFM_2026_01[].class,
					"UTF-8");
//			System.out.println(recordsDB.size());
//			System.out.println(gson.toJson(recordsDB));
			
			boolean useExcelMapping = false;
			
			DsstoxMapperFromChemRegExcelExport dm=null;
			
			if (useExcelMapping) {
				String filepathChemreg = strFolder + "/excel files/RIFM ChemReg curation.xlsx";
				dm = new DsstoxMapperFromChemRegExcelExport(filepathChemreg);
			}
			
			Iterator<RecordRIFM_2026_01> it = recordsDB.iterator();
			while (it.hasNext()) {
				RecordRIFM_2026_01 r = it.next();

//				System.out.println(r.CAS);

				if (r.CAS == null)
					continue;

				ExperimentalRecord er = r.toExperimentalRecord();

				if (useExcelMapping) {
					if (er.keep) {
						dm.getCuratedIdentifiers(er);
						dm.getDtxsid(er);
					}

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

	public static void main(String[] args) {
		ParseRIFM_2026_01 p = new ParseRIFM_2026_01();

		p.generateOriginalJSONRecords = false;
		p.removeDuplicates = false;

		p.writeJsonExperimentalRecordsFile = true;
		p.writeExcelExperimentalRecordsFile = true;
		p.writeExcelFileByProperty = true;
		p.writeCheckingExcelFile = false;// creates random sample spreadsheet
		p.createFiles();

	}
}