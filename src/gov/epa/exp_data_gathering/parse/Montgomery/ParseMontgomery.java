package gov.epa.exp_data_gathering.parse.Montgomery;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.USDA_Pesticide_Property_DB.ParsePesticidePropertyDB;
import gov.epa.exp_data_gathering.parse.USDA_Pesticide_Property_DB.RecordPesticidePropertyDB;

/**
* @author TMARTI02
*/
public class ParseMontgomery extends Parse {
	
	public ParseMontgomery() {
		sourceName = RecordMontgomery.sourceName;
		this.init();
	}
	@Override
	protected void createRecords() {

		if(generateOriginalJSONRecords) {
			
			RecordMontgomery rm=new RecordMontgomery();
			
			List<RecordMontgomery> records = rm.parseExcelFile();
			writeOriginalRecordsToFile(records);
		}
	}
	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		
		try {
			String jsonFileName = jsonFolder + File.separator + fileNameJSON_Records;
			File jsonFile = new File(jsonFileName);
			
			List<RecordMontgomery> recordsDB = new ArrayList<>();
			
			RecordMontgomery[] tempRecords = null;
			if (howManyOriginalRecordsFiles==1) {
				tempRecords = gson.fromJson(new FileReader(jsonFile), RecordMontgomery[].class);
				for (int i = 0; i < tempRecords.length; i++) {
					recordsDB.add(tempRecords[i]);
				}
			} else {
				for (int batch = 1; batch <= howManyOriginalRecordsFiles; batch++) {
					String batchFileName = jsonFileName.substring(0,jsonFileName.indexOf(".")) + " " + batch + ".json";
					File batchFile = new File(batchFileName);
					tempRecords = gson.fromJson(new FileReader(batchFile), RecordMontgomery[].class);
					for (int i = 0; i < tempRecords.length; i++) {
						recordsDB.add(tempRecords[i]);
					}
				}
			}
			
//			System.out.println(recordsDB.size());
			
			
			
			Iterator<RecordMontgomery> it = recordsDB.iterator();
			while (it.hasNext()) {
				RecordMontgomery r = it.next();
				ExperimentalRecord er = r.toExperimentalRecord();
				recordsExperimental.add(er);
			}
			
			Hashtable<String,ExperimentalRecords> htER = recordsExperimental.createExpRecordHashtableByCAS(ExperimentalConstants.str_L_KG,true);
			
			
//			System.out.println(gson.toJson(htER.get("29091-21-2")));
			
			
//			ExperimentalRecords recs=htER.get("29091-21-2");
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
		ParseMontgomery p = new ParseMontgomery();

		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();

		
	}
}