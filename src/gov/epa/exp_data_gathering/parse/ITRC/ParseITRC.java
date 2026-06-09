package gov.epa.exp_data_gathering.parse.ITRC;


import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.google.gson.reflect.TypeToken;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.ITRC.RecordITRC.RecordBio.Species;

/**
* @author TMARTI02
*/
public class ParseITRC extends Parse {
	
	/**
	 * Base folder for output (before mode-specific subfolder is appended)
	 * Used to switch between mode-specific output folders
	 */
	private String baseFolderPath = "data" + java.io.File.separator + "experimental" + java.io.File.separator + "ITRC";
	
	String fileName="todo";
	static final String filenameKoc = "PhysChemProp_Table_July2023-FINAL.xlsx";
	static final String filenameBio = "ITRC_PFAS_-BCF-BAF_compilation_Table5-1_Oct2021.xlsx";
	String valueType="todo";

	public ParseITRC(String valueType) {
		sourceName = RecordITRC.sourceName;
		this.valueType = valueType;
		if (valueType.equals("KOC")) {
			this.fileName = filenameKoc;
			this.init("KOC ITRC");
		} else if (valueType.equals("BAF")) {
			this.fileName = filenameBio;
			this.init("BAF ITRC");
		} else if (valueType.equals("BCF")) {
			this.fileName = filenameBio;
			this.init("BCF ITRC");
		}
	}

	@Override
	protected void createOriginalRecords() {
		if(generateOriginalJSONRecords) {
			RecordITRC ri=new RecordITRC();
			List<RecordITRC> records = null;
			
			if(fileName.equals(filenameKoc)) {
				records = ri.parseExcelFile(filenameKoc);
			} else if(fileName.equals(filenameBio)) {
				records = ri.parseExcelFileBio(fileName);
			}
			
			if(records != null) {
				writeOriginalRecordsToFile(records);
			}
		}
	}
	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		
		try {
			
			// Read Original Records from the same mainFolder where they were written by createRecords()
			// This ensures we read from the correct subfolder (BAF ITRC, BCF ITRC, or KOC ITRC)
			List<RecordITRC> recordsDB = getOriginalRecordsFromJsonFiles(mainFolder, RecordITRC[].class,"UTF-8");
//			System.out.println(recordsDB.size());
			
			Iterator<RecordITRC> it = recordsDB.iterator();

			Type type = new TypeToken<Hashtable<String, List<RecordITRC.RecordBio.Species>>>(){}.getType();
			Hashtable<String, List<Species>>htSpecies=JsonUtilities.gsonPretty.fromJson(new FileReader("data\\experimental\\Arnot 2006\\htSuperCategory.json"), type);
			while (it.hasNext()) {
				RecordITRC r = it.next();
				
				// Handle KOC records
				if(r.RecordsKOC != null && r.RecordsKOC.size() > 0 && r.RecordsKOC.get(0) != null) {
					List<ExperimentalRecord> kocRecords = r.toExperimentalRecordsKoc();
					recordsExperimental.addAll(kocRecords);
				}
				
				// Handle BAF/BCF records
				if(r.RecordsBio != null && r.RecordsBio.size() > 0) {
					List<ExperimentalRecord> bafRecords = r.toExperimentalRecordsBio(valueType, htSpecies);
					recordsExperimental.addAll(bafRecords);
				}
			}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return recordsExperimental;
	}

	static void runKoc() {
		ParseITRC p = new ParseITRC("KOC");
		
		String specialCharString = "Character with code 177: \u00B1";
		p.generateOriginalJSONRecords=true;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();
	}

	static void runBAF() {
		ParseITRC p = new ParseITRC("BAF");
		
		String specialCharString = "Character with code 177: \u00B1";
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();
	}

	static void runBCF() {
		ParseITRC p = new ParseITRC("BCF");
		
		String specialCharString = "Character with code 177: \u00B1";
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();
	}
	
	
	public static void main(String[] args) {
		// runKoc();
		runBAF();
		runBCF();
	}
}