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

	/**
	 * Output mode for the parser: "BINARY" or "CONTINUOUS"
	 * BINARY: Converts oxygen consumption % to 0.0 (not biodegradable) / 1.0 (biodegradable) if >60%
	 * CONTINUOUS: Preserves actual oxygen consumption percentage values
	 */
	private String outputMode = "BINARY";
	
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
	protected void createRecords() {
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
		this.outputMode = mode;
		RecordRIFM_2026_01.setMode(mode);
		
		// Update mainFolder and jsonFolder based on mode
		String subfolder;
		if ("CONTINUOUS".equalsIgnoreCase(mode)) {
			subfolder = "Percent Biodegradation 301F RIFM";
		} else if ("BINARY".equalsIgnoreCase(mode)) {
			subfolder = "RBiodeg 301F RIFM";
		} else {
			throw new IllegalArgumentException("Invalid output mode. Use 'BINARY' or 'CONTINUOUS'.");
		}
		
		this.mainFolder = baseFolderPath + java.io.File.separator + subfolder;
		this.jsonFolder = this.mainFolder;
		
		// Ensure the folder exists
		new java.io.File(this.mainFolder).mkdirs();
	}

	/**
	 * Gets the current output mode.
	 * 
	 * @return the current output mode ("BINARY" or "CONTINUOUS")
	 */
	public String getOutputMode() {
		return this.outputMode;
	}

	public static void main(String[] args) {
		ParseRIFM_2026_01 p = new ParseRIFM_2026_01();

		p.generateOriginalJSONRecords = true;
		p.removeDuplicates = false;

		p.writeJsonExperimentalRecordsFile = true;
		p.writeExcelExperimentalRecordsFile = true;
		p.writeExcelFileByProperty = true;
		p.writeCheckingExcelFile = false;// creates random sample spreadsheet

		// Set output mode - options are "BINARY" or "CONTINUOUS"
		// BINARY: classifies as biodegradable (1.0) if oxygen consumption > 60%, else not biodegradable (0.0)
		// CONTINUOUS: preserves actual oxygen consumption percentages from the source data
		p.setOutputMode("CONTINUOUS");

		p.createFiles();

	}
}