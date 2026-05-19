package gov.epa.exp_data_gathering.parse.NITE;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;

import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.DsstoxMapperFromChemRegExcelExport;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.JSONUtilities;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.RIFM_2026_01.RecordRIFM_2026_01;



/**
 * 
 * Gets records from OPPT's version of NITE data
 * 
* @author TMARTI02
*/
public class ParseNITE extends Parse {

	Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public ParseNITE() {
		sourceName = RecordNITE.sourceName; // TODO Consider creating ExperimentalConstants.strSourceRIFM instead.
		this.init();
	}

	@Override
	protected void createRecords() {
		List<RecordNITE>recordsOriginal=RecordNITE.parseRecordsFromExcel();
		writeOriginalRecordsToFile(recordsOriginal);
	}

	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {

		boolean useSpreadsheetBasedMapping=false;
		
		String source=RecordNITE.sourceName;
		String propertyName=ExperimentalConstants.strRBIODEG;

		String mainFolder = "data\\experimental\\";
		String strFolder = mainFolder + source + "\\";

		String jsonPath = strFolder+source+" "+propertyName+" original records.json";

		DsstoxMapperFromChemRegExcelExport dm=null;
		
		if (useSpreadsheetBasedMapping) {
			String filepathChemreg = strFolder + "/excel files/ChemReg curation.xlsx";
			dm = new DsstoxMapperFromChemRegExcelExport(filepathChemreg);
		}
		

		
		List<RecordNITE> recordsOriginal = getOriginalRecordsFromJsonFiles(strFolder, RecordNITE[].class,
				"UTF-8");
				
		ExperimentalRecords experimentalRecords=new ExperimentalRecords();

		System.out.println("\nType\tName\tCAS\tsidName\tsidCAS\tinchiKeyname\tinchiKeyCAS\tSmiles");
		
		for (RecordNITE r:recordsOriginal) {
			ExperimentalRecord er=r.toExperimentalRecord();
			
			if (useSpreadsheetBasedMapping) {
				if (er.keep) {
					dm.getCuratedIdentifiers(er);
					dm.getDtxsid(er);
				}
				if (er.dsstox_substance_id == null) {
					er.keep = false;
					er.updateReason("Could not map identifiers to DSSTox record");
				}
			}
			
			experimentalRecords.add(er);
		}

		if (useSpreadsheetBasedMapping) {
			dm.saveMissingChemregToTextFiles(sourceName);
			dm.printMissingChemreg();
		}
		
		return experimentalRecords;
		
	}
	
	public static void main(String[] args) {
		ParseNITE p=new ParseNITE();
		p.generateOriginalJSONRecords = true;
		p.removeDuplicates = false;

		p.writeJsonExperimentalRecordsFile = true;
		p.writeExcelExperimentalRecordsFile = true;
		p.writeExcelFileByProperty = true;
		p.writeCheckingExcelFile = false;// creates random sample spreadsheet
		p.createFiles();
	}
}
