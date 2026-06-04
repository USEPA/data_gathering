/**
 * 
 */
package gov.epa.exp_data_gathering.parse.Burkhard;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import gov.epa.QSAR.utilities.JsonUtilities;
import gov.epa.api.ExperimentalConstants;
import gov.epa.exp_data_gathering.parse.ExperimentalRecord;
import gov.epa.exp_data_gathering.parse.ExperimentalRecords;
import gov.epa.exp_data_gathering.parse.Parse;
import gov.epa.exp_data_gathering.parse.Burkhard.RecordBurkhard.Species;

/**
* @author TMARTI02
* 
* This version extends Parse class
* 
*/
/**
 * 
 */
public class ParseBurkhard2 extends Parse {
	
	String propertyName=null;
	
	public ParseBurkhard2(String propertyName) {
		this.propertyName=propertyName;
		sourceName = RecordBurkhard.sourceName; // TODO Consider creating ExperimentalConstants.strSourceQSAR_ToolBox instead.
		this.init(propertyName);
	}
	
	
	@Override
	protected void createRecords() {
		if(generateOriginalJSONRecords) {
			
			Vector<JsonObject> records = RecordBurkhard.parseBurkhardRecordsFromExcel();
			
			System.out.println(records.size());
			
			if(records!=null) 
				writeOriginalRecordsToFile(records);
		}
	}

	
	
	@Override
	protected ExperimentalRecords goThroughOriginalRecords() {
		ExperimentalRecords experimentalRecords=new ExperimentalRecords();

		
		try {

			Type type = new TypeToken<Hashtable<String, List<RecordBurkhard.Species>>>(){}.getType();

			List<RecordBurkhard>tempRecords=getOriginalRecordsFromJsonFiles(jsonFolder,
					RecordBurkhard[].class, StandardCharsets.UTF_8.name());

			Charset ansi=java.nio.charset.Charset.forName("windows-1252");
			FileReader fr=new FileReader("data\\experimental\\Arnot 2006\\htSuperCategory.json", ansi);
			Hashtable<String, List<Species>>htSpecies=JsonUtilities.gsonPretty.fromJson(fr, type);

			
			for (RecordBurkhard rb:tempRecords) {
				
				if(propertyName.toLowerCase().contains("bioconcentration")) {

					ExperimentalRecord erKinetic=rb.toExperimentalRecordBCF_Kinetic(propertyName, htSpecies);
					if(erKinetic!=null)	experimentalRecords.add(erKinetic);
					
					ExperimentalRecord erSS=rb.toExperimentalRecordBCF_SS(propertyName, htSpecies);
					if(erSS!=null)	experimentalRecords.add(erSS);


				} else if(propertyName.toLowerCase().contains("bioaccumulation")) {
					ExperimentalRecord er=rb.toExperimentalRecordBAF(propertyName, htSpecies);
					if(er!=null) experimentalRecords.add(er);
				}

			}
			
			Hashtable<String, ExperimentalRecords> htER = experimentalRecords.createExpRecordHashtableBySID(ExperimentalConstants.str_L_KG);
			ExperimentalRecords.calculateAvgStdDevOverAllChemicals(htER, true);

			
//			System.out.println("Other endpoints="+JsonUtilities.gsonPretty.toJson(RecordEChemPortal.endpoints));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return experimentalRecords;
	}
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ParseBurkhard2 p = new ParseBurkhard2(ExperimentalConstants.strBCF);
		
		p.generateOriginalJSONRecords=false;
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();
		
		p = new ParseBurkhard2(ExperimentalConstants.strBAF);
		p.generateOriginalJSONRecords=true;
		p.removeDuplicates=false;
		p.writeJsonExperimentalRecordsFile=true;
		p.writeExcelExperimentalRecordsFile=true;
		p.writeExcelFileByProperty=true;		
		p.writeCheckingExcelFile=false;//creates random sample spreadsheet
		p.createFiles();

	}

}
