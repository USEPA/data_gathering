package gov.epa.exp_data_gathering.parse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JSONUtilities {

	public static int batchAndWriteJSON(Vector<?> records, String baseFileName) {
		return batchAndWriteJSON(records, baseFileName,100000 );
	}
	
	
	public static int batchAndWriteJSON(Vector<?> records, String baseFileName, int batchSize) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting().disableHtmlEscaping().serializeSpecialFloatingPointValues();
		Gson gson = builder.create();
		int batch = 0;
		
		if (records.size() <= batchSize) {
			String jsonRecords = gson.toJson(records);
			writeJSONLineByLine(jsonRecords,baseFileName);
			batch = 1;
		} else {
			List<Object> temp = new ArrayList<Object>();
			Iterator<?> it = records.iterator();
			int i = 0;
			while (it.hasNext()) {
				temp.add(it.next());
				i++;
				if (i!=0 && i%batchSize==0) {
					batch++;
					String batchFileName = baseFileName.substring(0,baseFileName.indexOf(".json")) + " " + batch + ".json";
					String jsonRecords = gson.toJson(temp);
					writeJSONLineByLine(jsonRecords,batchFileName);
					temp.clear();
				}
			}
			batch++;
			String batchFileName = baseFileName.substring(0,baseFileName.indexOf(".json")) + " " + batch + ".json";
			String jsonRecords = gson.toJson(temp);
			writeJSONLineByLine(jsonRecords,batchFileName);
		}
		
		return batch;
	}
	
	
	public static int batchAndWriteJSON(List<?> records, String baseFileName) {
		return batchAndWriteJSON(records, baseFileName,100000 );
	}

	
	public static int batchAndWriteJSON(List<?> records, String baseFileName, int batchSize) {
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting().disableHtmlEscaping().serializeSpecialFloatingPointValues();
		Gson gson = builder.create();
		int batch = 0;
		
		if (records.size() <= batchSize) {
			String jsonRecords = gson.toJson(records);
			writeJSONLineByLine(jsonRecords,baseFileName);
			batch = 1;
		} else {
			List<Object> temp = new ArrayList<Object>();
			Iterator<?> it = records.iterator();
			int i = 0;
			while (it.hasNext()) {
				temp.add(it.next());
				i++;
				if (i!=0 && i%batchSize==0) {
					batch++;
					String batchFileName = baseFileName.substring(0,baseFileName.indexOf(".json")) + " " + batch + ".json";
					String jsonRecords = gson.toJson(temp);
					writeJSONLineByLine(jsonRecords,batchFileName);
					temp.clear();
				}
			}
			batch++;
			String batchFileName = baseFileName.substring(0,baseFileName.indexOf(".json")) + " " + batch + ".json";
			String jsonRecords = gson.toJson(temp);
			writeJSONLineByLine(jsonRecords,batchFileName);
		}
		
		return batch;
	}

	private static void writeJSONLineByLine(String jsonRecords,String filePath) {
		String[] strRecords = jsonRecords.split("\n");
		
		File file = new File(filePath);
		if(!file.getParentFile().exists()) { file.getParentFile().mkdirs(); }
		
		try {
			// Clear existing file contents
			FileWriter fw = new FileWriter(filePath);
			fw.close();
			
			BufferedWriter bwAppend = new BufferedWriter(new FileWriter(filePath,true));
		
			for (String s:strRecords) {
				s=TextUtilities.fixChars(s);
				bwAppend.write(s+"\n");
			}
			bwAppend.flush();
			bwAppend.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
