package gov.epa.exp_data_gathering.parse.USDA_Pesticide_Property_DB;



import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.epa.api.Chemical;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.LiteratureSource;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.PublicSource;
import gov.epa.exp_data_gathering.parse.Koc.RecordKoc;
import kong.unirest.json.JSONObject;

public class ParsePesticidePropertyDB extends Parse {
	
	public ParsePesticidePropertyDB() {
		sourceName = RecordPesticidePropertyDB.sourceName;
		this.init();
	}
	@Override
	protected void createRecords() {

		if(generateOriginalJSONRecords) {
			List<RecordPesticidePropertyDB> records = RecordPesticidePropertyDB.parseChemicalTextFile();
			writeOriginalRecordsToFile(records);
		}
	}
	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		
		try {
			String mainFolder="data\\experimental\\";
			String strFolder=mainFolder+RecordPesticidePropertyDB.sourceName+"\\";
			//Now using convenience method that works for any Record class:
			List<RecordPesticidePropertyDB> recordsDB=getOriginalRecordsFromJsonFiles(strFolder, RecordPesticidePropertyDB[].class);
			System.out.println(recordsDB.size());
			
			Iterator<RecordPesticidePropertyDB> it = recordsDB.iterator();
			while (it.hasNext()) {
				RecordPesticidePropertyDB r = it.next();
				ExperimentalRecord er = r.toExperimentalRecord();
				recordsExperimental.add(er);
			}
			
			Hashtable<String,ExperimentalRecords> htER = recordsExperimental.createExpRecordHashtableByCAS(ExperimentalConstants.str_L_KG,true);

			//			System.out.println(gson.toJson(htER.get("29091-21-2")));
			
			
			ExperimentalRecords recs=htER.get("29091-21-2");
			double median=ExperimentalRecords.calculateMedian(recs, true);
			
//			System.out.println("29091-21-2\t"+median);
			
			
			
			Hashtable<String,Double>htMedian=ExperimentalRecords.calculateMedian(htER, true);
			
//			System.out.println(htMedian.size()+"\n");
//			
//			for(String key:htMedian.keySet()) {
//				System.out.println(key+"|"+htMedian.get(key));
//			}
			

			
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return recordsExperimental;
	}
	
	
	public static void main(String[] args) {
		ParsePesticidePropertyDB p = new ParsePesticidePropertyDB();

		p.generateOriginalJSONRecords=true;
		p.removeDuplicates=false;//dont know which one is right
		p.writeCheckingExcelFile=false;
		p.createFiles();

		
	}



}
