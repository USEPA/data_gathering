package gov.epa.exp_data_gathering.parse.ITRC;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.USDA_Pesticide_Property_DB.ParsePesticidePropertyDB;
import gov.epa.exp_data_gathering.parse.USDA_Pesticide_Property_DB.RecordPesticidePropertyDB;

/**
* @author TMARTI02
*/
public class ParseITRC extends Parse {
	
	public ParseITRC() {
		sourceName = RecordITRC.sourceName;
		this.init();
	}
	@Override
	protected void createRecords() {

		if(generateOriginalJSONRecords) {
			RecordITRC ri=new RecordITRC();
			List<RecordITRC> records = ri.parseExcelFile();
			writeOriginalRecordsToFile(records);
		}
	}
	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		
		try {
			
			String mainFolder="data\\experimental\\";
			String strFolder=mainFolder+RecordITRC.sourceName+"\\";
			List<RecordITRC> recordsDB = getOriginalRecordsFromJsonFiles(strFolder, RecordITRC[].class,"UTF-8");
//			System.out.println(recordsDB.size());
			
			Iterator<RecordITRC> it = recordsDB.iterator();
			while (it.hasNext()) {
				RecordITRC r = it.next();
				ExperimentalRecord er = r.toExperimentalRecord();
				recordsExperimental.add(er);
			}
			
			Hashtable<String,ExperimentalRecords> htER = recordsExperimental.createExpRecordHashtableByName(ExperimentalConstants.str_L_KG,true);
//			double median=ExperimentalRecords.calculateMedian(recs, true);
			
//			System.out.println("29091-21-2\t"+median);
			
			
			Hashtable<String,Double>htMedian=ExperimentalRecords.calculateMedian(htER, true);
			
//			System.out.println(htMedian.size()+"\n");
//			for(String key:htMedian.keySet()) {
//				System.out.println(key+"|"+htMedian.get(key));
//			}
			
			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return recordsExperimental;
	}
	
	
	public static void main(String[] args) {
		ParseITRC p = new ParseITRC();
		
		String specialCharString = "Character with code 177: \u00B1";
		p.generateOriginalJSONRecords=true;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();

		
	}
}