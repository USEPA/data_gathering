package gov.epa.exp_data_gathering.parse.EPISUITE;

import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.ParseUtilities;

public class ParseEpisuiteISIS extends Parse {
	
	public ParseEpisuiteISIS() {
		sourceName = ExperimentalConstants.strSourceEpisuiteISIS;
		this.init();
	}
	
	@Override
	protected void createOriginalRecords() {
		Vector<RecordEpisuiteISIS> records = RecordEpisuiteISIS.getRecordsFromSDFs();
		
		System.out.println(records.size());
		
		writeOriginalRecordsToFile(records);
	}
	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		
		try {
			String jsonFileName = jsonFolder + File.separator + fileNameJSON_Records;
			File jsonFile = new File(jsonFileName);
			
			List<RecordEpisuiteISIS> recordsEpisuiteISIS = new ArrayList<RecordEpisuiteISIS>();
			RecordEpisuiteISIS[] tempRecords = null;
			if (howManyOriginalRecordsFiles==1) {
				tempRecords = gson.fromJson(new FileReader(jsonFile), RecordEpisuiteISIS[].class);
				for (int i = 0; i < tempRecords.length; i++) {
					recordsEpisuiteISIS.add(tempRecords[i]);
				}
			} else {
				for (int batch = 1; batch <= howManyOriginalRecordsFiles; batch++) {
					String batchFileName = jsonFileName.substring(0,jsonFileName.indexOf(".")) + " " + batch + ".json";
					File batchFile = new File(batchFileName);
					tempRecords = gson.fromJson(new FileReader(batchFile), RecordEpisuiteISIS[].class);
					for (int i = 0; i < tempRecords.length; i++) {
						recordsEpisuiteISIS.add(tempRecords[i]);
					}
				}
			}
			
			Iterator<RecordEpisuiteISIS> it = recordsEpisuiteISIS.iterator();
			while (it.hasNext()) {
				RecordEpisuiteISIS r = it.next();
				recordsExperimental.add(r.toExperimentalRecord());
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return recordsExperimental;
	}
	
	
	
	
		

	
	public static void main(String[] args) {
		ParseEpisuiteISIS p = new ParseEpisuiteISIS();
		p.generateOriginalJSONRecords = true;
		p.createFiles();
	}

	
	
}
