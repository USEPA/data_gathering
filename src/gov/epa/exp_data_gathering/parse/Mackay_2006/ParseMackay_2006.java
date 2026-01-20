package gov.epa.exp_data_gathering.parse.Mackay_2006;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.google.gson.JsonObject;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.Koc.RecordKoc;

/**
 * This class uses Weston Murdock's parsing of the mackay pdfs. 
 * Note: It's not complete- see pf_data_gathering project
 * 
 */

public class ParseMackay_2006 extends Parse {

	public ParseMackay_2006() {
		sourceName = "Mackay_2006"; // TODO Consider creating ExperimentalConstants.strSourceMackay_2006 instead.
		this.init();

		// TODO Is this a toxicity source? If so, rename original and experimental records files here.
	}

	@Override
	protected void createRecords() {
		
		if(generateOriginalJSONRecords) {
			//done with python
		}
	}

	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords recordsExperimental=new ExperimentalRecords();
		try {
			
			String mainFolder="data\\experimental\\";
			String strFolder=mainFolder+RecordMackay_2006.sourceName+"\\";
			
			//Now using convenience method that works for any Record class:
			List<RecordMackay_2006> recordsOriginal=getOriginalRecordsFromJsonFiles(strFolder, RecordMackay_2006[].class);
			Iterator<RecordMackay_2006> it = recordsOriginal.iterator();
			
//			System.out.println(recordsOriginal.size());
			
			while (it.hasNext()) {
				RecordMackay_2006 r = it.next();
				
//				System.out.println(RecordMackay2.gson.toJson(r));
				
				
				ExperimentalRecords ers=r.toExperimentalRecords();
				if(ers!=null)				
					recordsExperimental.addAll(ers);
			
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return recordsExperimental;
	}


	public static void main(String[] args) {
		ParseMackay_2006 p = new ParseMackay_2006();
		
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=false;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
		
	}
}