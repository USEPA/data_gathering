package gov.epa.exp_data_gathering.parse.RIFM_2026_01;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.google.gson.JsonObject;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.DsstoxMapperFromChemRegExcelExport;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;

public class ParseRIFM_2026_01 extends Parse {

		
	/**
	 * Base folder for output (before mode-specific subfolder is appended)
	 * Used to switch between mode-specific output folders
	 */
	private String baseFolderPath = "data" + java.io.File.separator + "experimental" + java.io.File.separator + "RIFM_2026_01";

	public ParseRIFM_2026_01() {
		sourceName = "RIFM_2026_01"; // TODO Consider creating ExperimentalConstants.strSourceRIFM_2026_01 instead.
		this.init("RBiodeg 301F RIFM");

		// TODO Is this a toxicity source? If so, rename original and experimental
		// records files here.
	}

	@Override
	protected void createOriginalRecords() {
		Vector<JsonObject> records = RecordRIFM_2026_01.parseRIFM_2026_01RecordsFromExcel();
		writeOriginalRecordsToFile(records);
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental = new ExperimentalRecords();
//		Hashtable<String, ExperimentalRecords> htRecsAll = new Hashtable<>();

		try {

			// Read from the current mainFolder (which contains the Original Records for this mode-specific output)
			String strFolder = mainFolder + java.io.File.separator;
			// Now using convenience method that works for any Record class:
			List<RecordRIFM_2026_01> recordsDB = getOriginalRecordsFromJsonFiles(strFolder, RecordRIFM_2026_01[].class,
					"UTF-8");
//			System.out.println(recordsDB.size());
//			System.out.println(gson.toJson(recordsDB));
			
			boolean useExcelMapping = false;
			
			DsstoxMapperFromChemRegExcelExport dm=null;
			
			if (useExcelMapping) {
				String filepathChemreg = strFolder + "excel files" + java.io.File.separator + "RIFM ChemReg curation.xlsx";
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

	/**
	 * Sets the output mode for property value transformation and updates output folder accordingly.
	 * BINARY mode outputs to: data/experimental/RIFM_2026_01/RBiodeg 301F RIFM
	 * CONTINUOUS mode outputs to: data/experimental/RIFM_2026_01/Percent Biodegradation 301F RIFM
	 * 
	 * @param mode "BINARY" for binary biodegradable classification, "CONTINUOUS" for percentage values
	 */
	public void setOutputMode(String mode) {
		RecordRIFM_2026_01.outputMode = mode;
		
		// Update mainFolder and jsonFolder based on mode
		String subfolder;
		if (mode.equals(ExperimentalConstants.str_continuous)) {
			subfolder = "Percent Biodegradation 301F RIFM";
		} else if (mode.equals(ExperimentalConstants.str_binary)) {
			subfolder = "RBiodeg 301F RIFM";
		} else {
			throw new IllegalArgumentException("Invalid output mode. Use binary or continuous constants");
		}
		
		this.mainFolder = baseFolderPath + java.io.File.separator + subfolder;
		this.jsonFolder = this.mainFolder;
		
		// Ensure the folder exists
		new java.io.File(this.mainFolder).mkdirs();
	}

	public static void main(String[] args) {
		ParseRIFM_2026_01 p = new ParseRIFM_2026_01();

		p.generateOriginalJSONRecords = true;
		p.removeDuplicates = false;

		p.writeJsonExperimentalRecordsFile = true;
		p.writeExcelExperimentalRecordsFile = true;
		p.writeExcelFileByProperty = true;
		p.writeCheckingExcelFile = false;// creates random sample spreadsheet

		// Set output mode:
		// BINARY: classifies as biodegradable (1.0) if oxygen consumption > 60%, else not biodegradable (0.0)
		// CONTINUOUS: preserves actual oxygen consumption percentages from the source data

		p.setOutputMode(ExperimentalConstants.str_continuous);
		p.createFiles();

		p.setOutputMode(ExperimentalConstants.str_binary);
		p.createFiles();


	}
}