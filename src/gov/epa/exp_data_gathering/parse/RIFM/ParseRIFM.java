package gov.epa.exp_data_gathering.parse.RIFM;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.google.gson.JsonObject;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;

public class ParseRIFM extends Parse {

	public ParseRIFM() {
		sourceName = "RIFM"; // TODO Consider creating ExperimentalConstants.strSourceRIFM instead.
		this.init();

		// TODO Is this a toxicity source? If so, rename original and experimental records files here.
	}

	@Override
	protected void createOriginalRecords() {
		Vector<JsonObject> records = RecordRIFM.parseRIFMRecordsFromExcel();
		
		for(JsonObject record:records) {
			System.out.println(record.get("Chemical_name").getAsString());
		}
		
		writeOriginalRecordsToFile(records);
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		try {
			String jsonFileName = jsonFolder + File.separator + fileNameJSON_Records;
			File jsonFile = new File(jsonFileName);
			List<RecordRIFM> recordsRIFM = new ArrayList<RecordRIFM>();
			RecordRIFM[] tempRecords = null;
			if (howManyOriginalRecordsFiles==1) {
				tempRecords = gson.fromJson(new FileReader(jsonFile), RecordRIFM[].class);
				for (int i = 0; i < tempRecords.length; i++) {
					recordsRIFM.add(tempRecords[i]);
				}
			} else {
				for (int batch = 1; batch <= howManyOriginalRecordsFiles; batch++) {
					String batchFileName = jsonFileName.substring(0,jsonFileName.indexOf(".")) + " " + batch + ".json";
					File batchFile = new File(batchFileName);
					tempRecords = gson.fromJson(new FileReader(batchFile), RecordRIFM[].class);
					for (int i = 0; i < tempRecords.length; i++) {
						recordsRIFM.add(tempRecords[i]);
					}
				}
			}

			Iterator<RecordRIFM> it = recordsRIFM.iterator();
			while (it.hasNext()) {
				RecordRIFM r = it.next();
				addExperimentalRecord(r,recordsExperimental);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return recordsExperimental;
	}

	private void addExperimentalRecord(RecordRIFM r,
				ExperimentalRecords recordsExperimental) {
		ExperimentalRecord er=r.toExperimentalRecord();
		recordsExperimental.add(er);
	}

	public static void main(String[] args) {
		ParseRIFM p = new ParseRIFM();
		
		p.generateOriginalJSONRecords=true;
		
		p.removeDuplicates=false;
		
		p.writeJsonExperimentalRecordsFile=true;
		
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
		
	}
}